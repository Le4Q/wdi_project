package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCategoryGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantAddressComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.CSVRestaurantDetailFormatter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import org.apache.logging.log4j.Logger;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;


public class ZomatoYelpSimpleMatching {


    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception {

        double threshold = 0.7;
        double name_weight = 0.4;
        double addr_weight = 0.6;

        // loading data
        HashedDataSet<Restaurant, Attribute> zomato = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);

        HashedDataSet<Restaurant, Attribute> yelp = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/yelp_target.xml"), "restaurants/restaurant", yelp);

        // create a matching rule
        LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule = new LinearCombinationMatchingRule<>(threshold);

        // add comparators
        matchingRule.addComparator(new RestaurantNameComparatorLevenshtein(), name_weight);
        matchingRule.addComparator(new RestaurantAddressComparatorLevenshtein(), addr_weight);

        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByCategoryGenerator());
        blocker.collectBlockSizeData("data/output/zomato_yelp/simplematching_debugBlocking.csv", 1000);

        // Execute the matching
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(zomato, yelp, null, matchingRule, blocker);

        // write the correspondences to the output files
        String overviewFName = "data/output/zomato_yelp/correspondence_simple_matching_new_" + Double.toString(threshold) + "_" + Double.toString(name_weight) + "_" + Double.toString(addr_weight) + ".csv";
        new CSVCorrespondenceFormatter().writeCSV(new File(overviewFName), correspondences);

        String detailFName = "data/output/zomato_yelp/correspondence_simple_matching_new_detail_" + Double.toString(threshold) + "_" + Double.toString(name_weight) + "_" + Double.toString(addr_weight) + ".csv";
        new CSVRestaurantDetailFormatter().writeCSV(new File(detailFName), correspondences);
        writeRandom(300, detailFName, zomato, yelp);

    }

    public static void writeRandom(int num, String pathname, DataSet<Restaurant, Attribute> first, DataSet<Restaurant, Attribute> second) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(new File (pathname), true));
        CSVRestaurantDetailFormatter formatter = new CSVRestaurantDetailFormatter();

        for (int i=0; i<=num; i++){
            Restaurant res1 = first.getRandomRecord();
            Restaurant res2 = second.getRandomRecord();

            Correspondence<Restaurant, Attribute> correspondence = new Correspondence<Restaurant, Attribute>(res1, res2, -1);
            String[] values = formatter.format(correspondence);
            writer.writeNext(values);
        }

        writer.close();
    }

}
