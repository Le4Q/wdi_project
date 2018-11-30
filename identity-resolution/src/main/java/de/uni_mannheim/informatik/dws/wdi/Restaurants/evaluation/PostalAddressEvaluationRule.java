package de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.PostalAddress;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.dws.winter.similarity.string.GeneralisedStringJaccard;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

public class PostalAddressEvaluationRule extends EvaluationRule<Restaurant, Attribute> {

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Attribute attribute) {

        PostalAddress add1 = record1.getPostalAddress(), add2 = record2.getPostalAddress();
        
        SimilarityMeasure jaccard = new GeneralisedStringJaccard(new LevenshteinSimilarity(), 0.6, 0);
        SimilarityMeasure leven = new LevenshteinSimilarity();

        if(add1 == null && add2 == null)
            return true;
        else if(add1 == null ^ add2 == null)
            return false;
        else if(add1.getCity().getName() != null && add2.getCity().getName() != null
                && add1.getCity().getPostalCode() != null && add2.getCity().getPostalCode() != null
                && add1.getAddress() != null && add2.getAddress() != null) {

            //System.out.println(add1.getAddress() + " | " + add2.getAddress() + " | " + jaccard.calculate(addressFormat(add1.getAddress()), addressFormat(add2.getAddress())));
            if (leven.calculate(add1.getCity().getName().toLowerCase(), add2.getCity().getName().toLowerCase()) < 0.8)
                return false;
            else {
                if (add1.getCity().getPostalCode().substring(0,2).equals(add2.getCity().getPostalCode().substring(0,2))
                        && leven.calculate(add1.getCity().getPostalCode(), add2.getCity().getPostalCode()) < 0.6)
                    return false;
                else {


                    if (jaccard.calculate(addressFormat(add1.getAddress()), addressFormat(add2.getAddress())) < 0.6)
                        return false;
                    else {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    @Override
    public boolean isEqual(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> correspondence) {
        return isEqual(record1, record2, (Attribute)null);
    }

    public static String addressFormat(String add) {
        Hashtable<String, String> table = new Hashtable<>();
        table.put("N", "North");
        table.put("S", "South");
        table.put("E", "East");
        table.put("W", "West");
        table.put("St", "Street");
        table.put("Blvd", "Boulevard");
        table.put("Rd", "Road");
        table.put("Ave", "Avenue");
        table.put("Pkwy", "Parkway");
        table.put("Dr", "Drive");

        Enumeration<String> enumKey = table.keys();
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            String val = table.get(key);
            add = add.replace(val, key);
        }

        if(add.indexOf(",") > 0)
            add = add.substring(0, add.indexOf(","));

        return add;
    }

}
