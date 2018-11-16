package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCategoryGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantAddressComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.CSVRestaurantDetailFormatter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import org.apache.logging.log4j.Logger;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCityNameGenerator;
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
        // loading data
        HashedDataSet<Restaurant, Attribute> zomato = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);

        HashedDataSet<Restaurant, Attribute> yelp = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/yelp_target.xml"), "restaurants/restaurant", yelp);

        // create a matching rule
        LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule = new LinearCombinationMatchingRule<>(0.80);

        // add comparators
        matchingRule.addComparator(new RestaurantNameComparatorLevenshtein(), 0.4);
        matchingRule.addComparator(new RestaurantAddressComparatorLevenshtein(), 0.6);

        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByCategoryGenerator());
        blocker.collectBlockSizeData("data/output/zomato_yelp_simplematching_debugBlocking.csv", 1000);

        // Execute the matching
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(zomato, yelp, null, matchingRule, blocker);


        // write the correspondences to the output files
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/zomato_yelp_correspondence_simple_matching.csv"), correspondences);

        String annotationPath = "data/output/zomato_yelp_correspondence_simple_matching_detail.csv";
        new CSVRestaurantDetailFormatter().writeCSV(new File(annotationPath), correspondences);
        writeRandom(300, annotationPath, zomato, yelp);


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
