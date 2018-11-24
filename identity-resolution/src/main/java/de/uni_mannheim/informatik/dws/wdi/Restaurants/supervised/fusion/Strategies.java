package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.util.Iterator;

public class Strategies {



	public static final boolean[] VOTING = new boolean[] {true, false, false, false};
	public static final boolean[] MOSTRECENT = new boolean[] {false, true, false, false};
	public static final boolean[] FAVOURSOURCES = new boolean[] {false, false, true, false};
	public static final boolean[] LONGESTSTRING = new boolean[] {false, false, false, true};
	public static final boolean[] ALL = new boolean[] {true, true, true, false};
	
	public static boolean[] union(boolean[]... in) {
		if (in == null)
			return null;
		if (in.length<1)
			return null;
		boolean[] out = new boolean[in[0].length];
		for (int i = 0; i < out.length; i++) {
			out[i] = false;
		}
		for (int i = 0; i < in[0].length; i++) {
			for (int j = 0; j < in.length; j++) {
				out[i] = (out[i] || in[j][i]);
			}
		}
		return out;
	}
	
}
