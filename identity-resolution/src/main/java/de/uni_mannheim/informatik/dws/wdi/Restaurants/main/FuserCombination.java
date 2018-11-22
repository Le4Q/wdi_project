package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import java.lang.reflect.ParameterizedType;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.evaluation.PriceRangeEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.PriceRange;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class FuserCombination<T> {

	public Attribute attribute;
	public String getMethod;
	public String setMethod;
	public Class datatype;
	public PriceRangeEvaluationRule evaluator;
	public T dummy;
	
	public FuserCombination(Attribute a, String b, String c, Class d, PriceRangeEvaluationRule e) {
		this.attribute = a;
		this.getMethod = b;
		this.setMethod = c;
		this.datatype = d;
		this.evaluator = e;
	}
	
}
