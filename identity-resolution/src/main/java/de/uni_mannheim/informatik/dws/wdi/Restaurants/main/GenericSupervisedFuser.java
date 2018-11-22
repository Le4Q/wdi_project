package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.*;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.PriceRange;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionArrayList;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionProcessor;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.FusionRunConfiguration;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.Voting;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.list.Union;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class GenericSupervisedFuser {

    public static void main(String args[]) throws Exception{
       
        // add attribute fusers TODO:
        
        ArrayList<FusionArrayList> features = new ArrayList<>();

        ArrayList<SupervisedFusionFeature> sfFeatures = new ArrayList<>();
        ArrayList<ConflictResolutionFunction<PriceRange, Restaurant, Attribute>> fusingstrategies = new ArrayList<>();
        fusingstrategies.add(new Voting<PriceRange, Restaurant, Attribute>());
        fusingstrategies.add(new MostRecent<PriceRange, Restaurant, Attribute>());
        fusingstrategies.add(new FavourSources<PriceRange, Restaurant, Attribute>());
        fusingstrategies.add(new MostRecent<PriceRange, Restaurant, Attribute>());
        for (ConflictResolutionFunction<PriceRange, Restaurant, Attribute> strategy : fusingstrategies) {
        	GenericFuser<PriceRange, Restaurant, Attribute> fuser = new GenericFuser<PriceRange, Restaurant, Attribute>(Restaurant.PRICERANGE, Restaurant.class.getMethod("getPriceRange", null), Restaurant.class.getMethod("setPriceRange", PriceRange.class), strategy);	
            sfFeatures.add(new SupervisedFusionFeature(fuser,new PriceRangeEvaluationRule(), Restaurant.PRICERANGE));
        }
        features.add(new FusionArrayList(Restaurant.PRICERANGE, sfFeatures));
        

		LinkedList<ImmutableList<SupervisedFusionFeature>> immutableFeatures = new LinkedList<>();
        for (int i = 0; i < features.size(); i++) {
        	ArrayList<SupervisedFusionFeature> _sfFeatures = features.get(i).getSupervisedFusionFeature();
        	immutableFeatures.add(ImmutableList.copyOf(_sfFeatures));
        }
        
        List<List<SupervisedFusionFeature>> cartesianProduct = Lists.cartesianProduct(immutableFeatures);
        ArrayList<SupervisedFusionFeature> _features = new ArrayList<>();
    	FusionProcessor fp = new FusionProcessor();
        for (List<SupervisedFusionFeature> list : cartesianProduct) {
        	System.out.println("\n\nRunning the following combination:");
        	for (SupervisedFusionFeature supervisedFusionFeature : list) {
				System.out.println(" - " + supervisedFusionFeature);
				_features.add(supervisedFusionFeature);
			}
        	FusionRunConfiguration frc = new FusionRunConfiguration(_features);
        	
        	frc.setFitness(fp.run(frc, false));
		}
        
    }
}
