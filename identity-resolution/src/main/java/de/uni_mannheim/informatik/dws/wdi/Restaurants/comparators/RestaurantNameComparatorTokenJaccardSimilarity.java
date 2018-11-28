
package de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.JaccardOnNGramsSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

import java.util.function.Function;


public class RestaurantNameComparatorTokenJaccardSimilarity implements Comparator<Restaurant, Attribute> {

	private static final long serialVersionUID = 1L;
	private TokenizingJaccardSimilarity sim = new TokenizingJaccardSimilarity();

	private ComparatorLogger comparisonLog;

	private Function<String,String> fn;
	private boolean removeCityName = false;

	public RestaurantNameComparatorTokenJaccardSimilarity(){
		this.fn = ComparatorUtils::def;
	}
	public RestaurantNameComparatorTokenJaccardSimilarity(Function<String,String> fn){
		this.fn = fn;
	}
	public RestaurantNameComparatorTokenJaccardSimilarity(Function<String,String> fn, boolean removeCityName){
		this.fn = fn;
		this.removeCityName = removeCityName;
	}

	@Override
	public double compare(
			Restaurant record1,
			Restaurant record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		String s1 = record1.getName();
		String s2 = record2.getName();

		// kind of hacky, append city name to restaurant name, will use that information to remove the city name from the restaurant name string
		if(removeCityName){
			s1 += ";" + record1.getPostalAddress().getCity().getName();
			s2 += ";" + record2.getPostalAddress().getCity().getName();
		}

		s1 = fn.apply(s1);
		s2 = fn.apply(s2);

    	double similarity = sim.calculate(s1, s2);
    	
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(s1);
			this.comparisonLog.setRecord2Value(s2);
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
		}
		
		return similarity;
	}

	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

}
