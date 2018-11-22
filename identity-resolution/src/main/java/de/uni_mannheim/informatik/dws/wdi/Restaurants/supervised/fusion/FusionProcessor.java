package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.CategoriesEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.LatitudeEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.LongitudeEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.NameEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.OpeningHoursEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.PostalAddressEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.PriceRangeEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.CategoriesFuserUnion;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.LatitudeFuserFavourSource;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.LongitudeFuserFavourSources;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.NameFuserVoting;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.NeighborhoodFuserMostRecent;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.OpeningHoursFuserMostRecent;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.PostalAddressFuserMostRecent;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.PriceRangeFuserVoting;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.main.SupervisedFusionFeature;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.FusibleRestaurantFactory;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLFormatter;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class FusionProcessor {

	
	public double run(FusionRunConfiguration configuration, boolean persistence) throws Exception{
		// Load the Data into FusibleDataSet
		System.out.println("*\n*\tLoading datasets\n*");
		FusibleDataSet<Restaurant, Attribute> datafiniti = new FusibleHashedDataSet<>();
		new RestaurantXMLReader().loadFromXML(new File("data/input/datafiniti_target.xml"), "/restaurants/restaurant",
				datafiniti);
		datafiniti.printDataSetDensityReport();

		FusibleDataSet<Restaurant, Attribute> schemaOrg = new FusibleHashedDataSet<>();
		new RestaurantXMLReader().loadFromXML(new File("data/input/schemaOrg_target.xml"), "/restaurants/restaurant",
				schemaOrg);
		schemaOrg.printDataSetDensityReport();

		FusibleDataSet<Restaurant, Attribute> zomato = new FusibleHashedDataSet<>();
		new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant",
				zomato);
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
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
				.parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0).parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
				.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).toFormatter(Locale.ENGLISH);

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
        
		// load the gold standard TODO:
        System.out.println("*\n*\tEvaluating results\n*");
        DataSet<Restaurant, Attribute> gs = new FusibleHashedDataSet<>();
        new RestaurantXMLReader().loadFromXML(new File("data/fusion/gold.xml"), "/restaurants/restaurant", gs);
		 

		// define the fusion strategy
		DataFusionStrategy<Restaurant, Attribute> strategy = new DataFusionStrategy<>(new FusibleRestaurantFactory());

		// add attribute fusers TODO:

		for (SupervisedFusionFeature sfFeature : configuration.getSupervisedFusionFeature()) {
			strategy.addAttributeFuser(sfFeature.getAttribute(), sfFeature.getAttributeValueFuser(), sfFeature.getEvaluationRule());
		}

		// create the fusion engine
		DataFusionEngine<Restaurant, Attribute> engine = new DataFusionEngine<>(strategy);

		// print consistency report
		engine.printClusterConsistencyReport(correspondences, null);

		// print record groups sorted by consistency
		//engine.writeRecordGroupsByConsistency(new File("data/output/recordGroupConsistencies.csv"), correspondences,
		//		null);

		// print record groups sorted by consistency
		//engine.writeRecordGroupsByConsistency(new File("data/output/recordGroupConsistencies.csv"), correspondences,
		//		null);

		// run the fusion
		System.out.println("*\n*\tRunning data fusion\n*");
		FusibleDataSet<Restaurant, Attribute> fusedDataSet = engine.run(correspondences, null);

		// write the result
		//new RestaurantXMLFormatter().writeXML(new File("data/output/fused.xml"), fusedDataSet);

		// evaluate
		DataFusionEvaluator<Restaurant, Attribute> evaluator = new DataFusionEvaluator<>(strategy);

		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);
		
		
		if (persistence) {
			new RestaurantXMLFormatter().writeXML(new File("data/output/fused.xml"), fusedDataSet);
		}
		
		System.out.println(String.format("Accuracy: %.2f", accuracy));
		return Double.parseDouble(String.format("Accuracy: %.2f", accuracy));
	}
}
