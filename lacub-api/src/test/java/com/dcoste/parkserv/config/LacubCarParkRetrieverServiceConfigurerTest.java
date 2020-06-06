package com.dcoste.parkserv.config;

import com.dcoste.parkserv.model.CarPark;
import fr.bordeaux_metropole.data.wfs.FeatureCollectionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LacubCarParkRetrieverServiceConfigurerTest {

    private static Unmarshaller unmarshaller ;
    LacubCarParkRetrieverServiceConfigurer configurer = new LacubCarParkRetrieverServiceConfigurer() ;

    @BeforeAll
    public static void beforeAll() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(FeatureCollectionType.class);
        unmarshaller = jaxbContext.createUnmarshaller() ;
    }

    @Test
    void getTargetType() {
        assertEquals(FeatureCollectionType.class, configurer.getTargetType() ,"target type should be FeatureCollectionType.class") ;
    }

    @Test
    void getMapper() {
        assertNotNull(configurer.getMapper(), "get mapper should be provided");
    }

    @Test
    void getDefaultHttpHeader() {
        assertEquals("text/xml", configurer.getDefaultHttpHeader() ,"default http content type header should be text/xml") ;
    }

    @Test
    void testMapper() throws IOException, JAXBException {
        FeatureCollectionType f ;
        List<CarPark> list ;
        StreamSource src ;
        String fileName ;

        fileName = "fcOk.xml" ;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            f = unmarshaller.unmarshal(new StreamSource(is), FeatureCollectionType.class).getValue() ;
            list = configurer.getMapper().apply(f) ;
            assertNotNull(list, "A non null list should be returned") ;
            assertEquals(1, list.size(), "The list should contain one element");
            CarPark parkOK = list.get(0) ;
            assertEquals("PARK_OK_NAME", parkOK.getName(), "Car park name should be PARK_OK_NAME");
            assertEquals("PARK_OK_ADDRESS", parkOK.getAddress(), "Car park name should be PARK_OK_ADDRESS");
            assertEquals(242, parkOK.getCapacity(), "Car park capacity should be 242");
            assertEquals(236, parkOK.getFree(), "Car park free should be 236");
            assertEquals(44.888824, parkOK.getLatitude(), "Car park latitude should be 44.888824");
            assertEquals(-0.517676, parkOK.getLongitude(), "Car park longitude should be -0.517676");
        }

        fileName = "fcNoGeometry.xml" ;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            f = unmarshaller.unmarshal(new StreamSource(is), FeatureCollectionType.class).getValue();
            list = configurer.getMapper().apply(f);
            assertNotNull(list, "A non null list should be returned if no geometry element is provided");
            assertEquals(0, list.size(), "The list should contain zero element if no geometry element is provided");
        }

        fileName = "fcNoPos.xml" ;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            f = unmarshaller.unmarshal(new StreamSource(is), FeatureCollectionType.class).getValue();
            list = configurer.getMapper().apply(f);
            assertNotNull(list, "A non null list should be returned if no position is provided");
            assertEquals(0, list.size(), "The list should contain zero element if no position is provided");
        }
    }


    public static Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public static void setUnmarshaller(Unmarshaller unmarshaller) {
        LacubCarParkRetrieverServiceConfigurerTest.unmarshaller = unmarshaller;
    }
}