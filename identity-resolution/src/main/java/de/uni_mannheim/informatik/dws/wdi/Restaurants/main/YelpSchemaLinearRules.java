package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.wcohen.ss.SourcedTFIDF;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByCityNameFirstFiveGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker.RestaurantBlockingKeyByPostalCodeGenerator;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.CSVRestaurantDetailFormatter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
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
import de.uni_mannheim.informatik.dws.winter.similarity.list.GeneralisedJaccard;
import de.uni_mannheim.informatik.dws.winter.similarity.list.MaximumOfContainment;
import de.uni_mannheim.informatik.dws.winter.similarity.string.*;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.Logger;
import sun.tools.jstat.Token;

import java.io.File;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;


public class YelpSchemaLinearRules {

    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception {
        // testParameters();

        // load the data sets
        HashedDataSet<Restaurant, Attribute> schemaOrg = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/schemaOrg_target.xml"), "/restaurants/restaurant", schemaOrg);
        HashedDataSet<Restaurant, Attribute> schema = Utils.removeDuplicates("data/output/schemaOrg_duplicates.csv", schemaOrg);
        System.out.println(schema.size());

        HashedDataSet<Restaurant, Attribute> yelp = new HashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/yelp_target.xml"), "/restaurants/restaurant", yelp);

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByPostalCodeGenerator());
        //StandardRecordBlocker<Restaurant, Attribute> blocker = new StandardRecordBlocker<Restaurant, Attribute>(new RestaurantBlockingKeyByCityNameFirstFiveGenerator());
        blocker.setMeasureBlockSizes(true);
        blocker.collectBlockSizeData("data/output/gs_yelp_schema_debugBlocking.csv", 100);


        Double t = 0.76;

        ArrayList<Double> weights = new ArrayList<Double>();
        weights.add(0.35);
        weights.add(0.3);
        weights.add(0.35);
        //weights.add(0.15);

        ArrayList<Function<String,String>> prep = new ArrayList<Function<String,String>>();
        prep.add(ComparatorUtils::removeCityName);
        prep.add(ComparatorUtils::cleanLower);
        prep.add(ComparatorUtils::unifyAddress);


        String fileName = "name_city_address_t:"+Double.toString(t)+"_w1:"+Double.toString(weights.get(0))+"_w2:"+Double.toString(weights.get(1))+
                "_w3:"+Double.toString(weights.get(2))+"_remCity_clean_unifyAddress";
        //simpleTemplate(schema,yelp,t,weights,prep,blocker,true,fileName);
        simpleTemplate02(schema,yelp,t,weights,prep,blocker,fileName);

