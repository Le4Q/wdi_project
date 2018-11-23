package de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class OpeningHoursEvaluationRule extends EvaluationRule<Restaurant, Attribute> {

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Attribute attribute) {
        if(record1.getOpeninghours() == null && record2.getOpeninghours() == null)
            return true;
        else if(record1.getOpeninghours() == null ^ record2.getOpeninghours() == null)
            return false;
        else
            return record1.getOpeninghours().equals(record2.getOpeninghours());
    }

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> correspondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}
