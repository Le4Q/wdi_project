package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.RestaurantXMLReader;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class DatasetLoader {
	FusibleDataSet<Restaurant, Attribute> datafiniti = new FusibleHashedDataSet<>();
	FusibleDataSet<Restaurant, Attribute> yelp = new FusibleHashedDataSet<>();
	FusibleDataSet<Restaurant, Attribute> schemaOrg = new FusibleHashedDataSet<>();
	FusibleDataSet<Restaurant, Attribute> zomato = new FusibleHashedDataSet<>();
    DataSet<Restaurant, Attribute> gs = new FusibleHashedDataSet<>();
    CorrespondenceSet<Restaurant, Attribute> correspondences = new CorrespondenceSet<>();
	
	public FusibleDataSet<Restaurant, Attribute> getDatafiniti() {
		return datafiniti;
	}

	public CorrespondenceSet<Restaurant, Attribute> getCorrespondences() {
		return correspondences;
	}

	public void setCorrespondences(CorrespondenceSet<Restaurant, Attribute> correspondences) {
		this.correspondences = correspondences;
	}

	public void setDatafiniti(FusibleDataSet<Restaurant, Attribute> datafiniti) {
		this.datafiniti = datafiniti;
	}

	public FusibleDataSet<Restaurant, Attribute> getYelp() {
		return yelp;
	}

	public void setYelp(FusibleDataSet<Restaurant, Attribute> yelp) {
		this.yelp = yelp;
	}

	public FusibleDataSet<Restaurant, Attribute> getSchemaOrg() {
		return schemaOrg;
	}

	public void setSchemaOrg(FusibleDataSet<Restaurant, Attribute> schemaOrg) {
		this.schemaOrg = schemaOrg;
	}

	public FusibleDataSet<Restaurant, Attribute> getZomato() {
		return zomato;
	}

	public void setZomato(FusibleDataSet<Restaurant, Attribute> zomato) {
		this.zomato = zomato;
	}

	public DataSet<Restaurant, Attribute> getGs() {
		return gs;
	}

	public void setGs(DataSet<Restaurant, Attribute> gs) {
		this.gs = gs;
	}

	public DatasetLoader() {
		try {
		// Load the Data into FusibleDataSet
				System.out.println("*\n*\tLoading datasets\n*");
				
					new RestaurantXMLReader().loadFromXML(new File("data/input/datafiniti_target.xml"), "/restaurants/restaurant",
							datafiniti);
				
				datafiniti.printDataSetDensityReport();

				new RestaurantXMLReader().loadFromXML(new File("data/input/schemaOrg_target.xml"), "/restaurants/restaurant",
						schemaOrg);
				schemaOrg.printDataSetDensityReport();

				new RestaurantXMLReader().loadFromXML(new File("data/input/zomato_target.xml"), "/restaurants/restaurant",
						zomato);
				zomato.printDataSetDensityReport();

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
		        new RestaurantXMLReader().loadFromXML(new File("data/goldstandard/fusion_gold_train.xml"), "/restaurants/restaurant", gs);
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
