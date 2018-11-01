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


public class SimpleMatching {


    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception {
        // loading data
        HashedDataSet<Restaurant, Attribute> zomato = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("identity-resolution/data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);

        HashedDataSet<Restaurant, Attribute> datafiniti = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("identity-resolution/data/input/datafiniti_target.xml"), "restaurants/restaurant", datafiniti);
        //HashedDataSet<Restaurant, Attribute> cleanSchemaOrg = removeDuplicates("data/output/schemaOrg_duplicates.csv", schemaOrg);
        // create a matching rule

        LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
                0.63);

        // add comparators
        matchingRule.addComparator(new RestaurantNameComparatorLevenshtein(), 0.4);
        matchingRule.addComparator(new RestaurantAddressComparatorLevenshtein(), 0.6);

        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByCityNameGenerator());
        //StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new StaticBlockingKeyGenerator<Restaurant, Attribute>());
        blocker.collectBlockSizeData("identity-resolution/data/output/debugBlocking.csv", 1000);

        // Execute the matching
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(
                zomato, datafiniti, null, matchingRule, blocker);


          // write the correspondences to the output files
        new CSVCorrespondenceFormatter().writeCSV(new File("identity-resolution/data/output/test_correspondences_SMI.csv"), correspondences);

        String annotationPath = "identity-resolution/data/output/test_correspondences_SM_detail_ZDI.csv";
        new CSVRestaurantDetailFormatter().writeCSV(new File(annotationPath), correspondences);
        writeRandom(300, annotationPath, zomato, datafiniti);


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

    public static HashedDataSet<Restaurant, Attribute> removeDuplicates(String pathDuplicates, HashedDataSet<Restaurant, Attribute> data) throws IOException {

        Set<String> ToKeep = new HashSet<String>();
        Set<String> ToRemove = new HashSet<String>();

        try (
                Reader reader = Files.newBufferedReader(Paths.get(pathDuplicates));
                CSVReader csvReader = new CSVReader(reader)
        ) {

            String[] duplicateTuple;
            while ((duplicateTuple = csvReader.readNext()) != null) {
                String id1 = duplicateTuple[0];
                String id2 = duplicateTuple[1];

                if(ToKeep.contains(id1)){
                    ToRemove.add(id2);
                }else if(ToKeep.contains(id2)){
                    ToRemove.add(id1);
                }else{
                    ToKeep.add(id1);
                }
            }
        }

        for (String duplicate : ToRemove) {
            data.removeRecord(duplicate);
        }

        return data;

    }

}
