package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.util.ArrayList;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.main.SupervisedFusionFeature;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class FusionRunConfiguration {

	private ArrayList<SupervisedFusionFeature> supervisedFusionFeature = null;
	private double fitness = 0d;
	
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public FusionRunConfiguration(ArrayList<SupervisedFusionFeature> supervisedFusionFeature) {
		
		this.supervisedFusionFeature = supervisedFusionFeature;
	}
	
	public ArrayList<SupervisedFusionFeature> getSupervisedFusionFeature() {
		return supervisedFusionFeature;
	}
	public void setSupervisedFusionFeature(ArrayList<SupervisedFusionFeature> supervisedFusionFeature) {
		this.supervisedFusionFeature = supervisedFusionFeature;
	}
	
}
