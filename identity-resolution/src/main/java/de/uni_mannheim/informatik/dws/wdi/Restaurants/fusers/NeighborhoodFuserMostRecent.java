package de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class NeighborhoodFuserMostRecent extends
        AttributeValueFuser<String, Restaurant, Attribute> {

    public NeighborhoodFuserMostRecent() {
        super(new MostRecent<String, Restaurant, Attribute>());
    }


    @Override
    public String getValue(Restaurant restaurant, Correspondence<Attribute, Matchable> correspondence) {
        return restaurant.getNeighborhood();
    }

    @Override
    public void fuse(RecordGroup<Restaurant, Attribute> recordGroup, Restaurant fusedRecord, Processable<Correspondence<Attribute, Matchable>> processable, Attribute attribute) {
        FusedValue<String, Restaurant, Attribute> fused = getFusedValue(recordGroup, processable, attribute);
        fusedRecord.setNeighborhood(fused.getValue());
    }

    @Override
    public boolean hasValue(Restaurant restaurant, Correspondence<Attribute, Matchable> correspondence) {
        return restaurant.hasValue(Restaurant.NEIGHBORHOOD);
    }
}
