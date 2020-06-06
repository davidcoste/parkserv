package com.dcoste.parkserv.config;

import com.dcoste.parkserv.model.CarPark;
import fr.bordeaux_metropole.data.wfs.DirectPositionType;
import fr.bordeaux_metropole.data.wfs.FeatureCollectionType;
import fr.bordeaux_metropole.data.wfs.PointType;
import fr.bordeaux_metropole.data.wfs.ST_PARK_PType;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LacubCarParkRetrieverServiceConfigurer implements CarParkRetrieverServiceConfigurer {

    Function<Object, List<CarPark>> mapper = obj -> {
        FeatureCollectionType featureCollection = (FeatureCollectionType) obj ;
        return Stream.of(featureCollection)
            // we want to be null safe at each step
            .filter(fc -> fc.getFeatureMember() != null)
            // we transform each feature collection to a stream of FeaturePropertyType (gml:featureMember)
            .flatMap(fc -> fc.getFeatureMember().stream())
            // we filter to get only the ones that have a sub element of type ST_PARK_Type
            .filter(f -> f.get_Feature() != null && f.get_Feature().getValue() instanceof ST_PARK_PType)
            // then we transform each one to an ST_PARK_Type
            .map(f -> (ST_PARK_PType) f.get_Feature().getValue())
            // we filter again because we cannot create a parking if some fields are missing
            .filter(stPark -> stPark.getGeometry() != null
                    && stPark.getGeometry().get_Geometry() != null
                    && stPark.getGeometry().get_Geometry().getValue() != null
                    && stPark.getGeometry().get_Geometry().getValue() instanceof PointType
                    && ((PointType) stPark.getGeometry().get_Geometry().getValue()).getPos() != null)
            // finally we map it to a parking
            .map(stPark -> {
                DirectPositionType pos = ((PointType) stPark.getGeometry().get_Geometry().getValue()).getPos();
                CarPark carPark = null;
                if (pos.getValue() != null && pos.getValue().size() == 2) {
                    carPark = new CarPark(
                            stPark.getNOM(),
                            stPark.getADRESSE(),
                            pos.getValue().get(0),
                            pos.getValue().get(1),
                            stPark.getTOTAL() != null ? stPark.getTOTAL().intValue() : 0,
                            stPark.getLIBRES() != null ? stPark.getLIBRES().intValue() : 0);
                }
                return carPark;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    };

    @Override
    public Type getTargetType() {
        return FeatureCollectionType.class;
    }

    @Override
    public Function<Object, List<CarPark>> getMapper() {
        return mapper;
    }

    @Override
    public String getDefaultHttpHeader() {
        return "text/xml";
    }
}
