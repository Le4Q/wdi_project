
package de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

import java.util.function.Function;


public class RestaurantAddressComparatorLevenshtein implements Comparator<Restaurant, Attribute> {

	private static final long serialVersionUID = 1L;
	private LevenshteinSimilarity sim = new LevenshteinSimilarity();
	
	private ComparatorLogger comparisonLog;

	private Function<String,String> fn;

	public RestaurantAddressComparatorLevenshtein(){
		this.fn = ComparatorUtils::def;
	}
	public RestaurantAddressComparatorLevenshtein(Function<String,String> fn){
		this.fn = fn;
	}

	@Override
	public double compare(
			Restaurant record1,
			Restaurant record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		String s1 = record1.getPostalAddress().getAddress();
		String s2 = record2.getPostalAddress().getAddress();

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
