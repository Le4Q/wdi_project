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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Actor;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.ActorXMLReader;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

/**
 * A {@link XMLMatchableReader} for {@link Restaurant}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class RestaurantXMLReader extends XMLMatchableReader<Restaurant, Attribute>  {

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.model.io.XMLMatchableReader#initialiseDataset(de.uni_mannheim.informatik.wdi.model.DataSet)
	 */
	@Override
	protected void initialiseDataset(DataSet<Restaurant, Attribute> dataset) {
		super.initialiseDataset(dataset);
		
	}
	
	private Double parseDouble(String in) {
		if (in != null)
			return Double.parseDouble(in);
		return null;
	}
	private Integer parseInt(String in) {
		if (in != null)
			return Integer.parseInt(in);
		return null;
	}
	
	private Node getNode(Node node, String childName) {

			// get all child nodes
			NodeList children = node.getChildNodes();

			// iterate over the child nodes until the node with childName is found
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);

				// check the node type and the name
				if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE
						&& child.getNodeName().equals(childName)) {

					return child;

				}
			}

			return null;
	}
	
	@Override
	public Restaurant createModelFromElement(Node node, String provenanceInfo) {
	
		String id = getValueFromChildElement(node, "id");
		// create the object with id and provenance information
		Restaurant restaurant = new Restaurant(id, provenanceInfo);
		String tmp;
		// fill the attributes

		tmp = getValueFromChildElement(node, "name");
		if (tmp != null) 
			restaurant.setName(tmp);
		
		tmp = getValueFromChildElement(node, "description");
		if (tmp != null) 
			restaurant.setDescription(tmp);
		
		tmp = getValueFromChildElement(node, "latitude");
		if (tmp != null) 
			restaurant.setLatitude(parseDouble(tmp));

		tmp = getValueFromChildElement(node, "longitude");
		if (tmp != null) 
			restaurant.setLongitude(parseDouble(tmp));
		
		tmp = getValueFromChildElement(node, "neighbourhood");
		if (tmp != null) 
			restaurant.setNeighbourhood(tmp);
		
		tmp = getValueFromChildElement(getNode(node, "postaladdress"), "address");
		if (tmp != null) 
			restaurant.setAddress(tmp);
		
		List<String> categories = getListFromChildElement(node, "categories");
		if (categories != null) 
			restaurant.setCategories(categories);
				
		tmp = getValueFromChildElement(getNode(getNode(node, "postaladdress"), "city"), "name");
		if (tmp != null) 
			restaurant.setCity_name(tmp);
		
		tmp = getValueFromChildElement(getNode(getNode(node, "postaladdress"), "city"), "postalcode");
		if (tmp != null) 
			restaurant.setCity_postalcode(tmp);
		
		tmp = getValueFromChildElement(getNode(getNode(node, "postaladdress"), "city"), "state");
		if (tmp != null) 
			restaurant.setCity_state(tmp);
		
		tmp = getValueFromChildElement(getNode(getNode(node, "postaladdress"), "city"), "country");
		if (tmp != null) 
			restaurant.setCity_country(tmp);

		tmp = getValueFromChildElement(node, "count");
		if (tmp != null) 
			restaurant.setReview_count(parseInt(tmp));

		tmp = getValueFromChildElement(node, "bodies");
		if (tmp != null) 
			restaurant.setReview_bodies(tmp);
		
		tmp = getValueFromChildElement(node, "average_rating");
		if (tmp!=null)
			restaurant.setReview_averagerating(parseDouble(tmp));

		tmp = getValueFromChildElement(node, "stars");
		if (tmp != null) 
			restaurant.setStars(parseInt(tmp));
		

		// load the list of actors
/*		List<Restaurant> actors = getObjectListFromChildElement(node, "actors",
				"actor", new ActorXMLReader(), provenanceInfo);
		restaurant.setActors(actors);*/

		return restaurant;
	}

}
