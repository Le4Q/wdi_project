package de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantNameComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

public class CountryEvaluationRule extends EvaluationRule<Restaurant, Attribute> {

    SimilarityMeasure sim = new TokenizingJaccardSimilarity();

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Attribute attribute) {
        if(record1.getPostalAddress().getCity().getCountry() == null && record2.getPostalAddress().getCity().getCountry() == null)
            return true;
        else if(record1.getPostalAddress().getCity().getCountry() == null ^ record2.getPostalAddress().getCity().getCountry() == null)
            return false;
        else
            return sim.calculate(record1.getPostalAddress().getCity().getCountry(), record2.getPostalAddress().getCity().getCountry()) > 0.7;
    }

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> correspondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}
