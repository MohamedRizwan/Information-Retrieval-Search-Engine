package searchEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class SearchEngine {
	
	private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
	private IndexWriter writer;
	private int numberDocuments = 0;
	
	private PrintWriter printWriter;
	private Map<String, Long> termFrequencies = new HashMap<String, Long>();
	private Map<String, Long> sortedTermFrequencies = new LinkedHashMap<String, Long>();
	
	private ArrayList<File> queue = new ArrayList<File>();
	
	SearchEngine(String indexDir) throws IOException {
		FSDirectory dir = FSDirectory.open(new File(indexDir));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, sAnalyzer);
		writer = new IndexWriter(dir, config);
	}
	
	private void generateIndex(String fileName) throws IOException{
		File file = new File(fileName);
		
		addFiles(file);
		
		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			FileReader fr = null;
			try {
				Document doc = new Document();
				fr = new FileReader(f);
				//doc.add(new TextField("contents", fr));
				FieldType type = new FieldType();
			    type.setIndexed(true);
			    type.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			    type.setStored(true);
			    type.setStoreTermVectors(true);
			    type.setTokenized(true);
			    type.setStoreTermVectorOffsets(true);
			    Field field = new Field("content", fr, type);
			    doc.add(field);
			    
			    doc.add(new StringField("ncontent","This is fragment. Highlters", Field.Store.YES));
				//doc.add(new TextField("ncontents", fr));
				
				doc.add(new StringField("path", f.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", f.getName(), Field.Store.YES));

				writer.addDocument(doc);
				System.out.println("Added: " + f);
			} catch (Exception e) {
				System.out.println("Could not add: " + f);
			} finally {
				fr.close();
			}
		}

		int newNumDocs = writer.numDocs();
		numberDocuments = newNumDocs;
		System.out.println("");
		System.out.println("************************");
		System.out.println(newNumDocs + " documents added.");
		System.out.println("************************");

		queue.clear();
		
	}
	
	private void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			// Only index text files
			if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml")
					|| filename.endsWith(".txt")) {
				queue.add(file);
			} else {
				System.out.println("Skipped " + filename);
			}
		}
	}
	
	public static void main(String[] args){
		
		String filesLocation = args[0];
		String indexLocation = args[1];
		try {
		SearchEngine indexer = new SearchEngine(indexLocation);
		
		indexer.generateIndex(filesLocation);
		indexer.closeIndex();
		
		//indexer.getTermFrequencies(indexLocation);
		//indexer.sortTermFrequencies();
		//indexer.writeSortedTermFrequencies();
		//indexer.searchQueries(indexLocation);
		
		indexer.highLighter(indexLocation);
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Map<String, Long> getTermFrequencies(String indexLocation) throws IOException{
		printWriter = new PrintWriter("Term Frequencies.txt", "UTF-8");
		
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
		
		Fields fields = MultiFields.getFields(reader);
        for (String field : fields) {
            Terms terms = fields.terms(field);
            TermsEnum termsEnum = terms.iterator(null);
            int count = 0;
            
            BytesRef term = null;
            
            while ((term = termsEnum.next()) != null) {
            	String termText = term.utf8ToString();
				Term termInstance = new Term("contents", term);                              
				long termFreq = reader.totalTermFreq(termInstance);
				long docCount = reader.docFreq(termInstance);

				System.out.println("term: "+termText+", termFreq = "+termFreq+", docCount = "+docCount);
				if(termFreq != 0)
					printWriter.println("term: "+termText+", termFreq = "+termFreq+", docCount = "+docCount);
				termFrequencies.put(termText,termFreq);
                count++;
            }
            System.out.println(count);
        }
		
		reader.close(); 
		printWriter.close();
		return termFrequencies;
		
	}
	
	private void sortTermFrequencies(){
		List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(termFrequencies.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {

			public int compare(Map.Entry<String, Long> p1, Map.Entry<String, Long> p2) {
				return (p1.getValue().compareTo(p2.getValue()));
			}
		});
		
		for (Map.Entry<String, Long> entry : list) {
			if(entry.getValue() != 0)
				sortedTermFrequencies.put(entry.getKey(), entry.getValue());
		}
	}
	
	private void writeSortedTermFrequencies() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("Sorted Term Frequencies.txt", "UTF-8");
		Iterator it = sortedTermFrequencies.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        writer.println(pair.getKey() + "," +pair.getValue());
	    }
	    
	    writer.close();
	}
	
	public void closeIndex() throws IOException {
		writer.close();
	}
	
	private void searchQueries(String indexLocation) throws IOException, ParseException{
		
		PrintWriter writer = new PrintWriter("Queries Result.txt", "UTF-8");
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));		

		String[] queries = {"portable operating systems",
				"code optimization for space efficiency",
				"parallel algorithms",
				"parallel processor in information retrieval"};
		
		
		   
		
		//for(int j=1; j<2; j++){
		for(int j=0; j < queries.length; j++){
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
			Query q = new QueryParser(Version.LUCENE_47, "contents", sAnalyzer).parse(queries[j]);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			
			
			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
			Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(q));
			
			System.out.println("Found " + hits.length + " hits.");
			writer.println("Query: " + queries[j]);
			writer.println("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				
				System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
				writer.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
			}
		}
		writer.close();
	}
	
	public void highLighter(String indexLocation) throws IOException, ParseException, InvalidTokenOffsetsException {
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Query q = new QueryParser(Version.LUCENE_47, "contents", sAnalyzer).parse("portable operating systems");
	    
	    TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
	    searcher.search(q, collector);
		ScoreDoc[] dHits = collector.topDocs().scoreDocs;
	    
	    TopDocs hits = searcher.search(q, reader.maxDoc());
	    System.out.println(hits.totalHits);
	    SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
	    Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(q));
	    for (int i = 0; i < reader.maxDoc(); i++) {
	        int id = hits.scoreDocs[i].doc;
	        Document doc = searcher.doc(id);
	        /*String text = doc.get("ncontent");
	        TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, "ncontent", sAnalyzer);
	        TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 4);
	        for (int j = 0; j < frag.length; j++) {
	            if ((frag[j] != null) && (frag[j].getScore() > 0)) {
	                System.out.println((frag[j].toString()));
	            }
	        }*/
	        //Term vector
	        String text = doc.get("content");
	        TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), hits.scoreDocs[i].doc, "content", sAnalyzer);
	        TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 10);
	        for (int j = 0; j < frag.length; j++) {
	            if ((frag[j] != null) && (frag[j].getScore() > 0)) {
	                System.out.println((frag[j].toString()));
	            }
	        }

	        System.out.println("-------------");
	    }
	}
	
	private String getHighlightedField(Query query, String fieldName, String fieldValue) throws IOException, InvalidTokenOffsetsException {
	    Formatter formatter = new SimpleHTMLFormatter();
	    QueryScorer queryScorer = new QueryScorer(query);
	    Highlighter highlighter = new Highlighter(formatter, queryScorer);
	    highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, Integer.MAX_VALUE));
	    highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
	    return highlighter.getBestFragment(this.sAnalyzer, fieldName, fieldValue);
	}
	
}
