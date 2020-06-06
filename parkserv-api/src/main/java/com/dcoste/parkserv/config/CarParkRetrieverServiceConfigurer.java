package com.dcoste.parkserv.config;

import com.dcoste.parkserv.model.CarPark;
//import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

/**
 * Interface to be implemented to hold the car park retriever service configuration
 */
public interface CarParkRetrieverServiceConfigurer {


    /**
     * @return the type of response topmost object
     */
    Type getTargetType() ;

    /**
     * @return the function that transforms the return type to a List of Parking
     */
    Function<Object, List<CarPark>> getMapper() ;

    /**
     * As we don't trust the client API to return a correct content type we force the content type to this value.
     * If null no substitution is done
     * @return the substitution HTTP header
     */
    String getDefaultHttpHeader() ;
}

