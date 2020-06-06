package com.dcoste.lacub;

import fr.bordeaux_metropole.data.wfs.FeatureCollectionType;
import org.junit.jupiter.api.Test;
import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FeatureCollectionParsingTest {

    @Test
    void parseResponse() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(FeatureCollectionType.class);
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("FeatureCollection.xml")) {
            Source src = new StreamSource(is);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<FeatureCollectionType> result = unmarshaller.unmarshal(src, FeatureCollectionType.class);
            FeatureCollectionType featureCollectionType = result.getValue();
            assertEquals(80, featureCollectionType.getFeatureMember().size(), "80 features found");
        }
    }
}
