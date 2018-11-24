package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.util.ArrayList;

public class FusionRunConfiguration implements Comparable<FusionRunConfiguration> {
   
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
	

	@Override 
	public int compareTo(FusionRunConfiguration arg0) {
		return (int) ((this.getFitness() - arg0.getFitness()) * 1000.0);
	} 
	
}
