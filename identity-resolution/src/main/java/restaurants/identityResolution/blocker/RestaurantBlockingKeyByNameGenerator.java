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

package restaurants.identityResolution.blocker;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.BlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import restaurants.identityResolution.model.Restaurant;

/**
 * {@link BlockingKeyGenerator} for {@link Movie}s, which generates a blocking
 * key based on the year.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class RestaurantBlockingKeyByNameGenerator extends
		RecordBlockingKeyGenerator<Restaurant, Attribute> {

	private static final long serialVersionUID = 1L;


	@Override
	public void generateBlockingKeys(Restaurant record,
			Processable<Correspondence<Attribute, Matchable>> correspondences,
			DataIterator<Pair<String, Restaurant>> resultCollector) {

		String blockingKeyValue = record.getName().substring(0, Math.min(3,record.getName().length())).toUpperCase()
				+ record.getCity_name().substring(0, Math.min(3,record.getName().length())).toUpperCase();

		resultCollector.next(new Pair<>(blockingKeyValue, record));
	}

}
