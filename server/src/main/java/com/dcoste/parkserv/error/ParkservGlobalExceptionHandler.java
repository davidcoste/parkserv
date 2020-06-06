package com.dcoste.parkserv.error;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * Global error handling
 */
@Component
@Order(-2)
public class ParkservGlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public ParkservGlobalExceptionHandler(ParkservErrorAttributes errorAttributes,
                                          ResourceProperties resourceProperties,
                                          ApplicationContext applicationContext,
    ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        setMessageWriters(serverCodecConfigurer.getWriters()) ;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(
            ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, false);
        return ServerResponse
                .status(Integer.parseInt(errorPropertiesMap.getOrDefault("code", 500).toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}
