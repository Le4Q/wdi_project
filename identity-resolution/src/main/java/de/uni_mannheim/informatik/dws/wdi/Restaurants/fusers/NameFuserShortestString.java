package de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.ShortestString;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

import java.util.List;

public class NameFuserShortestString extends
        AttributeValueFuser<String, Restaurant, Attribute> {

    public NameFuserShortestString() {
        super(new ShortestString<Restaurant, Attribute>());
    }


    @Override
    public String getValue(Restaurant restaurant, Correspondence<Attribute, Matchable> correspondence) {
        return restaurant.getName();
    }

    @Override
    public void fuse(RecordGroup<Restaurant, Attribute> recordGroup, Restaurant fusedRecord, Processable<Correspondence<Attribute, Matchable>> processable, Attribute attribute) {
        FusedValue<String, Restaurant, Attribute> fused = getFusedValue(recordGroup, processable, attribute);
        fusedRecord.setName(fused.getValue());
    }

    @Override
    public boolean hasValue(Restaurant restaurant, Correspondence<Attribute, Matchable> correspondence) {
        return restaurant.hasValue(Restaurant.NAME);
    }
}
