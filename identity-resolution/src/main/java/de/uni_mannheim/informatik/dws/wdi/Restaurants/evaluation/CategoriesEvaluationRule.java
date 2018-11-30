package de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CategoriesEvaluationRule extends EvaluationRule<Restaurant, Attribute> {

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Attribute attribute) {
        if(record1.getCategories() == null && record2.getCategories() == null)
            return true;
        else if(record1.getCategories() == null ^ record2.getCategories() == null)
            return false;
        else {
            //return record1.getCategories().containsAll(record2.getCategories()) &&
            //       record2.getCategories().containsAll(record1.getCategories());
            List<String> copy = record1.getCategories();
            copy.retainAll(record2.getCategories());
            copy.removeAll(Arrays.asList("", null));
            return copy.size() >= 1;
        }
    }

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> correspondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}
