/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class contains the query expansion algorithm based on Rocchio's Algorithm.
 ****/

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.HashMap;
import java.util.TreeSet;

public class QueryExpansion {
	
	private ArrayList<String> queryHistory = new ArrayList<String>();
	private YahooTop10Results result = null;
	private String lastQuery;
	private InvertedIndex invIdx = new InvertedIndex();
	
	/**
	 * Rocchio's Algorithm
	 * New query = alpha * query + ( beta / no of relevant docs ) * Sum ( relevant docs vector )
	 */
	
	// Rocchio Constants
	private final static double alpha = 1;
	private final static double beta = .75;
	
	// Relevant web page documents
	private Vector<WebPage> relDocs = null;
	private int relDocsCount = 0;
	
	/**
	 * Constructor
	 * @param initalQueryString The user's search string
	 */
	public QueryExpansion (String initalQueryString) {
		queryHistory.add(initalQueryString);
	}
	
	/**
	 * Update with results of last query search
	 * @param r
	 */
	public void updateResult (YahooTop10Results r) {
		result = r;
	}
	

	/**
	 * create the inverted index from search results
	 */
	public void buildIndex () {
		invIdx.clear();
		ArrayList<ResultNode> resultList = result.getResultNodes();
		for (int docid=0; docid < resultList.size(); docid++) {
			ResultNode r = resultList.get(docid);
			String title   = r.getTitle();
			String summary = r.getSummary();
			String line = title + " " + summary; // all text that was shown to user
			invIdx.addDocument(docid, line);
		}
		//System.out.println("DEBUG: index is\n" + invIdx); // @@@ DEBUG
	}
	
	/**
	 *  Compute the tf-idf vector for each document 
	 *  @@@ term with best combined tf-idf score will be the augment -- to be refined
	 */
	public String getTFIDFAugment() {
		// sum up the relevant documents' terms tfidf
		// print the 5 terms with highest tfidf score
		
		HashMap<String,TermTFIDF> tfidfVector = new HashMap<String,TermTFIDF>();
		ArrayList<ResultNode> rn = result.getResultNodes();
		for (int docid=0; docid < rn.size(); docid++) {
			ResultNode node = rn.get(docid);
			if (! node.isRelevant()) continue;

			for (String term: invIdx.getTerms()) {
				double tfidf = invIdx.tfidf(term, docid);
				if (term.equals("bill") || term.equals("information")) {
					System.out.println("DEBUG (before): "+ term + " tfidf = "+ tfidf);
				}
				if (tfidfVector.containsKey(term)) { // sum up all relevant doc's tfidf
					double origTfidf = tfidfVector.get(term).getTfIdf();
					tfidf += origTfidf;
					tfidfVector.get(term).put(term, tfidf);
				} else {
					TermTFIDF t = new TermTFIDF(term,tfidf);
					tfidfVector.put(term, t);
				}
				if (term.equals("bill") || term.equals("information")) {
					System.out.println("DEBUG (after): " + term + " tfidf = " + tfidf);
				}
				
			}
		}

		// Sort descending order by tf-idf score
		ArrayList<TermTFIDF> tfidfList = new ArrayList<TermTFIDF>(tfidfVector.values());
		Collections.sort(tfidfList); 

		// @@@ DEBUG: print top 5 tf-idf score terms
		int count = 0;
		for (TermTFIDF t: tfidfList) {
			System.out.println("DEBUG: " + t.getTerm() + " tf-idf=" + t.getTfIdf());
			if (++count >=5) break;
		}

		return tfidfList.get(0).getTerm(); // term with highest tfidf score
	}
	

	/**
	 * Expand the query
	 * @return New query
	 */
	public String expandx () {
		lastQuery = queryHistory.get(queryHistory.size()-1);
		
		String augment = expandQuery();
		String newQuery = lastQuery + " " + augment;  // @@@ test

		return newQuery;
	}
	
