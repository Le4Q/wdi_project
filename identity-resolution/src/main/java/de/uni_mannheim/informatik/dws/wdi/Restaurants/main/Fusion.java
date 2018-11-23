package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.CSVRestaurantDetailFormatter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.FusibleRestaurantFactory;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class Fusion {

    public static void main(String args[]) throws Exception{
        // Load the Data into FusibleDataSet
        System.out.println("*\n*\tLoading datasets\n*");
        FusibleDataSet<Restaurant, Attribute> datafiniti = new FusibleHashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/datafiniti_target.xml"), "/restaurants/restaurant", datafiniti);
        datafiniti.printDataSetDensityReport();

        FusibleDataSet<Restaurant, Attribute> schemaOrg = new FusibleHashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/schemaOrg_target.xml"), "/restaurants/restaurant", schemaOrg);
        schemaOrg.printDataSetDensityReport();

        FusibleDataSet<Restaurant, Attribute> zomato = new FusibleHashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant", zomato);
        zomato.printDataSetDensityReport();

        FusibleDataSet<Restaurant, Attribute> yelp = new FusibleHashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/input/yelp_target.xml"), "/restaurants/restaurant", yelp);
        yelp.printDataSetDensityReport();


        // Maintain Provenance
        // Scores (e.g. from rating)
        schemaOrg.setScore(1.0);
        datafiniti.setScore(2.0);
        zomato.setScore(3.0);
        yelp.setScore(4.0);

        // Date (e.g. last update)
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);

        yelp.setDate(LocalDateTime.parse("2018-08-02", formatter));
        schemaOrg.setDate(LocalDateTime.parse("2017-11-01", formatter));
        zomato.setDate(LocalDateTime.parse("2018-10-30", formatter));
        datafiniti.setDate(LocalDateTime.parse("2018-06-30", formatter));

        // load correspondences
        System.out.println("*\n*\tLoading correspondences\n*");
        CorrespondenceSet<Restaurant, Attribute> correspondences = new CorrespondenceSet<>();
        System.out.println("*\n*\tLoading correspondences 1\n*");
        correspondences.loadCorrespondences(new File("data/output/zomato_yelp_correspondence_simple_matching.csv"),zomato, yelp);
        System.out.println("*\n*\tLoading correspondences 2\n*");
        correspondences.loadCorrespondences(new File("data/output/zomato_datafiniti_correspondences_MP.csv"), zomato, datafiniti);
        System.out.println("*\n*\tLoading correspondences 3\n*");
        correspondences.loadCorrespondences(new File("data/output/zomato_schemaOrg_correspondences_KStar.csv"), zomato, schemaOrg);


        // write group size distribution
        correspondences.printGroupSizeDistribution();

        // load the gold standard
        System.out.println("*\n*\tEvaluating results\n*");
        DataSet<Restaurant, Attribute> gs = new FusibleHashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/goldstandard/fusion_gold.xml"), "/restaurants/restaurant", gs);


        // define the fusion strategy
        DataFusionStrategy<Restaurant, Attribute> strategy = new DataFusionStrategy<>(new FusibleRestaurantFactory());

        // add attribute fusers TODO:
        strategy.addAttributeFuser(Restaurant.NAME, new NameFuserVoting(),new NameEvaluationRule());
        strategy.addAttributeFuser(Restaurant.NEIGHBORHOOD, new NeighborhoodFuserMostRecent(),new NameEvaluationRule());
        strategy.addAttributeFuser(Restaurant.POSTALADDRESS, new PostalAddressFuserMostRecent(), new PostalAddressEvaluationRule());
        strategy.addAttributeFuser(Restaurant.CATEGORIES,new CategoriesFuserUnion(), new CategoriesEvaluationRule());
        strategy.addAttributeFuser(Restaurant.LATITUDE, new LatitudeFuserFavourSource(),new LatitudeEvaluationRule());
        strategy.addAttributeFuser(Restaurant.LONGITUDE,new LongitudeFuserFavourSources(),new LongitudeEvaluationRule());
        strategy.addAttributeFuser(Restaurant.PRICERANGE,new PriceRangeFuserVoting(),new PriceRangeEvaluationRule());
        strategy.addAttributeFuser(Restaurant.CATEGORIES,new CategoriesFuserUnion(), new CategoriesEvaluationRule());
        strategy.addAttributeFuser(Restaurant.OPENINGHOURS,new OpeningHoursFuserMostRecent(),new OpeningHoursEvaluationRule());

        // create the fusion engine
        DataFusionEngine<Restaurant, Attribute> engine = new DataFusionEngine<>(strategy);

        // print consistency report
        engine.printClusterConsistencyReport(correspondences, null);

        // print record groups sorted by consistency
        engine.writeRecordGroupsByConsistency(new File("data/output/recordGroupConsistencies.csv"), correspondences, null);

        // run the fusion
        System.out.println("*\n*\tRunning data fusion\n*");
        FusibleDataSet<Restaurant, Attribute> fusedDataSet = engine.run(correspondences, null);

        // evaluate
        DataFusionEvaluator<Restaurant, Attribute> evaluator = new DataFusionEvaluator<>(strategy);

        double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

        System.out.println(String.format("Accuracy: %.2f", accuracy));
    }
}