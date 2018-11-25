package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCityNameFirstThreeGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.CSVRestaurantDetailFormatter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

import java.io.File;


/*
Interesting Models revealed by WekaGridSearch for Zomato-SchemaOrg are tested here
e.g. MultilayerPerceptron -L 0.3 -M 0.2 -N 800
 */

public class ZomatoDatafinitiML {


    public static void main(String[] args) throws Exception {
        // loading data
        HashedDataSet<Restaurant, Attribute> zomato = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("identity-resolution/data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);

        HashedDataSet<Restaurant, Attribute> datafiniti = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("identity-resolution/data/input/datafiniti_target.xml"), "/restaurants/restaurant", datafiniti);

        String outputFile = "identity-resolution/data/output/zomato_datafiniti_correspondences_MP.csv";
        String outputCheckFile = "identity-resolution/data/output/zomato_datafiniti_check_MP.csv";

        MatchingGoldStandard gsTraining = new MatchingGoldStandard();
        gsTraining.loadFromCSVFile(new File("identity-resolution/data/goldstandard/gs_zomato_2_datafiniti_training"));

        // load the gold standard (test set)
        MatchingGoldStandard gsTest = new MatchingGoldStandard();
        gsTest.loadFromCSVFile(new File(
                "identity-resolution/data/goldstandard/gs_zomato_2_datafiniti_test"));


        String [] options = {"-L", "0.3", "-M", "0.2", "-N", "800"};

        WekaMatchingRule<Restaurant, Attribute> matchingRule = new WekaMatchingRule<>(0.6, "MultilayerPerceptron", options);
        // add comparators

        matchingRule.addComparator(new RestaurantNameComparatorEqual());
        matchingRule.addComparator(new RestaurantNameComparatorJaccard(ComparatorUtils::removeCityName));
        matchingRule.addComparator(new RestaurantNameComparatorLevenshtein(ComparatorUtils::removeCityName));
        matchingRule.addComparator(new RestaurantNameComparatorNGramJaccardSimilarity());
        matchingRule.addComparator(new RestaurantPostalCodeComparatorLevenshtein(ComparatorUtils::cleanLower));
        matchingRule.addComparator(new RestaurantAddressComparatorJaccard());
        matchingRule.addComparator(new RestaurantAddressComparatorLevenshtein(ComparatorUtils::unifyAddress));
        matchingRule.addComparator(new RestaurantAddressComparatorLowerCaseJaccard());
        matchingRule.addComparator(new RestaurantAddressComparatorNGramJaccard());

        // learning Matching rule
        RuleLearner<Restaurant, Attribute> learner = new RuleLearner<>();

        Performance pLearn = learner.learnMatchingRule(zomato, datafiniti, null, matchingRule, gsTraining);

        System.out.println("Training");
        System.out.println(String.format("Precision: %.4f", pLearn.getPrecision()));
        System.out.println(String.format("Recall: %.4f", pLearn.getRecall()));
        System.out.println(String.format("F1: %.4f", pLearn.getF1()));
        System.out.println(matchingRule.getModelDescription());


        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByCityNameFirstThreeGenerator());

        // Execute the matching
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(
                zomato, datafiniti, null, matchingRule, blocker);

        System.out.println("CorrespondenceSize" + Integer.toString(correspondences.size()));
        System.out.println("ReductionRatio" + Double.toString(blocker.getReductionRatio()));
        // write the correspondences to the output file

        new CSVCorrespondenceFormatter().writeCSV(new File(outputFile), correspondences);

        // evaluate your result
        MatchingEvaluator<Restaurant, Attribute> evaluator = new MatchingEvaluator<Restaurant, Attribute>();
        Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);


        System.out.println("Testing");
        System.out.println(String.format("Precision: %.4f", perfTest.getPrecision()));
        System.out.println(String.format("Recall: %.4f", perfTest.getRecall()));
        System.out.println(String.format("F1: %.4f", perfTest.getF1()));

        new CSVRestaurantDetailFormatter().writeCSV(new File(outputCheckFile), correspondences);

    }

}
