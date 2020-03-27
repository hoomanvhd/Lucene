/****
 * 
 * @author MasudRahman
 * Miscellaneous Utlity class 
 * 
 */
package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MiscUtility {	
	public static String list2Str(ArrayList<String> list) {
		String temp = new String();
	
		for (String item : list) {
		
			temp += item + " ";
		}
		return temp.trim();
	}

	
	
	public static String set2Str(HashSet<String> set) {
		String temp = new String();
		for (String item : set) {
			temp += item + " ";
		}
		return temp.trim();
	}

	public static ArrayList<String> str2List(String str) {
		return new ArrayList<>(Arrays.asList(str.split("\\s+")));
	}

	public static double[] list2Array(ArrayList<Integer> list) {
		double[] array = new double[list.size()];
		
		for (int index = 0; index < list.size(); index++) {
			array[index] = list.get(index);
		}
		
		return array;
	}

	public static int[] list2ArrayInt(ArrayList<Integer> list) {
		int[] array = new int[list.size()];
		for (int index = 0; index < list.size(); index++) {
			array[index] = list.get(index);
		}
		return array;
	}

	public static ArrayList<Integer> array2ListInt(int[] arr) {
		ArrayList<Integer> list = new ArrayList<>();
		for (int index = 0; index < arr.length; index++) {
			list.add(arr[index]);
		}
		return list;
	}

	public static double getAverage(ArrayList<Integer> itemList) {
		double sum = 0;
		for (int item : itemList) {
			sum += item;
		}
		return sum / itemList.size();
	}

	public static double getDoubleAverage(ArrayList<Double> itemList) {
		double sum = 0;
		for (double item : itemList) {
			sum += item;
		}
		return sum / itemList.size();
	}

	public static double getMax(ArrayList<Double> itemList) {
		double maxItem = 0;
		for (double item : itemList) {
			if (item > maxItem) {
				maxItem = item;
			}
		}
		return maxItem;
	}
	
	public static int getMaxInt(ArrayList<Integer> itemList) {
		int maxItem = 0;
		for (int item : itemList) {
			if (item > maxItem) {
				maxItem = item;
			}
		}
		return maxItem;
	}

	

	public static double getMin(ArrayList<Double> itemList) {
		double minItem = 0;
		for (double item : itemList) {
			if (item > minItem) {
				minItem = item;
			}
		}
		return minItem;
	}
	
	public static int getMinInt(ArrayList<Integer> itemList) {
		int minItem = Integer.MAX_VALUE;
		for (int item : itemList) {
			if (item < minItem) {
				minItem = item;
			}
		}
		return minItem;
	}
	

	public static HashMap<String, Integer> wordcount(String content) {
		// performing simple word count
		String[] words = content.split("\\s+");
		HashMap<String, Integer> countmap = new HashMap<>();
		for (String word : words) {
			if (countmap.containsKey(word)) {
				int count = countmap.get(word) + 1;
				countmap.put(word, count);
			} else {
				countmap.put(word, 1);
			}
		}
		return countmap;
	}

	public static HashMap<String, Integer> wordcount(File file) {
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(file.getAbsolutePath());
		HashMap<String, Integer> countmap = new HashMap<>();
		for (String line : lines) {
			String[] words = line.split("\\s+");
			for (String word : words) {
				if (countmap.containsKey(word)) {
					int count = countmap.get(word) + 1;
					countmap.put(word, count);
				} else {
					countmap.put(word, 1);
				}
			}
		}
		return countmap;
	}

	public static HashMap<String, String> loadKeys(String keyFile) {
		// loading file name keys
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(keyFile);
		HashMap<String, String> keyMapLocal = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.split(":");
			String key = parts[0] + ".java";
			keyMapLocal.put(key, parts[2].trim()); // startled me
		}
		return keyMapLocal;
	}

}
