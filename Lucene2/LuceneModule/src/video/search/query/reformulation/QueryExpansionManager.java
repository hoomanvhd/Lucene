





package video.search.query.reformulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import ca.polymtl.swat.videosegment.config.StaticData;
import core.pagerank.PageRankProvider;
import utility.ContentLoader;
import utility.ItemSorter;
import utility.MiscUtility;
import video.search.query.reformulation.pagerank.WordNetworkMaker;
import video.segment.search.LuceneVideoSearcher;
public class QueryExpansionManager {
	
	
	
	
	String searchQuery;
	static int PRF_SIZE = 50;

	public QueryExpansionManager(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	protected ArrayList<Integer> collectPRFDocuments() {
		LuceneVideoSearcher searcher = new LuceneVideoSearcher(StaticData.INDEX_FOLDER, this.searchQuery, PRF_SIZE);
		return searcher.executeQuery();
	}

	protected ArrayList<String> expandQuery(int expansionSize) {
		ArrayList<Integer> prfDocs = collectPRFDocuments();
		HashMap<String, Double> idfMap = new TFIDFManager(StaticData.INDEX_FOLDER).calculateIDFOnly();
		HashMap<String, Double> candidateScoreMap = new HashMap<>();
		
		
		
		for (int docID : prfDocs) {
			
			String docURL = StaticData.CORPUS_FOLDER + "/" + docID + ".txt";
			String content = ContentLoader.loadFileContent(docURL);
			HashMap<String, Integer> wordcountMap = MiscUtility.wordcount(content);
			for (String word : wordcountMap.keySet()) {
				
				int tf = wordcountMap.get(word);
				double idfScore = 0;
				if (idfMap.containsKey(word)) {
					idfScore = idfMap.get(word);
				} else if (idfMap.containsKey(word.toLowerCase())) {
					idfScore = idfMap.get(word.toLowerCase());
				}
				double tfidf = tf * idfScore;
				if (candidateScoreMap.containsKey(word)) {
					double oldscore = candidateScoreMap.get(word);
					double newScore = oldscore + tfidf;
					candidateScoreMap.put(word, newScore);
				} else {
					candidateScoreMap.put(word, tfidf);
				}
			}
		}
		List<Map.Entry<String, Double>> sortedCandidates = ItemSorter.sortHashMapDouble(candidateScoreMap);
		ArrayList<String> expansion = new ArrayList<>();
		for (int i = 0; i < sortedCandidates.size(); i++) {
			String keyword = sortedCandidates.get(i).getKey();
			expansion.add(keyword);
			if (expansion.size() == expansionSize)
				break;
		}

		return expansion;
	}

	protected ArrayList<String> expandQueryTF(int expansionSize) {
		ArrayList<Integer> prfDocs = collectPRFDocuments();
		HashMap<String, Double> candidateScoreMap = new HashMap<>();
		for (int docID : prfDocs) {
			String docURL = StaticData.CORPUS_FOLDER + "/" + docID + ".txt";
			String content = ContentLoader.loadFileContent(docURL);
			HashMap<String, Integer> wordcountMap = MiscUtility.wordcount(content);
			for (String word : wordcountMap.keySet()) {
				
				
				int tf = wordcountMap.get(word);

				// ignoring the IDF! Sometimes, it works better!
				double tfidf = tf * 1.0;

				if (candidateScoreMap.containsKey(word)) {
					double oldscore = candidateScoreMap.get(word);
					double newScore = oldscore + tfidf;
					candidateScoreMap.put(word, newScore);
				} else {
					candidateScoreMap.put(word, tfidf);
				}
			}
		}
		List<Map.Entry<String, Double>> sortedCandidates = ItemSorter.sortHashMapDouble(candidateScoreMap);
		ArrayList<String> expansion = new ArrayList<>();
		for (int i = 0; i < sortedCandidates.size(); i++) {
			String keyword = sortedCandidates.get(i).getKey();
			expansion.add(keyword);
			if (expansion.size() == expansionSize)
				break;
		}

		return expansion;
	}

	protected ArrayList<String> expandQueryPR(int expansionSize) {
		ArrayList<Integer> prfDocs = collectPRFDocuments();
		ArrayList<String> sentences = new ArrayList<String>();
		for (int docID : prfDocs) {
			String docURL = StaticData.CORPUS_FOLDER + "/" + docID + ".txt";
			String content = ContentLoader.loadFileContent(docURL);
			sentences.add(content);
		}
		
		// now construct the text graph
		WordNetworkMaker wnMaker = new WordNetworkMaker(sentences);
		DirectedGraph<String, DefaultEdge> textGraph = wnMaker.createWordNetwork();
		HashMap<String, Double> tokendb = wnMaker.getTokenDictionary(false);

		// calculate the Page Rank
		PageRankProvider prProvider = new PageRankProvider(textGraph, tokendb);
		HashMap<String, Double> candidateScoreMap = prProvider.calculatePageRank();

		// sorting the Page Rank
		List<Map.Entry<String, Double>> sortedCandidates = ItemSorter.sortHashMapDouble(candidateScoreMap);
		ArrayList<String> expansion = new ArrayList<>();
		for (int i = 0; i < sortedCandidates.size(); i++) {
			String keyword = sortedCandidates.get(i).getKey();
			expansion.add(keyword);
			if (expansion.size() == expansionSize)
				break;
		}

		return expansion;

		
	}


	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		System.out.println("BASELINE QUERY");
		String originalQuery = "Multiprocessing vs Threading Python";
		System.out.println("Original: " + originalQuery);
		int TOPK = 12;
		LuceneVideoSearcher searcher = new LuceneVideoSearcher(StaticData.INDEX_FOLDER, originalQuery, TOPK);
		ArrayList<Integer> results = searcher.executeQuery();
		searcher.evaluate(results);

		System.out.println("==================================");
		System.out.println("EXPANDED QUERY USING TERM FREQUENCY");

		// now lets try with the reformulated query
		int expansionSize = 10;
		QueryExpansionManager.PRF_SIZE = 30;
		ArrayList<String> expansionKeywords = new QueryExpansionManager(originalQuery).expandQueryTF(expansionSize);
		String expandedQuery = originalQuery + "\t" + MiscUtility.list2Str(expansionKeywords);
		System.out.println("Expanded query:" + expandedQuery);
		LuceneVideoSearcher searcher2 = new LuceneVideoSearcher(StaticData.INDEX_FOLDER, expandedQuery, TOPK);
		ArrayList<Integer> results2 = searcher2.executeQuery();
		searcher2.evaluate(results2);
		
		System.out.println("==================================");
		System.out.println("EXPANDED QUERY USING PAGE RANK");
		
		ArrayList<String> expansionKeywords2 = new QueryExpansionManager(originalQuery).expandQueryPR(expansionSize);
		String expandedQuery2 = originalQuery + "\t" + MiscUtility.list2Str(expansionKeywords2);
		System.out.println("Expanded query:" + expandedQuery2);
		LuceneVideoSearcher searcher3 = new LuceneVideoSearcher(StaticData.INDEX_FOLDER, expandedQuery2, TOPK);
		ArrayList<Integer> results3 = searcher3.executeQuery();
		searcher3.evaluate(results3);
		
		System.out.println("==================================");
		System.out.println("EXPANDED QUERY USING TF-IDF");
		
		ArrayList<String> expansionKeywords3 = new QueryExpansionManager(originalQuery).expandQuery(expansionSize);
		String expandedQuery3 = originalQuery + "\t" + MiscUtility.list2Str(expansionKeywords3);
		System.out.println("Expanded query:"+ expandedQuery3);
		LuceneVideoSearcher searcher4 = new LuceneVideoSearcher(StaticData.INDEX_FOLDER, expandedQuery, TOPK);
		ArrayList<Integer> results4 = searcher4.executeQuery();
		searcher4.evaluate(results4);
		
		
	}
}
