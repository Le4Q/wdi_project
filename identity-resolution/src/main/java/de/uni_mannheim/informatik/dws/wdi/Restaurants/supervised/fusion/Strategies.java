package de.uni_mannheim.informatik.dws.wdi.Restaurants.supervised.fusion;

import java.util.Iterator;

public class Strategies {

	public static final boolean[] VOTING = new boolean[] { true, false, false, false, false };
	public static final boolean[] MOSTRECENT = new boolean[] { false, true, false, false, false };
	public static final boolean[] FAVOURSOURCES = new boolean[] { false, false, true, false, false };
	public static final boolean[] LONGESTSTRING = new boolean[] { false, false, false, true, false };
	public static final boolean[] SHORTESTSTRING = new boolean[] { false, false, false, false, true };
	public static final boolean[] ALL = new boolean[] { true, true, true, false, false };

	public static boolean[] union(boolean[]... in) {
		if (in == null)
			return null;
		if (in.length < 1)
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

	static void combinationUtil(int arr[], int data[], int start, int end, int index, int r) {
		// Current combination is ready to be printed, print it
		if (index == r) {
			for (int j = 0; j < r; j++)
				System.out.print(data[j] + " ");
			System.out.println("");
			return;
		}

		// replace index with all possible elements. The condition
		// "end-i+1 >= r-index" makes sure that including one element
		// at index will make a combination with remaining elements
		// at remaining positions
		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data[index] = arr[i];
			combinationUtil(arr, data, i + 1, end, index + 1, r);
		}
	}

	// The main function that prints all combinations of size r
	// in arr[] of size n. This function mainly uses combinationUtil()
	public static void printCombination(int arr[], int n, int r) {
		// A temporary array to store all combination one by one
		int data[] = new int[r];

		// Print all combination using temprary array 'data[]'
		combinationUtil(arr, data, 0, n - 1, 0, r);
	}

}
