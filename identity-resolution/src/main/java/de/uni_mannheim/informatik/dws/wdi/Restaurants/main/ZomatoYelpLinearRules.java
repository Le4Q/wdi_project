package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCategoryGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCityNameFirstFiveGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCityNameFullGenerator;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;


public class ZomatoYelpLinearRules {

    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception {

        // load the data sets
        HashedDataSet<Restaurant, Attribute> zomato = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);

        HashedDataSet<Restaurant, Attribute> yelp = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/yelp_target.xml"), "/restaurants/restaurant", yelp);

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByCityNameFullGenerator());
        blocker.setMeasureBlockSizes(true);
        blocker.collectBlockSizeData("data/output/gs_zomato_yelp_debugBlocking.csv", 100);

        double nameWeight = 0.4;
        double addrWeight = 1 - nameWeight;
        double threshold = 0.75;

        ArrayList<Function<String, String>> prep = new ArrayList<Function<String, String>>();
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::cleanLowerStopwords);

        ArrayList<Double> weights = new ArrayList<Double>();
        weights.add(nameWeight);
        weights.add(addrWeight);

        String expResults = simpleTemplate(zomato, yelp, threshold, weights, prep, blocker);
        System.out.println(expResults);

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

    public static String simpleTemplate(HashedDataSet<Restaurant, Attribute> data1,
                                        HashedDataSet<Restaurant, Attribute> data2,
                                        Double threshold,
                                        ArrayList<Double> weights,
                                        ArrayList<Function<String,String>> prep,
                                        StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker) throws Exception {
        ArrayList<Comparator<Restaurant,Attribute>> fns = new ArrayList<Comparator<Restaurant,Attribute>>();

        fns.add(new RestaurantNameComparatorLevenshtein(prep.get(0)));
        fns.add(new RestaurantAddressComparatorLevenshtein(prep.get(1)));

        LinearCombinationMatchingRule<Restaurant,Attribute> matchingRule = getMatchingRule(fns, threshold, weights);

        return findCorrespondencesAndEvaluate(data1,data2,matchingRule,blocker,"zomato_yelp_correspondence_nameWeight_40_addrWeight_60_prep_cleanLower_cleanLowerstopwords_nameFull_blocker");

    }

    public static String findCorrespondencesAndEvaluate(HashedDataSet<Restaurant, Attribute> data1,
                                                        HashedDataSet<Restaurant, Attribute> data2,
                                                        LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule,
                                                        StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker,
                                                        String fileName) throws Exception{

        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // execute matching
        System.out.println("*\n*\tRunning identity resolution\n*");
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(data1, data2, null, matchingRule, blocker);

        // write correspondences to output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/"+fileName+".csv"), correspondences);
        new CSVRestaurantDetailFormatter().writeCSV(new File("data/output/zomato_yelp"+fileName+"_detail.csv"), correspondences);


        // load the gold standard (test set)
        System.out.println("*\n*\tLoading gold standard\n*");
        MatchingGoldStandard gsTest = new MatchingGoldStandard();
        gsTest.loadFromCSVFile(new File("data/goldstandard/gs_zomato_2_yelp_test_2"));

        System.out.println("*\n*\tEvaluating result\n*");
        // evaluate your result
        MatchingEvaluator<Restaurant, Attribute> evaluator = new MatchingEvaluator<Restaurant, Attribute>();
        Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);

        // print the evaluation result
        String expResults = String.format("Precision: %.4f\n",perfTest.getPrecision());
        expResults += String.format("Recall: %.4f\n",perfTest.getRecall());
        expResults += String.format("F1: %.4f\n",perfTest.getF1());

        return expResults;

    }

}
