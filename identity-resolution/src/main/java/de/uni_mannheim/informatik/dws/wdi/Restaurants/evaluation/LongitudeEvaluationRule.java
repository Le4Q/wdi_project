package de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class LongitudeEvaluationRule extends EvaluationRule<Restaurant, Attribute> {


    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Attribute attribute) {
        if(record1.getLongitude() == 0.0 && record2.getLongitude() == 0.0)
            return true;
        else if(record1.getLongitude() == 0.0 ^ record2.getLongitude() == 0.0)
            return false;
        else {
            double delta = record1.getLongitude() - record2.getLongitude();
            return delta < 0.05 && delta > -0.05;
        }
    }

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> correspondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}
