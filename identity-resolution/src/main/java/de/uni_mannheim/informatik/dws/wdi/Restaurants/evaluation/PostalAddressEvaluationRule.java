package de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class PostalAddressEvaluationRule extends EvaluationRule<Restaurant, Attribute> {

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Attribute attribute) {
        if(record1.getPostalAddress() == null && record2.getPostalAddress() == null)
            return true;
        else if(record1.getPostalAddress() == null ^ record2.getPostalAddress() == null)
            return false;
        if(record1.getPostalAddress().getCity() == null && record2.getPostalAddress().getCity() == null)
            return true;
        else if(record1.getPostalAddress().getCity() == null ^ record2.getPostalAddress().getCity() == null)
            return false;
        else if(record1.getPostalAddress().getCity().getPostalCode() == null && record2.getPostalAddress().getCity().getPostalCode() == null)
            return true;
        else if(record1.getPostalAddress().getCity().getPostalCode() == null ^ record2.getPostalAddress().getCity().getPostalCode() == null)
            return false;
        else
            return record1.getPostalAddress().getCity().getPostalCode().equals(record2.getPostalAddress().getCity().getPostalCode());
    }

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> correspondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}
