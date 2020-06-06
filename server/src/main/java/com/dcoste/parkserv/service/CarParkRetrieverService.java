package com.dcoste.parkserv.service;

import com.dcoste.parkserv.model.CarPark;
import reactor.core.publisher.Flux;

/**
 * Interface to be implemented for CarParkRetrieverService
 */
public interface CarParkRetrieverService {

    Flux<CarPark> getCarParks() ;

}
