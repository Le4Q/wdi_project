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
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
	private String name;
	private String neighborhood;
	private String description;
	private double latitude;
	private double longitude;
	private PostalAddress postaladdress;
	private Reviews reviews;
	private PriceRange pricerange;
	private List<String> categories;
	private OpeningHours openinghours;
	private int stars;
	private boolean accepts_credit_cards;
	private boolean restaurant_delivery;
	private boolean accepts_reservations;
	private boolean drivethru;
	private boolean has_wifi;


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

	public String getName() {
		return name;
	}

	public void setName(String name) { this.name = name; }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) { this.description = description; }

	public double getLatitude() { return latitude; }

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() { return longitude; }

	public void setLongitude(double longitude) { this.longitude = longitude; }

	public String getNeighborhood() { return neighborhood; }

	public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

	public PostalAddress getPostalAddress() { return postaladdress; }

	public void setPostalAddress(PostalAddress postaladdress) { this.postaladdress = postaladdress; }

	public Reviews getReviews() { return reviews; }

	public void setReviews(Reviews reviews) { this.reviews = reviews; }

	public PriceRange getPriceRange() { return pricerange; }

	public void setPriceRange(PriceRange pricerange) { this.pricerange = pricerange; }

	public OpeningHours getOpeninghours() { return openinghours; }

	public void setOpeninghours(OpeningHours openinghours) { this.openinghours = openinghours; }

	public int getStars() { return stars; }

	public void setStars(int stars) { this.stars = stars; }

	public boolean getAcceptsCreditCards() { return accepts_credit_cards; }

	public void setAcceptsCreditCards(boolean accepts_credit_cards) {
		this.accepts_credit_cards = accepts_credit_cards;
	}

	public boolean getRestaurantDelivery() { return restaurant_delivery; }

	public void setRestaurantDelivery(boolean restaurant_delivery) {
		this.restaurant_delivery = restaurant_delivery;
	}

	public boolean getAcceptsReservations() { return accepts_reservations; }

	public void setAcceptsReservations(boolean accepts_reservations) {
		this.accepts_reservations = accepts_reservations;
	}

	public boolean getDrivethru() { return drivethru; }

	public void setDrivethru(boolean drivethru) {
		this.drivethru = drivethru;
	}

	public boolean getHasWifi() { return has_wifi; }

	public void setHasWifi(boolean has_wifi) {
		this.has_wifi = has_wifi;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	

	@Override
	public String toString() {
		return String.format("[Restaurant %s: %s]", getIdentifier(), getName());
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

