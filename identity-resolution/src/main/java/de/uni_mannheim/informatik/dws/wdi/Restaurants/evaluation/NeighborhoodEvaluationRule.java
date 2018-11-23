package de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

public class NeighborhoodEvaluationRule extends EvaluationRule<Restaurant, Attribute> {

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Attribute attribute) {
        if(record1.getNeighborhood() == null && record2.getNeighborhood() == null)
            return true;
        else if(record1.getNeighborhood() == null ^ record2.getNeighborhood() == null)
            return false;
        else
            return (new TokenizingJaccardSimilarity()).calculate(record1.getNeighborhood(), record2.getNeighborhood()) > 0.8;
    }

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> correspondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}
