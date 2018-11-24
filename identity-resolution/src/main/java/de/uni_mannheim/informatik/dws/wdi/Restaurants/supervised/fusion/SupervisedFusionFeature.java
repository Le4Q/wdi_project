package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers.GenericFuser;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class SupervisedFusionFeature {
	

	private AttributeValueFuser<? extends Object, Restaurant, Attribute> attributeValueFuser = null;
	private EvaluationRule<Restaurant, Attribute> evaluationRule = null;
	private Attribute attribute = null; 
	
	public SupervisedFusionFeature(AttributeValueFuser<? extends Object, Restaurant, Attribute> attributeValueFuser, EvaluationRule<Restaurant, Attribute> evaluationRule, Attribute attribute) {
		this.attributeValueFuser = attributeValueFuser;
		this.evaluationRule = evaluationRule;
		this.attribute = attribute;
	}


	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public AttributeValueFuser<? extends Object, Restaurant, Attribute> getAttributeValueFuser() {
		return attributeValueFuser;
	}

	public void setAttributeValueFuser(AttributeValueFuser<? extends Object, Restaurant, Attribute> attributeValueFuser) {
		this.attributeValueFuser = attributeValueFuser;
	}

	public EvaluationRule<Restaurant, Attribute> getEvaluationRule() {
		return evaluationRule;
	}

	public void setEvaluationRule(EvaluationRule<Restaurant, Attribute> evaluationRule) {
		this.evaluationRule = evaluationRule;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public String toString() {
		if (attributeValueFuser instanceof GenericFuser) {
			return ((GenericFuser) attributeValueFuser).getValStrategy();
		}
		return attributeValueFuser.getClass().getName();
	}
	
	
	
	
	
}
