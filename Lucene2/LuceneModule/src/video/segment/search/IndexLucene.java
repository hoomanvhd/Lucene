package video.segment.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ca.polymtl.swat.videosegment.config.StaticData;

public class IndexLucene {

	String repoName;
	String index;
	String docs;
	public int totalIndexed = 0;

	public IndexLucene(String indexFolder, String docsFolder) {
		this.index = indexFolder;
		this.docs = docsFolder;
	}

	protected void makeIndexFolder(String repoName) {
		new File(this.index + "/" + repoName).mkdir();
		this.index = this.index + "/" + repoName;
	}

	public void indexCorpusFiles() {
		// index the files
		try {
			Directory dir = FSDirectory.open(new File(index).toPath());
			Analyzer analyzer = new StandardAnalyzer();
			// Analyzer analyzer=new EnglishAnalyzer(Version.LUCENE_44);
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(dir, config);
			indexDocs(writer, new File(this.docs));
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void clearIndexFiles() {
		// clearing index files
		File[] files = new File(this.index).listFiles();
		for (File f : files) {
			f.delete();
		}
		System.out.println("Index cleared successfully.");
	}

	protected void indexDocs(IndexWriter writer, File file) {
		// writing to the index file
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					return;
				}
				try {
					// make a new, empty document
					Document doc = new Document();
					Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
					doc.add(pathField);
					Field contentField = new TextField("contents",
							new BufferedReader(new InputStreamReader(fis, "UTF-8")));
					doc.add(contentField);

					// doc.add(new TextField("contents", new BufferedReader(
					// new InputStreamReader(fis, "UTF-8"))));
					// System.out.println("adding " + file);

					writer.addDocument(doc);
					totalIndexed++;

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		String normCorpusFolder = StaticData.HOME_DIR+"/Lucene/norm-docs";
		String indexFolder=StaticData.HOME_DIR+"/Lucene/index";
		IndexLucene indexer = new IndexLucene(indexFolder, normCorpusFolder);
		indexer.indexCorpusFiles();
		System.out.println("Files indexed:" + indexer.totalIndexed);
	}
}
