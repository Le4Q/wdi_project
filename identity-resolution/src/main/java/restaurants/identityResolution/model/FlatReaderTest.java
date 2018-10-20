package restaurants.identityResolution.model;

import java.io.File;

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.MovieBlockingKeyByDecadeGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.MovieBlockingKeyByTitleGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.MovieDateComparator10Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.MovieDateComparator2Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.MovieTitleComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.MovieTitleComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.MovieXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class ReaderTest 
{
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
	
    public static void main( String[] args ) throws Exception
    {
		// loading data
		System.out.println("*\n*\tLoading datasets\n*");
		HashedDataSet<Restaurant, Attribute> fastfood_restaurants = new HashedDataSet<>();
		new RestaurantXMLReader().loadFromXML(new File("data/input/datafiniti_target.xml"), "/restaurants/restaurant", fastfood_restaurants);


		//System.out.println("*\n*\tLoading datasets\n*");
		//HashedDataSet<Movie, Attribute> dataAcademyAwards = new HashedDataSet<>();
		//new MovieXMLReader().loadFromXML(new File("data/input/academy_awards.xml"), "/movies/movie", dataAcademyAwards);
    }
}
