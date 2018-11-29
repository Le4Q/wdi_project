package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.OpeningHours;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.PostalAddress;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.PriceRange;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.DatasetLoader;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionArrayList;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionProcessor;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionRunConfiguration;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionStrategyFactory;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.Strategies;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.SupervisedFusionFeature;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.Voting;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.list.Union;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class GenericSupervisedFuser {

    public static void main(String args[]) throws Exception{
       
      
    	
    	 
        ArrayList<FusionArrayList> features = new ArrayList<>();

        ArrayList<SupervisedFusionFeature> sfFeatures;
        sfFeatures = (new FusionStrategyFactory<PriceRange>()).createFusionStrategies(Strategies.union(Strategies.MOSTRECENT, Strategies.FAVOURSOURCES), Restaurant.PRICERANGE, "getPriceRange", "setPriceRange", PriceRange.class, new PriceRangeEvaluationRule());
        features.add(new FusionArrayList(Restaurant.PRICERANGE, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<String>()).createFusionStrategies(Strategies.union(Strategies.LONGESTSTRING, Strategies.SHORTESTSTRING), Restaurant.NAME, "getName", "setName", String.class, new NameEvaluationRule());
        features.add(new FusionArrayList(Restaurant.NAME, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<Double>()).createFusionStrategies(Strategies.union(Strategies.VOTING), Restaurant.LATITUDE, "getLatitude", "setLatitude", double.class, new LatitudeEvaluationRule());
        features.add(new FusionArrayList(Restaurant.LATITUDE, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<Double>()).createFusionStrategies(Strategies.union(Strategies.VOTING), Restaurant.LONGITUDE, "getLongitude", "setLongitude", double.class, new LongitudeEvaluationRule());
        features.add(new FusionArrayList(Restaurant.LONGITUDE, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<PostalAddress>()).createFusionStrategies(Strategies.union(Strategies.MOSTRECENT, Strategies.FAVOURSOURCES), Restaurant.POSTALADDRESS, "getPostalAddress", "setPostalAddress", PostalAddress.class, new PostalAddressEvaluationRule());
        features.add(new FusionArrayList(Restaurant.POSTALADDRESS, sfFeatures));
        //sfFeatures = (new FusionStrategyFactory<OpeningHours>()).createFusionStrategies(Strategies.union(Strategies.MOSTRECENT, Strategies.FAVOURSOURCES), Restaurant.OPENINGHOURS, "getOpeninghours", "setOpeninghours", OpeningHours.class, new OpeningHoursEvaluationRule());
        //features.add(new FusionArrayList(Restaurant.OPENINGHOURS, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<String>()).createFusionStrategies(Strategies.union(Strategies.LONGESTSTRING, Strategies.SHORTESTSTRING), Restaurant.NEIGHBORHOOD, "getNeighborhood", "setNeighborhood", String.class, new NeighborhoodEvaluationRule());
        features.add(new FusionArrayList(Restaurant.NEIGHBORHOOD, sfFeatures));
        /*sfFeatures = (new FusionStrategyFactory<Integer>()).createFusionStrategies(Strategies.union(Strategies.ALL), Restaurant.STARS, "getStars", "setStars", int.class, new StarsEvaluationRule());
        features.add(new FusionArrayList(Restaurant.STARS, sfFeatures));*/
        sfFeatures = (new FusionStrategyFactory<Boolean>()).createFusionStrategies(Strategies.union(Strategies.FAVOURSOURCES), Restaurant.ACCEPTSCREDITCARDS, "getAcceptsCreditCards", "setAcceptsCreditCards", boolean.class, new AcceptsCreditCardsEvaluationRule());
        features.add(new FusionArrayList(Restaurant.ACCEPTSCREDITCARDS, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<Boolean>()).createFusionStrategies(Strategies.union(Strategies.FAVOURSOURCES), Restaurant.RESTAURANTDELIVERY, "getRestaurantDelivery", "setRestaurantDelivery", boolean.class, new RestaurantDeliveryEvaluationRule());
        features.add(new FusionArrayList(Restaurant.RESTAURANTDELIVERY, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<Boolean>()).createFusionStrategies(Strategies.union(Strategies.FAVOURSOURCES), Restaurant.ACCEPTSRESERVATIONS, "getAcceptsReservations", "setAcceptsReservations", boolean.class, new AcceptsReservationsEvaluationRule());
        features.add(new FusionArrayList(Restaurant.ACCEPTSRESERVATIONS, sfFeatures));
        //sfFeatures = (new FusionStrategyFactory<Boolean>()).createFusionStrategies(Strategies.union(Strategies.FAVOURSOURCES), Restaurant.DRIVETHRU, "getDrivethru", "setDrivethru", boolean.class, new DrivethruEvaluationRule());
        //features.add(new FusionArrayList(Restaurant.DRIVETHRU, sfFeatures));
        sfFeatures = (new FusionStrategyFactory<Boolean>()).createFusionStrategies(Strategies.union(Strategies.FAVOURSOURCES), Restaurant.DRIVETHRU, "getHasWifi", "setHasWifi", boolean.class, new HasWifiEvaluationRule());
        features.add(new FusionArrayList(Restaurant.HASWIFI, sfFeatures));
        
        
        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new CategoriesFuserUnion(), new CategoriesEvaluationRule(), Restaurant.CATEGORIES));
        features.add(new FusionArrayList(Restaurant.CATEGORIES, sfFeatures));
        

        ArrayList<ArrayList<Double>> source = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        List<List<Double>> temp = new ArrayList<List<Double>>();
        for (ArrayList<Double> list : source) {
            temp.add(Collections.unmodifiableList(list));
        }
        List<List<Double>> out = Collections.unmodifiableList(temp);    
        List<List<Double>> weights = Lists.cartesianProduct(out);
        
        

		LinkedList<ImmutableList<SupervisedFusionFeature>> immutableFeatures = new LinkedList<>();
        for (int i = 0; i < features.size(); i++) {
        	ArrayList<SupervisedFusionFeature> _sfFeatures = features.get(i).getSupervisedFusionFeature();
        	immutableFeatures.add(ImmutableList.copyOf(_sfFeatures));
        }
        
        DatasetLoader dl = new DatasetLoader();
        
        List<List<SupervisedFusionFeature>> cartesianProduct = Lists.cartesianProduct(immutableFeatures);
        ArrayList<SupervisedFusionFeature> _features = new ArrayList<>();
    	FusionProcessor fp = new FusionProcessor();
    	ArrayList<FusionRunConfiguration> frcs = new ArrayList<>();
    	System.out.println("\n\n now running tests...\n");
        for (List<SupervisedFusionFeature> list : cartesianProduct) {
        	for (int i = 0; i < weights.size(); i++) {
	        	_features = new ArrayList<>();
	        	//System.out.println("\n\nRunning the following combination:");
	        	for (SupervisedFusionFeature supervisedFusionFeature : list) {
					//System.out.println(" - " + supervisedFusionFeature);
					_features.add(supervisedFusionFeature);
				}
	        	FusionRunConfiguration frc = new FusionRunConfiguration(_features);
	        	frc.setDatasetWeights(weights.get(i).toArray());
	        	
	        	frc.setFitness(fp.run(frc, false, dl));
	        	frcs.add(frc);
			}
		}
        
        Collections.sort(frcs);
        
        System.out.println("------------------------------------------------");
        System.out.println("No. of tests: " + frcs.size());
        System.out.println("Best result with accuracy="+frcs.get(frcs.size()-1).getFitness()+" found for the following setup:");
        for (SupervisedFusionFeature supervisedFusionFeature : frcs.get(frcs.size()-1).getSupervisedFusionFeature() ) {
			System.out.println(supervisedFusionFeature.toString());
		}
        System.out.println("Weights: " + frcs.get(frcs.size()-1).getDatasetWeights());
    	FusionRunConfiguration frc = new FusionRunConfiguration(frcs.get(frcs.size()-1).getSupervisedFusionFeature());
        
        System.out.println("Running the best setup again and saving results");
    	fp.run(frc, true, dl);
    	System.out.println("FINISHED.");
        
        
    }
}