	/**
	 * Expand the query using the highest TF-IDF scored term as augment
	 * @return newQueryString
	 */
	public String expand() {
		lastQuery = queryHistory.get(queryHistory.size()-1);
		
		buildIndex();
		String augmentx = getTFIDFAugment();
		System.out.println("TF-IDF Augment: " + augmentx);

		String newQuery = lastQuery + " " + augmentx;
		return newQuery;
	}
	
	/**
	 * Analyzes document terms and selects new query terms for expansion
	 * @return String of new query
	 */
	public String expandQuery() {
		
		// Store all relevant web pages
		try {
			relDocs = getRelevantDocs();
		} catch (IOException e) {
			System.out.println("There was a problem accessing the webpages");
			e.printStackTrace();
		}
		
		// Number of relevant docs
		relDocsCount = relDocs.size();
		
		// Collect terms from the documents (web pages)
		Vector<TermsVector> docTermsVector = getDocsTerms();
		
		// Set the scores of the document terms and merge
		TermsVector docTerms = calculateScores(docTermsVector);
		
		// Get the top 2 new terms
		String newTerms = docTerms.getBestTerms(2);
		
		// Alternative:
		// TODO: Set the score of the query terms
		// TODO: Combine query and doc terms, sort and choose top 2 new terms
		
		System.out.println("Augmenting by  " + newTerms);
		
		return newTerms;
		
	}
	
	/**
	 * Collects the web pages of all relevant result nodes
	 * @return Vector of relevant web pages
	 * @throws IOException
	 */
	private Vector<WebPage> getRelevantDocs() throws IOException {
		Vector<WebPage> docs = new Vector<WebPage>();
				
		System.out.println("Indexing results ....");

		// Iterate over each result
		for (ResultNode node : result.getResultNodes()) {
			if (node.isRelevant()) {							// If relevant
				WebPage wp = new WebPage(node.getWebPage());	// Download its web page
				docs.add(wp);									// Add to vector
			}
		}
		
		return docs;
	}
	
	/**
	 * Collects all the terms of each web page into a Vector
	 * @return Vector of separate term vectors per document
	 */
	private Vector<TermsVector> getDocsTerms() {
		Vector<TermsVector> docsTerms = new Vector<TermsVector>();
		
		System.out.println("Indexing results ....");

		// Iterate over each document
		for (WebPage wp: relDocs) {
			TermsVector tv = new TermsVector(wp.getFullText());	// Get the full text of web page (then parse it)
			docsTerms.add(tv);									// Add to vector
		}
		
		return docsTerms;
	}
	
	/**
	 * Calculates the scores of all the terms contained in the relevant documents
	 * @param docsTerms
	 * @return Vector of scored terms
	 */
	private TermsVector calculateScores(Vector<TermsVector> docsTerms) {
		
		Vector<TermNode> newDocsTerms = new Vector<TermNode>();
		
		// Iterate over all the terms
		for (int i = 0; i < docsTerms.size(); i++) {
			
			TermsVector tv = docsTerms.get(i); // get the vector for a particular document
			
			for (int j = 0; j < tv.size(); j++) {
				
				// Get term node of the given vector
				TermNode node = tv.getTerm(j);
				
				// Calculate weight and score
				int df = 10; // @@ test
				float weight = node.calculateWeight(df);
				node.setScore(weight * (float)beta);
				
				// Add to new docs
				newDocsTerms.add(node);
			}
		}
		
		// Merge the docs terms into one vector
		merge(newDocsTerms);
		
		// Returns new vector
		return new TermsVector(newDocsTerms);
	}
	
	/**
	 * Removes duplicates in the term vector, combines scores
	 * @param terms
	 */
	private void merge(Vector<TermNode> terms) {
		
		// Get a term node
		for (int i = 0; i < terms.size(); i++) {
			TermNode node = terms.get(i);
			
			// Iterate through all other term nodes
			for (int j = i+1; j < terms.size(); j++) {
				TermNode tmp = terms.get(j);
				
				// If equal terms, then combine their scores
				if (tmp.getTerm().equalsIgnoreCase(node.getTerm())) {
					float score = node.getScore() + tmp.getScore();
					node.setScore(score);
					
					// Remove the duplicate and continue
					terms.remove(j);
					j--;
				}
			}
		}
	}
	
	
}