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

        // simple matching rule to create the gold standard
        LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule = new LinearCombinationMatchingRule<>(0.75);

        // add comparators
        matchingRule.addComparator(new RestaurantNameComparatorLevenshtein(ComparatorUtils::cleanLower), 0.4);
        matchingRule.addComparator(new RestaurantCityNameComparatorLevenshtein(ComparatorUtils::cleanLower), 0.3);
        matchingRule.addComparator(new RestaurantAddressComparatorLevenshtein(ComparatorUtils::cleanLower), 0.3);

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByPostalCodeGenerator());
        blocker.setMeasureBlockSizes(true);
        blocker.collectBlockSizeData("data/output/gs_yelp_schema_debugBlocking.csv", 100);

        findCorrespondencesAndEvaluate(schema,yelp,matchingRule,blocker,"simple");


        // simple matching rule to create the gold standard
        matchingRule = new LinearCombinationMatchingRule<>(0.75);

        // add comparators
        matchingRule.addComparator(new RestaurantNameComparatorLevenshtein(ComparatorUtils::cleanLower), 0.4);
        matchingRule.addComparator(new RestaurantCityNameComparatorLevenshtein(ComparatorUtils::cleanLower), 0.3);
        matchingRule.addComparator(new RestaurantAddressComparatorLevenshtein(ComparatorUtils::unifyAddress), 0.3);

        findCorrespondencesAndEvaluate(schema,yelp,matchingRule,blocker,"simple");

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
