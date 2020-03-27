package video.segment.search;

import java.util.ArrayList;
import java.util.HashMap;
import utility.MiscUtility;



public class LongestConsecutiveSeq {

	/**
	 * thanks to
	 * https://www.geeksforgeeks.org/printing-longest-increasing-consecutive-subsequence/
	 */

	int[] a;
	int n;

	public LongestConsecutiveSeq(ArrayList<Integer> returnedResults) {
		this.a = MiscUtility.list2ArrayInt(returnedResults);
		this.n = returnedResults.size();
	}

	protected ArrayList<Integer> determineLongestCosecSeq() {
		// stores the index of elements
		HashMap<Integer, Integer> mp = new HashMap<>();

		// stores the length of the longest
		// subsequence that ends with a[i]
		int[] dp = new int[n];

		int maximum = Integer.MIN_VALUE;

		// iterate for all element
		int index = -1;
		for (int i = 0; i < n; i++) {

			// if a[i]-1 is present before i-th index
			if (mp.get(a[i] - 1) != null) {

				// last index of a[i]-1
				int lastIndex = mp.get(a[i] - 1) - 1;

				// relation
				dp[i] = 1 + dp[lastIndex];
			} else
				dp[i] = 1;

			// stores the index as 1-index as we need to
			// check for occurrence, hence 0-th index
			// will not be possible to check
			mp.put(a[i], i + 1);

			// stores the longest length
			if (maximum < dp[i]) {
				maximum = dp[i];
				index = i;
			}
		}

		// We know last element of sequence is
		// a[index]. We also know that length
		// of subsequence is "maximum". So We
		// print these many consecutive elements
		// starting from "a[index] - maximum + 1"
		// to a[index].

		ArrayList<Integer> finalResults = new ArrayList<>();

		for (int curr = a[index] - maximum + 1; curr <= a[index]; curr++) {
			finalResults.add(curr);
		}
		
		System.out.println(finalResults);

		return finalResults;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 3, 10, 3, 11, 4, 5, 6, 7, 8, 12, 2 };
		ArrayList<Integer> results = MiscUtility.array2ListInt(a);
		System.out.println(new LongestConsecutiveSeq(results).determineLongestCosecSeq());
	}
}
