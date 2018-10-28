package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.IdentityResolution;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.RestaurantBlockingKeyByCityNameGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.SchemaOrgDuplicateDetectionBlockingKey;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.RestaurantAddressComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.RestaurantNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.RestaurantXMLReader;
import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.StaticBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;


public class SchemaOrgDuplicateDetection {

    /*
     * Logging Options:
     * 		default: 	level INFO	- console
     * 		trace:		level TRACE     - console
     * 		infoFile:	level INFO	- console/file
     * 		traceFile:	level TRACE	- console/file
     *
     * To set the log level to trace and write the log to winter.log and console,
     * activate the "traceFile" logger as follows:
     *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
     *
     */

    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception {

        // define the matching rule
        LinearCombinationMatchingRule<Restaurant, Attribute> rule = new LinearCombinationMatchingRule<>(
                0, 0.95);
        rule.addComparator(new RestaurantNameComparatorLevenshtein(), 0.5);
        rule.addComparator(new RestaurantAddressComparatorLevenshtein(), 0.5);

        // create the matching engine

        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new SchemaOrgDuplicateDetectionBlockingKey());
        blocker.collectBlockSizeData("data/output/debugBlocking.csv", 1000);

        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // load the data sets
        HashedDataSet<Restaurant, Attribute> schemaOrg = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/schemaOrg_target.xml"), "/restaurants/restaurant", schemaOrg);


        // run the matching
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine
                .runDuplicateDetection(schemaOrg, rule, blocker);

        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/schemaOrg_duplicates.csv"), correspondences);

        printCorrespondences(new ArrayList<>(correspondences.get()));
    }

    private static void printCorrespondences(
            List<Correspondence<Restaurant, Attribute>> correspondences) {
        // sort the correspondences
        Collections.sort(correspondences,
                new Comparator<Correspondence<Restaurant, Attribute>>() {

                    @Override
                    public int compare(Correspondence<Restaurant, Attribute> o1,
                                       Correspondence<Restaurant, Attribute> o2) {
                        int score = Double.compare(o1.getSimilarityScore(),
                                o2.getSimilarityScore());
                        int title = o1.getFirstRecord().getName()
                                .compareTo(o2.getFirstRecord().getName());

                        if (score != 0) {
                            return -score;
                        } else {
                            return title;
                        }
                    }

                });

        // print the correspondences
        for (Correspondence<Restaurant, Attribute> correspondence : correspondences) {
            logger.info(String
                    .format("%s,%s |\t\t%.2f\t[%s] %s (%s) <--> [%s] %s (%s)",
                            correspondence.getFirstRecord().getIdentifier(),
                            correspondence.getSecondRecord().getIdentifier(),
                            correspondence.getSimilarityScore(),
                            correspondence.getFirstRecord().getIdentifier(),
                            correspondence.getFirstRecord().getName(),
                            correspondence.getFirstRecord().getPostalAddress().getAddress(),
                            correspondence.getSecondRecord().getIdentifier(),
                            correspondence.getSecondRecord().getName(),
                            correspondence.getSecondRecord().getPostalAddress().getAddress()));
            // }
        }
    }
}
