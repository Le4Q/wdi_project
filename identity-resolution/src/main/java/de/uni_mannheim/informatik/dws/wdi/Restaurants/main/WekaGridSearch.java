package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.RestaurantAddressComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByPostalCodeGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.*;
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
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WekaGridSearch {

    private static final Logger logger = WinterLogManager.activateLogger("trace");

    private static final String[][] SL_options = {{"-S"}};

    private static final String[][] DT_options = {{"-X", "1", "-D", "0"},
                                                  {"-X", "2", "-D", "0"},
                                                  {"-X", "1", "-D", "1"},
                                                  {"-X", "2", "-D", "1"}};

    private static final String[][] HT_options = {{"-L", "0", "-S", "0"},
                                                  {"-L", "0", "-S", "1"},
                                                  {"-L", "1", "-S", "0"},
                                                  {"-L", "1", "-S", "0"}};

    private static final String[][] IBk_options = {{"-I", "-K", "1"},
                                                   {"-I", "-K", "2"},
                                                   {"-I", "-K", "3"},
                                                   {"-I", "-K", "4"},
                                                   {"-I", "-K", "5"},
                                                   {"-I", "-K", "6"},
                                                   {"-I", "-K", "7"},
                                                   {"-I", "-K", "8"},
                                                   {"-I", "-K", "9"},
                                                   {"-I", "-K", "9"}};

    private static final String[][] MP_options = {{"-L", "0.1", "-M", "0.2", "-N", "300"},
                                                  {"-L", "0.3", "-M", "0.2", "-N", "300"},
                                                  {"-L", "0.5", "-M", "0.2", "-N", "300"},
                                                  {"-L", "0.1", "-M", "0.4", "-N", "300"},
                                                  {"-L", "0.3", "-M", "0.4", "-N", "300"},
                                                  {"-L", "0.5", "-M", "0.4", "-N", "300"},
                                                  {"-L", "0.1", "-M", "0.2", "-N", "800"},
                                                  {"-L", "0.3", "-M", "0.2", "-N", "800"},
                                                  {"-L", "0.5", "-M", "0.2", "-N", "800"},
                                                  {"-L", "0.1", "-M", "0.4", "-N", "800"},
                                                  {"-L", "0.3", "-M", "0.4", "-N", "800"},
                                                  {"-L", "0.5", "-M", "0.4", "-N", "800"}};

    private static final String[][] Empty_options = {{""}};

    private static final String[] classifiers = {"SimpleLogistic","DecisionTable","HoeffdingTree","IBk",
                                                 "J48","KStar","LMT","M5Rules","MultilayerPerceptron",
                                                 "RandomTree","VotedPerceptron","BayesNet","NaiveBayes"};

    private static final String[][][] options = {SL_options, DT_options, HT_options, IBk_options,
                                                 Empty_options, Empty_options, Empty_options, Empty_options, MP_options,
                                                 Empty_options, Empty_options, Empty_options, Empty_options};

    private static final double[] thresholds = new double[]{0.6, 0.8};

    private static void runTrial(String resultPath, String modelType, String[] option, double threshold,
                                 HashedDataSet<Restaurant, Attribute> data1, HashedDataSet<Restaurant, Attribute> data2,
                                 MatchingGoldStandard gsTraining, MatchingGoldStandard gsTest) throws Exception{

        List<String> information = new ArrayList<String>();
        information.add(modelType);
        information.addAll(Arrays.asList(option));
        information.add("Threshold");
        information.add(Double.toString(threshold));

        WekaMatchingRule<Restaurant, Attribute> matchingRule = new WekaMatchingRule<>(threshold, modelType, option);
        // add comparators
        matchingRule.addComparator(new RestaurantNameComparatorEqual());
        matchingRule.addComparator(new RestaurantNameComparatorJaccard());
        matchingRule.addComparator(new RestaurantNameComparatorLevenshtein());
        matchingRule.addComparator(new RestaurantNameComparatorNGramJaccardSimilarity());
        matchingRule.addComparator(new RestaurantPostalCodeComparatorLevenshtein());
        matchingRule.addComparator(new RestaurantAddressComparatorJaccard());
        matchingRule.addComparator(new RestaurantAddressComparatorLevenshtein());
        matchingRule.addComparator(new RestaurantAddressComparatorLowerCaseJaccard());
        matchingRule.addComparator(new RestaurantAddressComparatorNGramJaccard());

        // learning Matching rule
        RuleLearner<Restaurant, Attribute> learner = new RuleLearner<>();

        Performance pLearn = learner.learnMatchingRule(data1, data2, null, matchingRule, gsTraining);


        information.addAll(Arrays.asList("Training", String.format("Precision: %.4f", pLearn.getPrecision()),
                                         String.format("Recall: %.4f", pLearn.getRecall()), String.format("F1: %.4f", pLearn.getF1())));


        System.out.println(matchingRule.getModelDescription());



        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByPostalCodeGenerator());

        // Execute the matching
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(
                data1, data2, null, matchingRule, blocker);

        information.addAll(Arrays.asList("CorrespondenceSize", Integer.toString(correspondences.size())));
        information.addAll(Arrays.asList("ReductionRatio", Double.toString(blocker.getReductionRatio())));
        // write the correspondences to the output file
        // new CSVCorrespondenceFormatter().writeCSV(new File("data/output/zomato_2_schemaOrg_correspondences.csv"), correspondences);

        // evaluate your result
        MatchingEvaluator<Restaurant, Attribute> evaluator = new MatchingEvaluator<Restaurant, Attribute>();
        Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);

        information.addAll(Arrays.asList("Testing", String.format("Precision: %.4f", perfTest.getPrecision()),
                String.format("Recall: %.4f", perfTest.getRecall()), String.format("F1: %.4f", perfTest.getF1())));


        try (
            Writer writer = new FileWriter(new File(resultPath), true);
            CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        ) {
            String[] resultLine = information.toArray(new String[0]);
            csvWriter.writeNext(resultLine);
        }

    }

    public static void main(String[] args) throws Exception {
        // loading data
        HashedDataSet<Restaurant, Attribute> zomato = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);
        HashedDataSet<Restaurant, Attribute> datafiniti = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/datafiniti_target.xml"), "/restaurants/restaurant", datafiniti);
        //HashedDataSet<Restaurant, Attribute> cleanSchemaOrg = removeDuplicates("data/output/schemaOrg_duplicates.csv", schemaOrg);

        MatchingGoldStandard gsTraining = new MatchingGoldStandard();
        gsTraining.loadFromCSVFile(new File("data/goldstandard/gs_zomato_2_datafiniti_training"));

        // load the gold standard (test set)
        MatchingGoldStandard gsTest = new MatchingGoldStandard();
        gsTest.loadFromCSVFile(new File(
                "data/goldstandard/gs_zomato_2_datafiniti_test"));

        String resultPath = "data/output/ML-grids/grid_zomato_2_datafiniti_PB.csv";


        for (int c=0; c<classifiers.length; c++){
            for (int o=0; o<options[c].length; o++){
                for (int t=0; t<thresholds.length; t++) {
                    try {
                        runTrial(resultPath, classifiers[c], options[c][o], thresholds[t], zomato, datafiniti, gsTraining, gsTest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
