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

	
	public double run(FusionRunConfiguration configuration, boolean persistence, DatasetLoader dl) throws Exception{
		 
		FusibleDataSet<Restaurant, Attribute> yelp = dl.getYelp();
		FusibleDataSet<Restaurant, Attribute> schemaOrg = dl.getSchemaOrg();
		FusibleDataSet<Restaurant, Attribute> datafiniti = dl.getDatafiniti();
		FusibleDataSet<Restaurant, Attribute> zomato = dl.getZomato();
		DataSet<Restaurant, Attribute> gs = dl.getGs();
	    CorrespondenceSet<Restaurant, Attribute> correspondences = dl.getCorrespondences();

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
		return accuracy;
	}
}
