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
package restaurants.identityResolution.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

/**
 * A {@link AbstractRecord} representing a movie.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class Restaurant implements Matchable {

	/*
	 * example entry <movie> <id>academy_awards_2</id> <title>True Grit</title>
	 * <director> <name>Joel Coen and Ethan Coen</name> </director> <actors>
	 * <actor> <name>Jeff Bridges</name> </actor> <actor> <name>Hailee
	 * Steinfeld</name> </actor> </actors> <date>2010-01-01</date> </movie>
	 */

	protected String id;
	protected String provenance;
	protected String name;
	protected String description;
	protected double latitude;
	protected double longitude;
	protected String neighbourhood;
	protected String address;
	protected String city_name;
	protected String city_postalcode;
	protected String city_state;
	protected String city_country;
	protected int review_count;
	protected String review_bodies;
	protected double review_averagerating;
	protected int pricerange_lowerbound;
	protected int pricerange_upperbound;
	protected List<String> categories;
	protected int stars;

	public Restaurant(String identifier, String provenance) {
		id = identifier;
		this.provenance = provenance;
		categories = new LinkedList<>();
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getProvenance() {
		return provenance;
	}


	@Override
	public String toString() {
		return String.format("[Movie %s: %s / %s / %s]", getIdentifier(), getName());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getNeighbourhood() {
		return neighbourhood;
	}

	public void setNeighbourhood(String neighbourhood) {
		this.neighbourhood = neighbourhood;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getCity_postalcode() {
		return city_postalcode;
	}

	public void setCity_postalcode(String city_postalcode) {
		this.city_postalcode = city_postalcode;
	}

	public String getCity_state() {
		return city_state;
	}

	public void setCity_state(String city_state) {
		this.city_state = city_state;
	}

	public String getCity_country() {
		return city_country;
	}

	public void setCity_country(String city_country) {
		this.city_country = city_country;
	}

	public int getReview_count() {
		return review_count;
	}

	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}

	public String getReview_bodies() {
		return review_bodies;
	}

	public void setReview_bodies(String review_bodies) {
		this.review_bodies = review_bodies;
	}

	public double getReview_averagerating() {
		return review_averagerating;
	}

	public void setReview_averagerating(double review_averagerating) {
		this.review_averagerating = review_averagerating;
	}

	public int getPricerange_lowerbound() {
		return pricerange_lowerbound;
	}

	public void setPricerange_lowerbound(int pricerange_lowerbound) {
		this.pricerange_lowerbound = pricerange_lowerbound;
	}

	public int getPricerange_upperbound() {
		return pricerange_upperbound;
	}

	public void setPricerange_upperbound(int pricerange_upperbound) {
		this.pricerange_upperbound = pricerange_upperbound;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Restaurant){
			return this.getIdentifier().equals(((Restaurant) obj).getIdentifier());
		}else
			return false;
	}
	
	
	
}
