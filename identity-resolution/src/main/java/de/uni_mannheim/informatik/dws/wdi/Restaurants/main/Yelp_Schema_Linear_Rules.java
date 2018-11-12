package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByPostalCodeGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.ComparatorUtils;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantAddressComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantCityNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.CSVRestaurantDetailFormatter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Iterator;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;


public class Yelp_Schema_Linear_Rules {

    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception {

        // load the data sets
        HashedDataSet<Restaurant, Attribute> schemaOrg = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/schemaOrg_target.xml"), "/restaurants/restaurant", schemaOrg);
        HashedDataSet<Restaurant, Attribute> schema = Utils.removeDuplicates("data/output/schemaOrg_duplicates.csv", schemaOrg);
        System.out.println(schema.size());

        HashedDataSet<Restaurant, Attribute> yelp = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/yelp_target.xml"), "/restaurants/restaurant", yelp);

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByPostalCodeGenerator());
        blocker.setMeasureBlockSizes(true);
        blocker.collectBlockSizeData("data/output/gs_yelp_schema_debugBlocking.csv", 100);

        ArrayList<Double> weights = new ArrayList<Double>();
        weights.add(0.4);
        weights.add(0.3);
        weights.add(0.3);

        ArrayList<Function<String,String>> prep = new ArrayList<Function<String,String>>();
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::cleanLower);

        /*
        ################
        #### Preprocessing: Clean, Lower
        #### Different thresholds
        ################
         */
        /*
        Precision: 0.8000
        Recall: 0.8000
        F1: 0.8000
         */
        // simpleTemplate(schema,yelp,0.75,weights,prep,blocker);

        /*
        Precision: 1.0000
        Recall: 0.6000
        F1: 0.7500
         */
        // simpleTemplate(schema,yelp,0.8,weights,prep,blocker);

        /*
        Precision: 0.9333
        Recall: 0.7000
        F1: 0.8000
         */
        // simpleTemplate(schema,yelp,0.78,weights,prep,blocker);

        /*
        ################
        #### Preprocessing: Remove Stopwords
        #### Different thresholds
        ################
         */
        prep = new ArrayList<Function<String,String>>();
        prep.add(ComparatorUtils::cleanLowerStopwords);
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::cleanLower);

        /*
        Precision: 0.8000
        Recall: 0.8000
        F1: 0.8000
         */
        // simpleTemplate(schema,yelp,0.75,weights,prep,blocker);


        /*
        ################
        #### Preprocessing: Remove city names from restaurant name
        #### Different thresholds
        ################
         */
        prep = new ArrayList<Function<String,String>>();
        prep.add(ComparatorUtils::removeCityName);
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::cleanLower);

        /*
        Precision: 0.8182
        Recall: 0.9000
        F1: 0.8571
         */
        // simpleTemplate(schema,yelp,0.75,weights,prep,blocker, true);


        /*
        Precision: 0.9444
        Recall: 0.8500
        F1: 0.8947
         */
        // simpleTemplate(schema,yelp,0.78,weights,prep,blocker, true);

        /*
        Precision: 1.0000
        Recall: 0.8000
        F1: 0.8889
         */
        // simpleTemplate(schema,yelp,0.79,weights,prep,blocker, true);

        /*
        Precision: 1.0000
        Recall: 0.7500
        F1: 0.8571
         */
        // simpleTemplate(schema,yelp,0.8,weights,prep,blocker,true);
        /*
        ################
        #### Preprocessing: Unify address
        #### Different thresholds
        ################
         */
        prep = new ArrayList<Function<String,String>>();
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::unifyAddress);
        /*
        Precision: 0.8095
        Recall: 0.8500
        F1: 0.8293
         */
        // simpleTemplate(schema,yelp,0.75,weights,prep,blocker);

        /*
        Precision: 0.9375
        Recall: 0.7500
        F1: 0.8333
         */
        // simpleTemplate(schema,yelp,0.8,weights,prep,blocker);



        /*
        ################
        #### Preprocessing: Remove city names from restaurant name + unify address
        #### Different thresholds
        ################
         */
        prep = new ArrayList<Function<String,String>>();
        prep.add(ComparatorUtils::removeCityName);
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::unifyAddress);

        /*
        Precision: 0.8182
        Recall: 0.9000
        F1: 0.8571
         */
        // simpleTemplate(schema,yelp,0.75,weights,prep,blocker, true);

        /*
        Precision: 0.9444
        Recall: 0.8500
        F1: 0.8947
         */
        simpleTemplate(schema,yelp,0.80,weights,prep,blocker, true);


        weights = new ArrayList<Double>();
        weights.add(0.3);
        weights.add(0.2);
        weights.add(0.5);

    }

    public static LinearCombinationMatchingRule<Restaurant,Attribute> getMatchingRule(ArrayList<Comparator<Restaurant,Attribute>> fns,
                                                                                      Double threshold,
                                                                                      ArrayList<Double> weights) throws Exception{
        // simple matching rule to create the gold standard
        LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule = new LinearCombinationMatchingRule<>(threshold);

        // add comparators
        Iterator<Comparator<Restaurant,Attribute>> it_fn = fns.iterator();
        Iterator<Double> it_weights = weights.iterator();

        Double weight;
        Comparator<Restaurant,Attribute> fn;
        while (it_fn.hasNext() && it_weights.hasNext()) {
            fn = it_fn.next();
            weight = it_weights.next();
            matchingRule.addComparator(fn, weight);
        }

        return matchingRule;
    }

    public static void simpleTemplate(HashedDataSet<Restaurant, Attribute> data1,
                                      HashedDataSet<Restaurant, Attribute> data2,
                                      Double threshold,
                                      ArrayList<Double> weights,
                                      ArrayList<Function<String,String>> prep,
                                      StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker) throws Exception {
        ArrayList<Comparator<Restaurant,Attribute>> fns = new ArrayList<Comparator<Restaurant,Attribute>>();

        fns.add(new RestaurantNameComparatorLevenshtein(prep.get(0)));
        fns.add(new RestaurantCityNameComparatorLevenshtein(prep.get(1)));
        fns.add(new RestaurantAddressComparatorLevenshtein(prep.get(2)));

        LinearCombinationMatchingRule<Restaurant,Attribute> matchingRule = getMatchingRule(fns, threshold, weights);

        findCorrespondencesAndEvaluate(data1,data2,matchingRule,blocker,"simple");
    }

    public static void simpleTemplate(HashedDataSet<Restaurant, Attribute> data1,
                                      HashedDataSet<Restaurant, Attribute> data2,
                                      Double threshold,
                                      ArrayList<Double> weights,
                                      ArrayList<Function<String,String>> prep,
                                      StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker,
                                      boolean removeCityName) throws Exception {
        ArrayList<Comparator<Restaurant,Attribute>> fns = new ArrayList<Comparator<Restaurant,Attribute>>();

        fns.add(new RestaurantNameComparatorLevenshtein(prep.get(0), removeCityName));
        fns.add(new RestaurantCityNameComparatorLevenshtein(prep.get(1)));
        fns.add(new RestaurantAddressComparatorLevenshtein(prep.get(2)));

        LinearCombinationMatchingRule<Restaurant,Attribute> matchingRule = getMatchingRule(fns, threshold, weights);

        findCorrespondencesAndEvaluate(data1,data2,matchingRule,blocker,"simple");
    }

    public static void findCorrespondencesAndEvaluate(HashedDataSet<Restaurant, Attribute> data1,
                                                      HashedDataSet<Restaurant, Attribute> data2,
                                                      LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule,
                                                      StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker,
                                                      String fileName) throws Exception{

        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // execute matching
        System.out.println("*\n*\tRunning identity resolution\n*");
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(
                data1, data2, null, matchingRule, blocker
        );

        // write correspondences to output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/"+fileName+".csv"), correspondences);
        new CSVRestaurantDetailFormatter().writeCSV(new File("data/output/"+fileName+"_detail.csv"), correspondences);


        // load the gold standard (test set)
        System.out.println("*\n*\tLoading gold standard\n*");
        MatchingGoldStandard gsTest = new MatchingGoldStandard();
        gsTest.loadFromCSVFile(new File(
                "data/goldstandard/yelp_schema_gs_test.csv"));

        System.out.println("*\n*\tEvaluating result\n*");
        // evaluate your result
        MatchingEvaluator<Restaurant, Attribute> evaluator = new MatchingEvaluator<Restaurant, Attribute>();
        Performance perfTest = evaluator.evaluateMatching(correspondences,
                                                          gsTest);

        // print the evaluation result
        System.out.println("Schema <-> Yelp");
        System.out.println(String.format(
                "Precision: %.4f",perfTest.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTest.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTest.getF1()));
    }

}
