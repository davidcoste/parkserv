package com.dcoste.parkserv.error;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class to handle global error attributes
 */
@Component
public class ParkservErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Throwable error = getError(request);
        // In case he error is an handled ResponseStatus exception the error will only contain a status and a message
        if (error instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) error;
            Map<String, Object> errorAttributes = new LinkedHashMap<>();
            errorAttributes.put("code", responseStatusException.getStatus().value());
            errorAttributes.put("msg", responseStatusException.getMessage());
            return errorAttributes;
        } else {
            Map<String, Object> errorAttributes = super.getErrorAttributes(request, includeStackTrace);
            errorAttributes.put("code", errorAttributes.getOrDefault("status", 404));
            errorAttributes.put("msg", error.getMessage());
            return errorAttributes;
        }
    }
}
