package com.dcoste.parkserv.service;

import com.dcoste.parkserv.config.CarParkRetrieverServiceConfigurer;
import com.dcoste.parkserv.config.RetrieverServiceConfiguration;
import com.dcoste.parkserv.model.CarPark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;


/**
 * Implementation of the parking retriever service with a cache. This service is aimed to provide a list of car parks
 * from an external data source
 */
@Component
public class CachedCarParkRetrieverService implements CarParkRetrieverService {

    Logger LOG = LoggerFactory.getLogger(CachedCarParkRetrieverService.class) ;
    /**
     * The name of the cache used as aky to get the results stored in cache
     */
    @Value("${retriever.service.cache.name}")
    private String cacheName ;

    /**
     * The web client object used to perform the requests to the external API
     */
    private WebClient webClient;
    /**
     * The current cache manager
     */
    CacheManager cacheManager ;
    /**
     * The ParkingRetrieverServiceConfigurer object that holds the configuration of the web client
     */
    private CarParkRetrieverServiceConfigurer configurer;

    /**
     * This constructor is the only one available. This is why Autowired annotation is not needed
     * @param configuration the retriever service configuration object
     */
    public CachedCarParkRetrieverService(RetrieverServiceConfiguration configuration) {
        this.configurer = configuration.getRetrieverServiceConfigurer() ;
        // some APIs send a content type that cannot be parsed by the spring webflux API, this is why we need to apply
        // a filtering that will replace the content type with the specified one if not null
        if (configurer.getDefaultHttpHeader() != null && configurer.getDefaultHttpHeader().length() > 0) {
            LOG.info(String.format("The Content-Type HTTP Header will be overwritten by %s", configurer.getDefaultHttpHeader())) ;
            ExchangeFilterFunction contentTypeFilter = ExchangeFilterFunction.ofResponseProcessor(clientResponse ->
                    Mono.just(ClientResponse.from(clientResponse)
                            .headers(httpHeaders -> httpHeaders.set(HttpHeaders.CONTENT_TYPE, configurer.getDefaultHttpHeader()))
                            .body(clientResponse.body(BodyExtractors.toDataBuffers()))
                            .build()));
            this.webClient = WebClient.builder().baseUrl(configuration.getBaseUrl()).filter(contentTypeFilter).build();
        }
        // if no default http header is specified we don't apply any filtering function
        else {
            LOG.info("No filtering needed") ;
            this.webClient = WebClient.builder().baseUrl(configuration.getBaseUrl()).build();
        }
    }


    /**
     * Get the car park list from the cache or from the external API if not present in the cache
     * @return the car park list
     */
    @Override
    public Flux<CarPark> getCarParks() {
        // this supplier materialize a call of the external API
        Supplier<Flux<CarPark>> callParkingRetriever = () -> webClient.get()
                        .retrieve()
                        .bodyToMono((Class<?>) configurer.getTargetType())
                        .flatMapIterable(configurer.getMapper()) ;

        // getting the cache
        Cache cache = cacheManager.getCache(cacheName);
        // if the cache does not exists
        if (cache == null) {
            LOG.warn(String.format("No cache found for name %s", cacheName));
            return callParkingRetriever.get();
        }
        else {
            return CacheFlux.lookup(k -> {
                    @SuppressWarnings("unchecked") List<CarPark> list = (List<CarPark>) cache.get(k, List.class);
                    if (list == null || list.size() == 0) {
                        LOG.info(String.format("No carpark list found in cache %s", cacheName)) ;
                        return Mono.empty();
                    }
                    return Flux.fromIterable(list).materialize().collectList();
                }, cacheName)
                .onCacheMissResume(callParkingRetriever)
                .andWriteWith((k, v) -> {
                        LOG.info(String.format("Storing the new car park list in cache %s", cacheName));
                        List<CarPark> carParkList = new ArrayList<>();
                        return Flux.fromIterable(v)
                                .dematerialize()
                                .cast(CarPark.class)
                                .doOnNext(carParkList::add)
                                .doOnComplete(() -> cache.put(cacheName, carParkList)).then(Mono.empty());
                    });
        }
    }

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager ;
    }

}