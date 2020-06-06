package com.dcoste.parkserv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Class that holds the configuration of the external API
 */
@Configuration
public class RetrieverServiceConfiguration {

    @Value("${retriever.service.base.url}")
    private String baseUrl ;

    /**
     * Configurer for the external application
     */
    CarParkRetrieverServiceConfigurer retrieverServiceConfigurer ;

    /**
     * Getter for the base URL of the external API
     * @return the base URL of the external API
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Getter for the external API configurer
     * @return the retrieverServiceConfigurer
     */
    public CarParkRetrieverServiceConfigurer getRetrieverServiceConfigurer() {
        return retrieverServiceConfigurer;
    }

    /**
     * Setter for the CarParkRetrieverServiceConfigurer
     * @param retrieverServiceConfigurer the configurer of this retriever service
     */
    @Autowired
    public void setRetrieverServiceConfigurer(CarParkRetrieverServiceConfigurer retrieverServiceConfigurer) {
        this.retrieverServiceConfigurer = retrieverServiceConfigurer;
    }
}
