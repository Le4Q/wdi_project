/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package restaurants.identityResolution.comparators;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;
import restaurants.identityResolution.model.Restaurant;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;

/**
 * {@link Comparator} for {@link Movie}s based on the {@link Movie#getDate()}
 * value, with a maximal difference of 2 years.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
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
