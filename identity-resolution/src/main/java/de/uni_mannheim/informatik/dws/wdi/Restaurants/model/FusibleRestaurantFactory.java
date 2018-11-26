package de.uni_mannheim.informatik.dws.wdi.Restaurants.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class FusibleRestaurantFactory implements FusibleFactory<Restaurant, Attribute> {

    @Override
    public Restaurant createInstanceForFusion(RecordGroup<Restaurant, Attribute> cluster) {

        List<String> ids = new LinkedList<>();

        for (Restaurant m : cluster.getRecords()) {
            ids.add(m.getIdentifier());
        }

        Collections.sort(ids);

        String mergedId = StringUtils.join(ids, '+');

        return new Restaurant(mergedId, "fused");
    }

}
