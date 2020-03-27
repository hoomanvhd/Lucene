


/****
 *
* 
* @author MasudRahman
 * Text normalizer class, performs standard NLP
 *
 */



package text.normalizer;

import java.util.ArrayList;
import java.util.Arrays;
import text.stopwords.StopWordManager;
import utility.MiscUtility;


public class TextNormalizer {

	
	
	String content;
	boolean stem = false;

	public TextNormalizer(String content) {
		this.content = content;
	}

	public String normalizeSimple() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		
		
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		return MiscUtility.list2Str(wordList);
	}

	// some code is changed here for testing

	public String normalizeSimpleCode() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// extracting code only items
		wordList = extractCodeItem(wordList);
		String modifiedContent = MiscUtility.list2Str(wordList);
		StopWordManager stopManager = new StopWordManager();
		this.content = stopManager.getRefinedSentence(modifiedContent);
		return this.content;
	}

	public String normalizeSimpleNonCode() {
		String[] words = this.content.split("\\p{Punct}+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		ArrayList<String> codeOnly = extractCodeItem(wordList);
		// only non-code elements
		wordList.removeAll(codeOnly);
		return MiscUtility.list2Str(wordList);
	}

	protected ArrayList<String> discardSmallTokens(ArrayList<String> items) {
		// discarding small tokens
		ArrayList<String> temp = new ArrayList<>();
		for (String item : items) {
			if (item.length() > 1) { // discarding single char items
				temp.add(item);
			}
		}
		return temp;
	}

	public String normalizeText() {
		// normalize the content
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// discard the small tokens
		//wordList = discardSmallTokens(wordList);
		// String modifiedContent = MiscUtility.list2Str(wordList);
		StopWordManager stopManager = new StopWordManager();
		ArrayList<String> refined = stopManager.getRefinedList(wordList);
		refined = discardSmallTokens(refined);
		// now perform the stemming
		this.content = MiscUtility.list2Str(refined);
		return this.content;
	}

	protected ArrayList<String> extractCodeItem(ArrayList<String> words) {
		// extracting camel-case letters
		ArrayList<String> codeTokens = new ArrayList<>();
		for (String token : words) {
			if (decomposeCamelCase(token).size() > 1) {
				codeTokens.add(token);
			}
		}
		return codeTokens;
	}
	protected ArrayList<String> decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		refined.addAll(Arrays.asList(ftokens));
		return refined;
	}
	public static void main(String[] args) {
		String query = "Multiprocessing vs Threading Python";
		System.out.println(new TextNormalizer(query).normalizeText());
	}

}
