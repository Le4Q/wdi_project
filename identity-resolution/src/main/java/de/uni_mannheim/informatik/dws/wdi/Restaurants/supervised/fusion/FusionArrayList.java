package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.util.ArrayList;

import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class FusionArrayList {

	private Attribute attribute = null;
	private ArrayList<SupervisedFusionFeature> supervisedFusionFeature = null;
	

	public FusionArrayList(Attribute attribute, ArrayList<SupervisedFusionFeature> supervisedFusionFeature) {
		this.attribute = attribute;
		this.supervisedFusionFeature = supervisedFusionFeature;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public ArrayList<SupervisedFusionFeature> getSupervisedFusionFeature() {
		return supervisedFusionFeature;
	}
	public void setSupervisedFusionFeature(ArrayList<SupervisedFusionFeature> supervisedFusionFeature) {
		this.supervisedFusionFeature = supervisedFusionFeature;
	}
	
}
