package com.dcoste.parkserv.rest;


import com.dcoste.parkserv.model.CarParkResponse;
import com.dcoste.parkserv.service.CarParkRetrieverService;
import static org.apache.lucene.util.SloppyMath.haversinMeters;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.Comparator;

/**
 * Rest controller responsible for serving the carpark list with distance in a unified format
 */
@RestController
@RequestMapping("/parkserv")
public class ParkServController {

    /**
     * The service that will query the external API
     */
    private CarParkRetrieverService carParkRetrieverService;

    /**
     * Constructor for this ParkservController
     * @param carParkRetrieverService the underlying carParkRetriever service (no need to autowire)
     */
    public ParkServController(CarParkRetrieverService carParkRetrieverService) {
        this.carParkRetrieverService = carParkRetrieverService;
    }

    /**
     * Method to get the list of car parks with the distance from the provided position
     * @param latitude latitude of the reference position
     * @param longitude longitude of the reference position
     * @return the list of car parks with the distance from the reference position ordered by the distance
     */
    @GetMapping("/v1_0/carparks")
    public Flux<CarParkResponse> getParkingList(@RequestParam Double latitude, @RequestParam Double longitude) {
        // check that longitude and latitude are correct
        if (latitude == null || longitude == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both latitude and longitude must be provided") ;
        } else if  (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "latitude must be in the range -90 to +90, longitude must be in the range -180 to +180");
        }
        return carParkRetrieverService.getCarParks()
                .map(p -> new CarParkResponse(p, haversinMeters(latitude, longitude, p.getLatitude(), p.getLongitude())))
                .sort(Comparator.comparing(CarParkResponse::getDistance));
    }
}
