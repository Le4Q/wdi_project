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
package de.uni_mannheim.informatik.dws.wdi.Restaurants.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

/**
 * {@link XMLFormatter} for {@link Movie}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class RestaurantXMLFormatter extends XMLFormatter<Restaurant> {


	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("restaurants");
	}

	@Override
	public Element createElementFromRecord(Restaurant record, Document doc) {
		Element restaurant = doc.createElement("restaurant");
		
		restaurant.appendChild(createTextElement("id", record.getIdentifier(), doc));
		restaurant.appendChild(createTextElement("name", record.getName(), doc));
		restaurant.appendChild(createTextElement("description", record.getDescription(), doc));
		restaurant.appendChild(createTextElement("latitude", record.getLatitude(), doc));
		restaurant.appendChild(createTextElement("longitude", record.getLongitude(), doc));
		
		Element postaladdress = doc.createElement("postalcode");
		postaladdress.appendChild(createTextElement("address", record.getPostalAddress().getAddress(), doc));
		Element city = doc.createElement("city");
		city.appendChild(createTextElement("name", record.getPostalAddress().getCity().getName(), doc));
		city.appendChild(createTextElement("postalcode", record.getPostalAddress().getCity().getPostalCode(), doc));
		city.appendChild(createTextElement("state", record.getPostalAddress().getCity().getState(), doc));
		city.appendChild(createTextElement("country", record.getPostalAddress().getCity().getCountry(), doc));
		postaladdress.appendChild(city);
		restaurant.appendChild(postaladdress);
		
		//Element reviews = doc.createElement("reviews");
		//reviews.appendChild(createTextElement("count", record.getReviews().getCount(), doc));
		//reviews.appendChild(createTextElement("bodies", record.getReviews().getBodies(), doc));
		//reviews.appendChild(createTextElement("average_rating", record.getReviews().getAverageRating(), doc));
		//restaurant.appendChild(reviews);
		
		/*Element openinghours = doc.createElement("openinghours");
		for (Day day : record.getOpeninghours().getDays()) {
			openinghours.appendChild(createTextElement("day", day.getName(), doc));
			Element hours = doc.createElement("hours");
			hours.appendChild(createTextElement("opens", day.getOpens().toString(), doc));
			hours.appendChild(createTextElement("closes",  day.getCloses().toString(), doc));
			openinghours.appendChild(hours);
		}
		restaurant.appendChild(openinghours);*/
		
		Element pricerange = doc.createElement("pricerange");
		pricerange.appendChild(createTextElement("lowerBound", record.getPriceRange().getLowerBound(), doc));
		pricerange.appendChild(createTextElement("upperBound", record.getPriceRange().getUpperBound(), doc));
		restaurant.appendChild(pricerange);
		
		/*Element categories = doc.createElement("categories");
		for (String category : record.getCategories()) {
			categories.appendChild(createTextElement("category", category, doc));
		}
		restaurant.appendChild(categories);*/
		
		/*restaurant.appendChild(createTextElement("stars", record.getStars(), doc));
		restaurant.appendChild(createTextElement("accepts_credit_cards", record.getAcceptsCreditCards(), doc));
		restaurant.appendChild(createTextElement("restaurant_delivery", record.getRestaurantDelivery(), doc));
		restaurant.appendChild(createTextElement("accepts_reservations", record.getAcceptsReservations(), doc));
		restaurant.appendChild(createTextElement("drivethru", record.getDrivethru(), doc));*/
		//restaurant.appendChild(createTextElement("wheelchair_accessibility", record., doc));
		//restaurant.appendChild(createTextElement("has_wifi", record.getHasWifi(), doc));
		

		//Element websites = doc.createElement("categories");
		//for (String category : record.getWebsites()) {
		//	websites.appendChild(createTextElement("website", category, doc));
		//}
		//restaurant.appendChild(websites);
		
		restaurant.appendChild(createTextElement("accepts_currency", record.getAcceptsCreditCards(), doc));
		
		
		return restaurant;
	}

	protected Element createTextElementWithProvenance(String name,
		Object value, String provenance, Document doc) {
		if (value==null) {
			value = new Object();
		}
		Element elem = createTextElement(name, ""+value.toString(), doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createTextElement(String name, Object value, Document doc) {
			if (value==null) {
				value = new Object();
			}
		return createTextElement(name, value.toString(), doc);
	}


}
