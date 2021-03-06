
package de.uni_mannheim.informatik.dws.wdi.Restaurants.model;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RestaurantXMLReader extends XMLMatchableReader<Restaurant, Attribute>  {

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.model.io.XMLMatchableReader#initialiseDataset(de.uni_mannheim.informatik.wdi.model.DataSet)
	 */
	@Override
	protected void initialiseDataset(DataSet<Restaurant, Attribute> dataset) {
		super.initialiseDataset(dataset);

		dataset.addAttribute(Restaurant.NAME);
		dataset.addAttribute(Restaurant.NEIGHBORHOOD);
		dataset.addAttribute(Restaurant.DESCRIPTION);
		dataset.addAttribute(Restaurant.LATITUDE);
		dataset.addAttribute(Restaurant.LONGITUDE);
		dataset.addAttribute(Restaurant.POSTALADDRESS);
		dataset.addAttribute(Restaurant.REVIEWS);
		dataset.addAttribute(Restaurant.PRICERANGE);
		dataset.addAttribute(Restaurant.CATEGORIES);
		dataset.addAttribute(Restaurant.OPENINGHOURS);
		dataset.addAttribute(Restaurant.STARS);
		dataset.addAttribute(Restaurant.ACCEPTSCREDITCARDS);
		dataset.addAttribute(Restaurant.RESTAURANTDELIVERY);
		dataset.addAttribute(Restaurant.ACCEPTSRESERVATIONS);
		dataset.addAttribute(Restaurant.DRIVETHRU);
		dataset.addAttribute(Restaurant.HASWIFI);
		dataset.addAttribute(City.COUNTRY);
	}
	
	@Override
	public Restaurant createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Restaurant restaurant = new Restaurant(id, provenanceInfo);

		// fill the attributes
		restaurant.setName(getValueFromChildElement(node, "name"));
		restaurant.setNeighborhood(getValueFromChildElement(node, "neighborhood"));
		restaurant.setDescription(getValueFromChildElement(node, "description"));
		try { restaurant.setStars(Integer.parseInt(getValueFromChildElement(node, "stars"))); } catch (Exception e) {}
		try { restaurant.setLatitude(Double.parseDouble(getValueFromChildElement(node, "latitude"))); } catch (Exception e){ }
		try { restaurant.setLongitude(Double.parseDouble(getValueFromChildElement(node, "longitude"))); }catch (Exception e){ }
		/*
		try { restaurant.setAcceptsCreditCards(Boolean.parseBoolean(getValueFromChildElement(node, "accepts_credit_cards"))); } catch (Exception e) {}
		try { restaurant.setRestaurantDelivery(Boolean.parseBoolean(getValueFromChildElement(node, "restaurant_delivery"))); } catch (Exception e) {}
		try { restaurant.setAcceptsReservations(Boolean.parseBoolean(getValueFromChildElement(node, "accepts_reservations"))); } catch (Exception e) {}
		try { restaurant.setDrivethru(Boolean.parseBoolean(getValueFromChildElement(node, "drivethru"))); } catch (Exception e) {}
		try { restaurant.setHasWifi(Boolean.parseBoolean(getValueFromChildElement(node, "has_wifi"))); } catch (Exception e) {}
		*/

		if(getValueFromChildElement(node, "accepts_credit_cards") != null && (getValueFromChildElement(node, "accepts_credit_cards").equals("true")
				|| getValueFromChildElement(node, "accepts_credit_cards").equals("false")))
			try { restaurant.setAcceptsCreditCards(Boolean.parseBoolean(getValueFromChildElement(node, "accepts_credit_cards"))); } catch (Exception e) {}
		if(getValueFromChildElement(node, "restaurant_delivery") != null && (getValueFromChildElement(node, "restaurant_delivery").equals("true")
				|| getValueFromChildElement(node, "restaurant_delivery").equals("false")))
			try { restaurant.setRestaurantDelivery(Boolean.parseBoolean(getValueFromChildElement(node, "restaurant_delivery"))); } catch (Exception e) {}
		if(getValueFromChildElement(node, "accepts_reservations") != null && (getValueFromChildElement(node, "accepts_reservations").equals("true")
				|| getValueFromChildElement(node, "accepts_reservations").equals("false")))
			try { restaurant.setAcceptsReservations(Boolean.parseBoolean(getValueFromChildElement(node, "accepts_reservations"))); } catch (Exception e) {}
		if(getValueFromChildElement(node, "drivethru") != null && (getValueFromChildElement(node, "drivethru").equals("true")
				|| getValueFromChildElement(node, "drivethru").equals("false")))
			try { restaurant.setDrivethru(Boolean.parseBoolean(getValueFromChildElement(node, "drivethru"))); } catch (Exception e) {}
		if(getValueFromChildElement(node, "has_wifi") != null && (getValueFromChildElement(node, "has_wifi").equals("true")
				|| getValueFromChildElement(node, "has_wifi").equals("false")))
			try { restaurant.setHasWifi(Boolean.parseBoolean(getValueFromChildElement(node, "has_wifi"))); } catch (Exception e) {}


		if(node instanceof Element) {
			Element nodeEle = (Element)node;


			Node postaladdressNode = nodeEle.getElementsByTagName("postaladdress").item(0);
			Node reviewsNode = nodeEle.getElementsByTagName("reviews").item(0);
			Node pricerangeNode = nodeEle.getElementsByTagName("pricerange").item(0);
			Node openinghoursNode = nodeEle.getElementsByTagName("openinghours").item(0);

			PostalAddress postaladdress = new PostalAddress();

			if (postaladdressNode instanceof Element) {
				Element postaladdressEle = (Element) postaladdressNode;
				Node address = postaladdressEle.getElementsByTagName("address").item(0);
				Node cityNode = postaladdressEle.getElementsByTagName("city").item(0);
				try { postaladdress.setAddress(address.getTextContent()); } catch (Exception e) { }

				if (cityNode instanceof Element) {
					Element cityEle = (Element) cityNode;
					Node nameNode = cityEle.getElementsByTagName("name").item(0);
					Node postalcodeNode = cityEle.getElementsByTagName("postalcode").item(0);
					Node stateNode = cityEle.getElementsByTagName("state").item(0);
					Node countryNode = cityEle.getElementsByTagName("country").item(0);

					City city = new City(provenanceInfo);
					try { city.setName(nameNode.getTextContent()); } catch (Exception e) { }
					try { city.setPostalCode(postalcodeNode.getTextContent()); } catch (Exception e) { }
					try { city.setState(stateNode.getTextContent()); } catch (Exception e) { }
					try { city.setCountry(countryNode.getTextContent()); } catch (Exception e) { }
					postaladdress.setCity(city);

				}
			}

			restaurant.setPostalAddress(postaladdress);

			if (reviewsNode instanceof Element) {
				Element reviewsEle = (Element) reviewsNode;

				Node countNode = reviewsEle.getElementsByTagName("count").item(0);
				Node bodiesNode = reviewsEle.getElementsByTagName("bodies").item(0);
				Node averageratingNode = reviewsEle.getElementsByTagName("average_rating").item(0);

				Reviews reviews = new Reviews();
				try { reviews.setCount(Integer.parseInt(countNode.getTextContent())); } catch (Exception e) { }
				try { reviews.setBodies(bodiesNode.getTextContent()); } catch (Exception e) { }
				try { reviews.setAverageRating(Double.parseDouble(averageratingNode.getTextContent())); } catch (Exception e) { }

				restaurant.setReviews(reviews);
			}

			if (pricerangeNode instanceof Element) {
				Element pricerangeEle = (Element) pricerangeNode;

				Node lowerboundNode = pricerangeEle.getElementsByTagName("lowerBound").item(0);
				Node upperboundNode = pricerangeEle.getElementsByTagName("upperBound").item(0);

				PriceRange pricerange = new PriceRange();
				try { pricerange.setLowerBound(Integer.parseInt(lowerboundNode.getTextContent())); } catch (Exception e) { }
				try { pricerange.setUpperBound(Integer.parseInt(upperboundNode.getTextContent())); } catch (Exception e) { }

				restaurant.setPriceRange(pricerange);
			}

			if (openinghoursNode instanceof Element) {
				Element openinghoursEle = (Element) openinghoursNode;

				NodeList dayNodes = openinghoursEle.getElementsByTagName("specification");
				List<Day> days = new ArrayList<Day>();

				for (int i = 0; i < dayNodes.getLength(); i++) {
					Element dayEle = (Element) dayNodes.item(i);
					Day day = new Day();
					day.setName(dayEle.getElementsByTagName("day").item(0).getTextContent());
					try { day.setOpens(dayEle.getElementsByTagName("opens").item(0).getTextContent()); } catch (ParseException e) {}
					try { day.setCloses(dayEle.getElementsByTagName("closes").item(0).getTextContent()); } catch (ParseException e) {}
					days.add(day);
				}
				if (!days.isEmpty()) {
					OpeningHours openinghours = new OpeningHours();
					openinghours.setDays(days);
					restaurant.setOpeninghours(openinghours);
				}

			}

		}

		// load the list of actors
		List<String> categories = getListFromChildElement(node, "categories");
		restaurant.setCategories(categories);

		return restaurant;
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

		HashedDataSet<Restaurant, Attribute> yelpRestaurants = new HashedDataSet<>();
		new RestaurantXMLReader().loadFromXML(new File("identity-resolution/data/input/yelp_target.xml"), "restaurants/restaurant", yelpRestaurants);

		Collection<Restaurant> test = yelpRestaurants.get();

		int counter = 0;

		for (Restaurant res : test) {
			System.out.println("++++++++ Restaurant " + counter +  " +++++++++++");
			System.out.println(res.getIdentifier());

			System.out.println(res.getName());
			System.out.println(res.getPostalAddress().getCity().getName());
			System.out.println(res.getReviews().getCount());
			System.out.println();

			counter ++;
			if (counter > 10){
				break;
			}

		}

	}

}
