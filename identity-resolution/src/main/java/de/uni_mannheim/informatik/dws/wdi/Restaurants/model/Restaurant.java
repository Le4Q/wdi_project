
package de.uni_mannheim.informatik.dws.wdi.Restaurants.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.util.LinkedList;
import java.util.List;

public class Restaurant extends AbstractRecord<Attribute> {


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
	private Integer stars;
	private Boolean accepts_credit_cards;
	private Boolean restaurant_delivery;
	private Boolean accepts_reservations;
	private Boolean drivethru;
	private Boolean has_wifi;


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

	public Integer getStars() { return stars; }

	public void setStars(int stars) { this.stars = stars; }

	public Boolean getAcceptsCreditCards() { return accepts_credit_cards; }

	public void setAcceptsCreditCards(boolean accepts_credit_cards) {
		this.accepts_credit_cards = accepts_credit_cards;
	}

	public Boolean getRestaurantDelivery() { return restaurant_delivery; }

	public void setRestaurantDelivery(boolean restaurant_delivery) {
		this.restaurant_delivery = restaurant_delivery;
	}

	public Boolean getAcceptsReservations() { return accepts_reservations; }

	public void setAcceptsReservations(boolean accepts_reservations) {
		this.accepts_reservations = accepts_reservations;
	}

	public Boolean getDrivethru() { return drivethru; }

	public void setDrivethru(boolean drivethru) {
		this.drivethru = drivethru;
	}

	public Boolean getHasWifi() { return has_wifi; }

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

	public static final Attribute NAME = new Attribute("Name");
	public static final Attribute NEIGHBORHOOD = new Attribute("Neighborhood");
	public static final Attribute DESCRIPTION = new Attribute("Description");
	public static final Attribute POSTALADDRESS = new Attribute("PostalAddress");
	public static final Attribute LATITUDE = new Attribute("Latitude");
	public static final Attribute LONGITUDE = new Attribute("Longitude");
	public static final Attribute REVIEWS = new Attribute("Reviews");
	public static final Attribute PRICERANGE = new Attribute("PriceRange");
	public static final Attribute CATEGORIES = new Attribute("Categories");
	public static final Attribute OPENINGHOURS = new Attribute("OpeningHours");
	public static final Attribute STARS = new Attribute("Stars");
	public static final Attribute ACCEPTSCREDITCARDS = new Attribute("AcceptsCreditCards");
	public static final Attribute RESTAURANTDELIVERY = new Attribute("RestaurantDelivery");
	public static final Attribute ACCEPTSRESERVATIONS = new Attribute("AcceptsReservations");
	public static final Attribute DRIVETHRU = new Attribute("Drivethru");
	public static final Attribute HASWIFI = new Attribute("HasWifi");

	@Override
	public boolean hasValue(Attribute attribute) {
		if(attribute==NAME)
			return getName() != null && !getName().isEmpty();
		else if(attribute==NEIGHBORHOOD)
			return getNeighborhood() != null && !getNeighborhood().isEmpty();
		else if(attribute==DESCRIPTION)
			return getDescription() != null && !getDescription().isEmpty();
		else if(attribute==POSTALADDRESS)
			return (getPostalAddress().getAddress() != null && !getPostalAddress().getAddress().isEmpty()) ||
					(getPostalAddress().getCity() != null && !getPostalAddress().getCity().getName().isEmpty());
		else if(attribute==LATITUDE)
			return getLatitude() != 0.0;
		else if(attribute==LONGITUDE)
			return getLongitude() != 0.0;
		else if(attribute ==REVIEWS)
			if(getReviews() != null)
				return getReviews().getCount() >= 0;
			else
				return false;
		else if(attribute==PRICERANGE)
			return getPriceRange() != null;
		else if(attribute==CATEGORIES)
			return getCategories() != null && !getCategories().isEmpty();
		else if(attribute==OPENINGHOURS)
			return getOpeninghours() != null && getOpeninghours().getDays() != null;
		else if(attribute==STARS)
			return getStars() != null;
		else if(attribute==ACCEPTSCREDITCARDS)
			return getAcceptsCreditCards() != null;
		else if(attribute==RESTAURANTDELIVERY)
			return getRestaurantDelivery() != null;
		else if(attribute==ACCEPTSRESERVATIONS)
			return getAcceptsReservations() != null;
		else if(attribute==DRIVETHRU)
			return getDrivethru() != null;
		else if(attribute==HASWIFI)
			return getHasWifi() != null;
		else
			return false;
	}
}

