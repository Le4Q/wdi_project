package de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;

import org.apache.commons.math3.ml.distance.EuclideanDistance;


public class RestaurantGeolocationComparator implements Comparator<Restaurant, Attribute> {

	private static final long serialVersionUID = 1L;
	private EuclideanDistance sim = new EuclideanDistance();
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(Restaurant record1, Restaurant record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
    	
		double similarity = sim.compute(new double[] {record1.getLatitude(), record1.getLongitude()}, new double[] {record2.getLatitude(), record2.getLongitude()});
    	
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());

			this.comparisonLog.setRecord1Value(record1.getLatitude()+"");
			this.comparisonLog.setRecord1Value(record1.getLongitude()+"");
			this.comparisonLog.setRecord2Value(record2.getLatitude()+"");
			this.comparisonLog.setRecord2Value(record2.getLongitude()+"");
    	
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
