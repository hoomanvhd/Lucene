package video.segment.search;

import java.io.File;
import ca.polymtl.swat.videosegment.config.StaticData;
import text.normalizer.TextNormalizer;
import utility.ContentLoader;
import utility.ContentWriter;

public class CorpusMaker {

	String corpusFolder;
	String normCorpusFolder;

	public CorpusMaker(String docs, String normDocs) {
		this.corpusFolder = StaticData.HOME_DIR + "/Lucene/" + docs;
		this.normCorpusFolder = StaticData.HOME_DIR + "/Lucene/" + normDocs;
	}

	protected void normalizeCorpus() {
		File dir = new File(this.corpusFolder);
		File[] files = dir.listFiles();
		for (File f : files) {
			String content = ContentLoader.loadFileContent(f.getAbsolutePath());
			TextNormalizer normalizer = new TextNormalizer(content.toLowerCase());
			String normalized = normalizer.normalizeText();
			String normOutputFile = this.normCorpusFolder + "/" + f.getName();
			ContentWriter.writeContent(normOutputFile, normalized);
			System.out.println("Done: " + f.getName());
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String docs = "docs";
		String normDocs = "norm-docs";
		new CorpusMaker(docs, normDocs).normalizeCorpus();
	}
}
