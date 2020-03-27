package video.segment.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import utility.MiscUtility;

public class LCSVideoSegment {

	ArrayList<Integer> results;

	public LCSVideoSegment(ArrayList<Integer> results) {
		this.results = results;
	}

	protected double getCentroid(ArrayList<Integer> candidateCluster) {
		// considering median of the sequence
		double[] numbers = new double[candidateCluster.size()];
		int index = 0;
		for (int val : candidateCluster) {
			numbers[index++] = val;
		}
		Median median = new Median();
		return median.evaluate(numbers);
	}

	protected ArrayList<Integer> mergeClosestClusters(ArrayList<ArrayList<Integer>> clusters) {

		ArrayList<Integer> merged = new ArrayList<Integer>();
		double minDiff = Double.MAX_VALUE;
		int seqLength = 0;

		for (int i = 0; i < clusters.size(); i++) {
			ArrayList<Integer> first = clusters.get(i);
			double centroid1 = getCentroid(first);
			for (int j = i + 1; j < clusters.size(); j++) {
				ArrayList<Integer> second = clusters.get(j);
				double centroid2 = getCentroid(second);
				double clusterDiff = Math.abs(centroid1 - centroid2);
				// looks like we have two constraints to meet
				// lower distance and maximum size, but lets focus on only closest distance.
				// we can add also timing constraint here
				// thus we need to CSP approach.
				if (clusterDiff < minDiff) {
					minDiff = clusterDiff;

					if ((first.size() + second.size()) > seqLength) {
						merged.clear();
						merged.addAll(first);
						merged.addAll(second);
					}
				}
			}
		}
		return merged;
	}

	protected ArrayList<Integer> determineLCSforVS() {

		HashSet<Integer> uniques = new HashSet<Integer>(this.results);
		HashMap<Integer, Integer> dictMap = new HashMap<Integer, Integer>();
		ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();

		for (int key : uniques) {
			dictMap.put(key, 1);
		}
		int sslength = Integer.MIN_VALUE;

		ArrayList<Integer> lcs = new ArrayList<Integer>();

		for (int number : uniques) {
			if (!dictMap.containsKey(number - 1)) {
				int j = number;
				ArrayList<Integer> tempBox = new ArrayList<Integer>();
				while (dictMap.containsKey(j)) {
					tempBox.add(j);
					j++;
				}

				// System.out.println(tempBox);
				clusters.add(tempBox);

				if (tempBox.size() > sslength) {
					sslength = tempBox.size();
					lcs.clear();
					lcs.addAll(tempBox);
					// System.out.println(lcs);
				}
			}
		}

		System.out.println("All consecutive sections:");
		System.out.println(clusters);

		System.out.println("Longest consecutive section:");
		System.out.println(lcs);

		System.out.println("Suggested consecutive section");
		ArrayList<Integer> suggested = mergeClosestClusters(clusters);
		System.out.println(suggested);

		return suggested;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 3, 10, 3, 11, 4, 5, 7, 8, 12, 2 };
		ArrayList<Integer> results = MiscUtility.array2ListInt(a);
		new LCSVideoSegment(results).determineLCSforVS();
	}
}
