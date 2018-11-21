package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCategoryGenerator;
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


public class ZomatoYelpLinearRulesGridSearch {

    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception {

        // load the data sets
        HashedDataSet<Restaurant, Attribute> zomato = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);

        HashedDataSet<Restaurant, Attribute> yelp = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/yelp_target.xml"), "/restaurants/restaurant", yelp);

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByCategoryGenerator());
        blocker.setMeasureBlockSizes(true);
        blocker.collectBlockSizeData("data/output/gs_zomato_yelp_debugBlocking.csv", 100);

        ArrayList<Double> nameWeights = new ArrayList<Double>();
        Double addrWeight;
        nameWeights.add(0.3);
        nameWeights.add(0.4);
        nameWeights.add(0.5);
        nameWeights.add(0.6);

        ArrayList<Double> thresholds = new ArrayList<Double>();
        thresholds.add(0.50);
        thresholds.add(0.55);
        thresholds.add(0.60);
        thresholds.add(0.65);
        thresholds.add(0.70);
        thresholds.add(0.75);
        thresholds.add(0.80);
        thresholds.add(0.85);
        thresholds.add(0.90);

        ArrayList<String> comparatorFunctions = new ArrayList<String>();
        comparatorFunctions.add("def");
        comparatorFunctions.add("cleanLowerStopWords");
        comparatorFunctions.add("cleanLower");
        comparatorFunctions.add("removeCityName");
        // comparatorFunctions.add("unifyAddress");

        String expFileName = "/home/rohitalyosha/Masters/Fall_2018/wdi/project/wdi_project/identity-resolution/data/exps/LinearRulesTest2.txt";
        File expFile = new File(expFileName);
        expFile.createNewFile();
        String expResults;
        String expSettings;


        for (String func1Name : comparatorFunctions) {
            for (String func2Name : comparatorFunctions) {
                for (Double threshold : thresholds) {
                    for (Double nameWeight : nameWeights) {
                        addrWeight = 1 - nameWeight;
                        ArrayList<String> funcNames = new ArrayList<String>();
                        funcNames.add(func1Name);
                        funcNames.add(func2Name);

                        expSettings = "Threshold: " + Double.toString(threshold) + "\t";
                        expSettings += func1Name + "\t";
                        expSettings += func2Name + "\t";
                        expSettings += "Name weight: " + Double.toString(nameWeight) + "\t";
                        expSettings += "Address weight: " + Double.toString(addrWeight) + "\n";
                        Files.write(Paths.get(expFileName), expSettings.getBytes(), StandardOpenOption.APPEND);

                        ArrayList<Function<String, String>> prep = new ArrayList<Function<String, String>>();
                        for (String funcName : funcNames) {
                            System.out.println(funcName);
                            if (funcName.equals("cleanLower"))
                                prep.add(ComparatorUtils::cleanLower);
                            else if (funcName.equals("cleanLowerStopWords"))
                                prep.add(ComparatorUtils::cleanLowerStopwords);
                            else if (funcName.equals("removeCityName"))
                                prep.add(ComparatorUtils::removeCityName);
                            else if (funcName.equals("unifyAddress"))
                                prep.add(ComparatorUtils::unifyAddress);
                            else if (funcName.equals("def"))
                                prep.add(ComparatorUtils::def);
                        }

                        ArrayList<Double> weights = new ArrayList<Double>();
                        weights.add(nameWeight);
                        weights.add(addrWeight);

                        expResults = simpleTemplate(zomato, yelp, threshold, weights, prep, blocker);
                        Files.write(Paths.get(expFileName), expResults.getBytes(), StandardOpenOption.APPEND);
                    }
                }
            }
        }
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

        return findCorrespondencesAndEvaluate(data1,data2,matchingRule,blocker,"simple");

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
        new CSVRestaurantDetailFormatter().writeCSV(new File("data/output/"+fileName+"_detail.csv"), correspondences);


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
