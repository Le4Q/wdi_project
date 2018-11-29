package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion.Strategies;

public class Main {

	public static void main(String[] args) {

        ArrayList<ArrayList<Double>> source = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        a = new ArrayList<>();
		a.add(1d);
		a.add(2d);
		a.add(3d);
		a.add(4d);
        source.add(a);
        List<List<Double>> temp = new ArrayList<List<Double>>();
        for (ArrayList<Double> list : source) {
            temp.add(Collections.unmodifiableList(list));
        }
        List<List<Double>> out = Collections.unmodifiableList(temp);    
        List<List<Double>> x = Lists.cartesianProduct(out);

	}
}