        //testParameters()
    }

    public static void testParameters() throws Exception{

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

        // INIT WEIGHTS
        ArrayList<Double> weights1 = new ArrayList<Double>();
        weights1.add(0.4);
        weights1.add(0.3);
        weights1.add(0.3);

        ArrayList<Double> weights2 = new ArrayList<Double>();
        weights2.add(0.5);
        weights2.add(0.1);
        weights2.add(0.4);

        ArrayList<Double> weights3 = new ArrayList<Double>();
        weights3.add(0.8);
        weights3.add(0.05);
        weights3.add(0.15);

        ArrayList<ArrayList<Double>> aWeights = new ArrayList<ArrayList<Double>>();
        aWeights.add(weights1);
        aWeights.add(weights2);
        aWeights.add(weights3);

        // INIT PREPROCESSING STUFF
        ArrayList<Function<String,String>> prep1 = new ArrayList<Function<String,String>>();
        prep1.add(ComparatorUtils::cleanLower);
        prep1.add(ComparatorUtils::cleanLower);
        prep1.add(ComparatorUtils::cleanLower);

        ArrayList<String> prepNames1 = new ArrayList<String>();
        prepNames1.add("clean");
        prepNames1.add("clean");
        prepNames1.add("clean");

        ArrayList<Function<String,String>> prep2 = new ArrayList<Function<String,String>>();
        prep2.add(ComparatorUtils::cleanLowerStopwords);
        prep2.add(ComparatorUtils::cleanLower);
        prep2.add(ComparatorUtils::cleanLower);

        ArrayList<String> prepNames2 = new ArrayList<String>();
        prepNames2.add("cleanStop");
        prepNames2.add("clean");
        prepNames2.add("clean");

        ArrayList<Function<String,String>> prep3 = new ArrayList<Function<String,String>>();
        prep3.add(ComparatorUtils::removeCityName);
        prep3.add(ComparatorUtils::cleanLower);
        prep3.add(ComparatorUtils::cleanLower);

        ArrayList<String> prepNames3 = new ArrayList<String>();
        prepNames3.add("remCity");
        prepNames3.add("clean");
        prepNames3.add("clean");


        ArrayList<Function<String,String>> prep4 = new ArrayList<Function<String,String>>();
        prep4.add(ComparatorUtils::cleanLower);
        prep4.add(ComparatorUtils::cleanLower);
        prep4.add(ComparatorUtils::unifyAddress);

        ArrayList<String> prepNames4 = new ArrayList<String>();
        prepNames4.add("clean");
        prepNames4.add("clean");
        prepNames4.add("unifyAdd");


        ArrayList<Function<String,String>> prep5 = new ArrayList<Function<String,String>>();
        prep5.add(ComparatorUtils::removeCityName);
        prep5.add(ComparatorUtils::cleanLower);
        prep5.add(ComparatorUtils::unifyAddress);

        ArrayList<String> prepNames5 = new ArrayList<String>();
        prepNames5.add("remCity");
        prepNames5.add("clean");
        prepNames5.add("unifyAdd");


        ArrayList<ArrayList<Function<String,String>>> aPreprocessings = new ArrayList<ArrayList<Function<String,String>>>();
        aPreprocessings.add(prep1);
        aPreprocessings.add(prep2);
        aPreprocessings.add(prep3);
        aPreprocessings.add(prep4);
        aPreprocessings.add(prep5);

        ArrayList<ArrayList<String>> aPreprocessingNames = new ArrayList<ArrayList<String>>();
        aPreprocessingNames.add(prepNames1);
        aPreprocessingNames.add(prepNames2);
        aPreprocessingNames.add(prepNames3);
        aPreprocessingNames.add(prepNames4);
        aPreprocessingNames.add(prepNames5);


        Double[] aThresholds = {
                0.7,0.75,0.78,0.8
        };


        Iterator<ArrayList<String>> i1 = aPreprocessingNames.iterator();
        Iterator<ArrayList<Function<String, String>>> i2 = aPreprocessings.iterator();

        while(i1.hasNext() && i2.hasNext()){
            ArrayList<Function<String,String>> p = i2.next();
            ArrayList<String> names = i1.next();
            //for (ArrayList<Function<String, String>> p : aPreprocessings) {
            for (ArrayList<Double> w : aWeights) {
                for (Double t : aThresholds) {
                    String fileName = "name_city_address_t:"+Double.toString(t)+"_w1:"+Double.toString(w.get(0))+"_w2:"+Double.toString(w.get(1))+
                            "_w3:"+Double.toString(w.get(2))+"_"+names.get(0)+"_"+names.get(1)+"_"+names.get(2);
                    Boolean bRemoveCityName = names.get(0).equals("remCity") ? true : false;
                    simpleTemplate(schema,yelp,t,w,p,blocker,bRemoveCityName,fileName);
                }
            }
        }
    }




    public static LinearCombinationMatchingRule<Restaurant,Attribute> getMatchingRule(ArrayList<Comparator<Restaurant,Attribute>> fns,
                                                                                      Double threshold,
                                                                                      ArrayList<Double> weights) throws Exception{
        // simple matching rule to create the gold standard
        LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule = new LinearCombinationMatchingRule<>(threshold);

        //MatchingGoldStandard gsTraining = new MatchingGoldStandard();
        //gsTraining.loadFromCSVFile(new File(
        //        "data/goldstandard/yelp_schema_gs_training.csv"));
        //matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", -1, gsTraining);

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
                                      StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker,
                                      String fileName) throws Exception {
        ArrayList<Comparator<Restaurant,Attribute>> fns = new ArrayList<Comparator<Restaurant,Attribute>>();

        fns.add(new RestaurantNameComparatorLevenshtein(prep.get(0)));
        fns.add(new RestaurantCityNameComparatorLevenshtein(prep.get(1)));
        fns.add(new RestaurantAddressComparatorLevenshtein(prep.get(2)));

        LinearCombinationMatchingRule<Restaurant,Attribute> matchingRule = getMatchingRule(fns, threshold, weights);

        findCorrespondencesAndEvaluate(data1,data2,matchingRule,blocker, fileName);
    }

    public static void simpleTemplate(HashedDataSet<Restaurant, Attribute> data1,
                                      HashedDataSet<Restaurant, Attribute> data2,
                                      Double threshold,
                                      ArrayList<Double> weights,
                                      ArrayList<Function<String,String>> prep,
                                      StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker,
                                      boolean removeCityName,
                                      String fileName) throws Exception {
        ArrayList<Comparator<Restaurant,Attribute>> fns = new ArrayList<Comparator<Restaurant,Attribute>>();

        fns.add(new RestaurantNameComparatorLevenshtein(prep.get(0), removeCityName));
        fns.add(new RestaurantCityNameComparatorLevenshtein(prep.get(1)));
        fns.add(new RestaurantAddressComparatorLevenshtein(prep.get(2)));

        LinearCombinationMatchingRule<Restaurant,Attribute> matchingRule = getMatchingRule(fns, threshold, weights);

        findCorrespondencesAndEvaluate(data1,data2,matchingRule,blocker,fileName);
    }


    public static void simpleTemplate02(HashedDataSet<Restaurant, Attribute> data1,
                                      HashedDataSet<Restaurant, Attribute> data2,
                                      Double threshold,
                                      ArrayList<Double> weights,
                                      ArrayList<Function<String,String>> prep,
                                      StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker,
                                      String fileName) throws Exception {
        ArrayList<Comparator<Restaurant,Attribute>> fns = new ArrayList<Comparator<Restaurant,Attribute>>();

        fns.add(new RestaurantNameComparatorNGramJaccardSimilarity(prep.get(0), true));
        //fns.add(new RestaurantNameComparatorTokenJaccardSimilarity(prep.get(0), true));
        fns.add(new RestaurantCityNameComparatorLevenshtein(prep.get(1)));
        //fns.add(new RestaurantPostalCodeComparatorNgramJaccard(ComparatorUtils::cleanPostal));

        fns.add(new RestaurantAddressComparatorNGramJaccardNoPostProcessing(prep.get(2)));

        LinearCombinationMatchingRule<Restaurant,Attribute> matchingRule = getMatchingRule(fns, threshold, weights);

        findCorrespondencesAndEvaluate(data1,data2,matchingRule,blocker, fileName);
    }

    public static void findCorrespondencesAndEvaluate(HashedDataSet<Restaurant, Attribute> data1,
                                                      HashedDataSet<Restaurant, Attribute> data2,
                                                      LinearCombinationMatchingRule<Restaurant, Attribute> matchingRule,
                                                      StandardBlocker<Restaurant,Attribute,Restaurant,Attribute> blocker,
                                                      String fileName) throws Exception{

        // Initialize Matching Engine
        MatchingEngine<Restaurant, Attribute> engine = new MatchingEngine<>();

        LocalDateTime start = LocalDateTime.now();

        // execute matching
        System.out.println("*\n*\tRunning identity resolution\n*");
        Processable<Correspondence<Restaurant, Attribute>> correspondences = engine.runIdentityResolution(
                data1, data2, null, matchingRule, blocker
        );

        LocalDateTime end = LocalDateTime.now();
        String t = DurationFormatUtils.formatDurationHMS(Duration.between(start, end).toMillis());


        // write correspondences to output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/"+fileName+".csv"), correspondences);
        new CSVRestaurantDetailFormatter().writeCSV(new File("data/output/"+fileName+"_detail.csv"), correspondences);


        // load the gold standard (test set)
        System.out.println("*\n*\tLoading gold standard\n*");

        MatchingGoldStandard gsTotal = new MatchingGoldStandard();
        gsTotal.loadFromCSVFile(new File(
                "data/goldstandard/yelp_schema_gs_total.csv"));

        MatchingGoldStandard gsTest = new MatchingGoldStandard();
        gsTest.loadFromCSVFile(new File(
                "data/goldstandard/yelp_schema_gs_test.csv"));

        System.out.println("*\n*\tEvaluating result\n*");
        // evaluate your result
        System.out.println("** TOTAL **");
        MatchingEvaluator<Restaurant, Attribute> evaluator = new MatchingEvaluator<Restaurant, Attribute>();
        Performance perfTest = evaluator.evaluateMatching(correspondences,
                                                          gsTotal);

        // print the evaluation result
        System.out.println("Schema <-> Yelp");
        System.out.println(String.format(
                "Precision: %.4f",perfTest.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTest.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTest.getF1()));

        System.out.println("** TEST FILE **");
        evaluator = new MatchingEvaluator<Restaurant, Attribute>();
        perfTest = evaluator.evaluateMatching(correspondences,
                                                          gsTest);

        // print the evaluation result
        System.out.println("Schema <-> Yelp");
        System.out.println(String.format(
                "Precision: %.4f",perfTest.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTest.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTest.getF1()));

        // write it to file
        PrintWriter writer = new PrintWriter("data/output/yelp_schema_logs/"+fileName+".txt", "UTF-8");
        writer.println(Double.toString(perfTest.getPrecision())+";"+Double.toString(perfTest.getRecall())+";"+Double.toString(perfTest.getF1())+";"+t+";"+
        Double.toString(blocker.getReductionRatio()));
        writer.close();

    }

}
