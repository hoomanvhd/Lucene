


package video.segment.search;

import java.io.File;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import ca.polymtl.swat.videosegment.config.StaticData;
import text.normalizer.TextNormalizer;

public class LuceneVideoSearcher {

	String indexFolder;
	String searchQuery;
	String field = "contents";
	int TOPK = 10;
	ArrayList<Integer> groundTruths;

	public LuceneVideoSearcher(String indexFolder, String searchQuery, int TOPK) {
		this.indexFolder = indexFolder;
		this.searchQuery = normalizeQuery(searchQuery.toLowerCase());
		System.out.println("Preprocessed:" + this.searchQuery);
		this.TOPK = TOPK;
		this.groundTruths = loadGroundTruth();
	}

	protected ArrayList<Integer> loadGroundTruth() {
		// 66 to 150 for the query
		// these files will be loaded from the file system.
		ArrayList<Integer> temp = new ArrayList<>();
		for (int fileID = 0; fileID <= 11; fileID++) {
			temp.add(fileID);
		}
		return temp;
	}

	protected String normalizeQuery(String searchQuery) {
		return new TextNormalizer(searchQuery).normalizeText();
	}

	public ArrayList<Integer> executeQuery() {
		// execute the query and collect results
		IndexReader reader = null;
		IndexSearcher searcher = null;
		Analyzer analyzer = null;
		ArrayList<Integer> resultedFiles = new ArrayList<>();

		try {
			if (reader == null)
				reader = DirectoryReader.open(FSDirectory.open(new File(indexFolder).toPath()));
			if (searcher == null)
				searcher = new IndexSearcher(reader);
			if (analyzer == null)
				analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser(field, analyzer);

			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, TOPK);
				ScoreDoc[] hits = results.scoreDocs;
				int len = hits.length < TOPK ? hits.length : TOPK;
				for (int i = 0; i < len; i++) {
					ScoreDoc item = hits[i];
					Document doc = searcher.doc(item.doc);
					String fileURL = doc.get("path");
					fileURL = fileURL.replace('\\', '/');
					int fileID = Integer.parseInt(new File(fileURL).getName().split("\\.")[0]);
					resultedFiles.add(fileID);
				}
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return resultedFiles;
	}

	public void evaluate(ArrayList<Integer> resultedFiles) {
		ArrayList<Integer> temp = new ArrayList<>(resultedFiles);

		// refining the results based on longest consecutive sequence
		//resultedFiles=refineResults(resultedFiles);

		temp.retainAll(this.groundTruths);
		System.out.println("Precision:" + (double) temp.size() / resultedFiles.size());
		System.out.println("Recall:" + (double) temp.size() / this.groundTruths.size());
	}

	protected ArrayList<Integer> refineResults(ArrayList<Integer> resultedFiles) {
		// return new LongestConsecutiveSeq(resultedFiles).determineLongestCosecSeq();
		return new LCSVideoSegment(resultedFiles).determineLCSforVS();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indexFolder = StaticData.HOME_DIR + "/lucene/index";
		String searchQuery = "Multiprocessing vs Threading Python";
		int TOPK = 12;
		LuceneVideoSearcher searcher = new LuceneVideoSearcher(indexFolder, searchQuery, TOPK);
		ArrayList<Integer> results = searcher.executeQuery();
		System.out.println(searcher.executeQuery());
		searcher.evaluate(results);
	}
}
