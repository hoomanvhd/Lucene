package video.segment.search;

import java.util.ArrayList;

public class LCS {

	
	
	ArrayList<Integer> groundTruth;
	ArrayList<Integer> returnedResults;

	public LCS(ArrayList<Integer> pseudogroundTruth, ArrayList<Integer> returnedResults) {
		this.groundTruth = pseudogroundTruth;
		this.returnedResults = returnedResults;
	}

	protected void determineLCS() {
		int m = this.groundTruth.size();
		int n = this.returnedResults.size();
		int[][] L = new int[m + 1][n + 1];
		// Following steps build L[m+1][n+1] in bottom up fashion. Note
		// that L[i][j] contains length of LCS of X[0..i-1] and Y[0..j-1]
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i == 0 || j == 0)
					L[i][j] = 0;
				else if (this.groundTruth.get(i - 1) == this.returnedResults.get(j - 1))
					L[i][j] = L[i - 1][j - 1] + 1;
				else
					L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);
			}
		}

		// Following code is used to print LCS
		int index = L[m][n];
		int temp = index;

		// Create a character array to store the lcs string
		int[] lcsBox = new int[index + 1];
		lcsBox[index] = -1; // Set the terminating character

		// Start from the right-most-bottom-most corner and
		// one by one store characters in lcs[]
		int i = m, j = n;
		while (i > 0 && j > 0) {
			// If current character in X[] and Y are same, then
			// current character is part of LCS
			if (this.groundTruth.get(i - 1) == this.returnedResults.get(j - 1)) {
				// Put current character in result
				lcsBox[index - 1] = this.groundTruth.get(i - 1);

				// reduce values of i, j and index
				i--;
				j--;
				index--;
			}

			// If not same, then find the larger of two and
			// go in the direction of larger value
			else if (L[i - 1][j] > L[i][j - 1])
				i--;
			else
				j--;
		}

		for (int k = 0; k < temp; k++)
			System.out.print(lcsBox[k] + " ");
	}

	public static void main(String[] args) {
		ArrayList<Integer> gold = new ArrayList<>();
		gold.add(1);
		gold.add(2);
		gold.add(5);
		gold.add(6);

		ArrayList<Integer> result = new ArrayList<>();
		result.add(2);
		result.add(3);
		result.add(1);
		result.add(5);
		result.add(10);
		result.add(6);
		new LCS(gold, result).determineLCS();
	}
}
