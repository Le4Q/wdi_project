package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionArrayList;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionProcessor;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionRunConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SupervisedFuser {

    public static void main(String args[]) throws Exception{
       
        // add attribute fusers TODO:
        
        ArrayList<FusionArrayList> features = new ArrayList<>();
        
        ArrayList<SupervisedFusionFeature> sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new NameFuserVoting(),new NameEvaluationRule(), Restaurant.NAME));
        features.add(new FusionArrayList(Restaurant.NAME, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new NeighborhoodFuserMostRecent(),new NameEvaluationRule(), Restaurant.NEIGHBORHOOD));
        features.add(new FusionArrayList(Restaurant.NEIGHBORHOOD, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new PostalAddressFuserMostRecent(), new PostalAddressEvaluationRule(), Restaurant.POSTALADDRESS));
        features.add(new FusionArrayList(Restaurant.POSTALADDRESS, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new CategoriesFuserUnion(), new CategoriesEvaluationRule(), Restaurant.CATEGORIES));
        features.add(new FusionArrayList(Restaurant.CATEGORIES, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new LatitudeFuserFavourSource(),new LatitudeEvaluationRule(), Restaurant.LATITUDE));
        features.add(new FusionArrayList(Restaurant.LATITUDE, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new LongitudeFuserFavourSources(),new LongitudeEvaluationRule(), Restaurant.LONGITUDE));
        features.add(new FusionArrayList(Restaurant.LONGITUDE, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new PriceRangeFuserVoting(),new PriceRangeEvaluationRule(), Restaurant.PRICERANGE));
        features.add(new FusionArrayList(Restaurant.PRICERANGE, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new CategoriesFuserUnion(), new CategoriesEvaluationRule(), Restaurant.CATEGORIES));
        features.add(new FusionArrayList(Restaurant.CATEGORIES, sfFeatures));

        sfFeatures = new ArrayList<>();
        sfFeatures.add(new SupervisedFusionFeature(new OpeningHoursFuserMostRecent(),new OpeningHoursEvaluationRule(), Restaurant.OPENINGHOURS));
        features.add(new FusionArrayList(Restaurant.OPENINGHOURS, sfFeatures));
        
        

		LinkedList<ImmutableList<SupervisedFusionFeature>> immutableFeatures = new LinkedList<>();
        for (int i = 0; i < features.size(); i++) {
        	ArrayList<SupervisedFusionFeature> _sfFeatures = features.get(i).getSupervisedFusionFeature();
        	immutableFeatures.add(ImmutableList.copyOf(_sfFeatures));
        }
        
        List<List<SupervisedFusionFeature>> cartesianProduct = Lists.cartesianProduct(immutableFeatures);
        ArrayList<SupervisedFusionFeature> _features = new ArrayList<>();
    	FusionProcessor fp = new FusionProcessor();
    	ArrayList<FusionRunConfiguration> frcs = new ArrayList<>();
        for (List<SupervisedFusionFeature> list : cartesianProduct) {
        	System.out.println("\n\nRunning the following combination:");
        	for (SupervisedFusionFeature supervisedFusionFeature : list) {
				System.out.println(" - " + supervisedFusionFeature);
				_features.add(supervisedFusionFeature);
			}
        	FusionRunConfiguration frc = new FusionRunConfiguration(_features);
        	
        	frc.setFitness(fp.run(frc, false));
        	frcs.add(frc);
		}
        
        Collections.sort(frcs);
        
        System.out.println("------------------------------------------------");
        System.out.println("Best result with accuracy="+frcs.get(0).getFitness()+" found for the following setup:");
        _features = new ArrayList<>();
        for (SupervisedFusionFeature supervisedFusionFeature : frcs.get(0).getSupervisedFusionFeature()) {
			System.out.println(" - " + supervisedFusionFeature);
			_features.add(supervisedFusionFeature);
		}
    	FusionRunConfiguration frc = new FusionRunConfiguration(_features);
        
        System.out.println("Running the best setup again and saving results");
    	fp.run(frc, true);
    	System.out.println("FINISHED.");
        
        
    }
}
