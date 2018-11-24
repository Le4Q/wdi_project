package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.util.ArrayList;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.PriceRangeEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.GenericFuser;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.PriceRange;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.Voting;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.LongestString;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.ShortestString;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class FusionStrategyFactory<T> {

	
	public ArrayList<SupervisedFusionFeature> createFusionStrategies(boolean[] strategies, Attribute attribute, String getMethod, String setMethod, Class<T> class1, EvaluationRule<Restaurant, Attribute> evaluationRule) {

	    ArrayList<ConflictResolutionFunction<T, Restaurant, Attribute>> fusingstrategies = new ArrayList<>();
	    
	    if (strategies[0])
	    	fusingstrategies.add(new Voting<T, Restaurant, Attribute>());
	    if (strategies[1])
	    	fusingstrategies.add(new MostRecent<T, Restaurant, Attribute>());
	    if (strategies[2])
	    	fusingstrategies.add(new FavourSources<T, Restaurant, Attribute>());
	    if (strategies[3])
	    	fusingstrategies.add((ConflictResolutionFunction<T, Restaurant, Attribute>) new LongestString<Restaurant, Attribute>());

        ArrayList<SupervisedFusionFeature> sfFeatures = new ArrayList<>();
         
	    for (ConflictResolutionFunction<T, Restaurant, Attribute> strategy : fusingstrategies) {
        	GenericFuser<T, Restaurant, Attribute> fuser = null;
			try {
				fuser = new GenericFuser<T, Restaurant, Attribute>(attribute, Restaurant.class.getMethod(getMethod, null), Restaurant.class.getMethod(setMethod, class1), strategy);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
            sfFeatures.add(new SupervisedFusionFeature(fuser,evaluationRule, attribute));
        }   
	    return sfFeatures;
	}
}
