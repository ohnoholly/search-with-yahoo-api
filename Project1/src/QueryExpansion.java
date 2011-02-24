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
		System.out.println("Indexing results ....");
		invIdx.clear();
		ArrayList<ResultNode> resultList = result.getResultNodes();
		for (ResultNode rn: resultList) {
			invIdx.addDocument(rn);
		}
	}
	
	/**
	 *  Compute the tf-idf vector for each document
	 *  using Sahami-Heilman's similarity Algorithm 
	 *  
	 */
	public String getTFIDFAugment2() {
		// Build the TFIDF term vectors for the relevant documents
		ArrayList<TermVector> docVectors = new ArrayList<TermVector> ();
		for (ResultNode rn: result.getRelevantResultNodes()) {
			TermVector tv = new TermVector(rn, invIdx); // term vector for a document
			docVectors.add(tv);
		}

		// Original algorithm truncates each vector to 50 highest scoring terms
		// Our vectors are short, no need to truncate
		
		// Find the Centroid of the L2 normalized term vectors
		// Centroid is a vector of all distinct terms in the relevant documents
		// The term weight is the sum of the normalized weight of the term in all relevant docs
		TermVector centroid = new TermVector();
		for (TermVector tv: docVectors) {
			for (TermNode tn: tv.getTerms()) {
				String term = tn.getTerm();
				double weight = tn.getNormalizedWeight();
				centroid.addTerm(term, weight);
			}
		}
		
		// Normalize the Centroid
		centroid.l2normalize();
		
		// Sort descending order by normalized weight
		ArrayList<TermNode> termList = centroid.getTerms();
		Collections.sort(termList);

		// @@@ DEBUG: print top 5 tf-idf score terms
		int count = 0;
		for (TermNode t: termList) {
			System.out.println("DEBUG: " + t.getTerm() + " normalized weight=" + t.getNormalizedWeight());
			if (++count >=5) break;
		}

		return termList.get(0).getTerm(); // term with highest tfidf score
	}

	/**
	 *  Compute the tf-idf vector for each document 
	 *  @@@ term with best combined tf-idf score will be the augment -- to be refined
	 */
	public String getTFIDFAugment1() {
		// sum up the relevant documents' terms tfidf
		// print the 5 terms with highest tfidf score
		// Build the TFIDF term vectors for the relevant documents
		ArrayList<TermVector> docVectors = new ArrayList<TermVector> ();
		for (ResultNode rn: result.getRelevantResultNodes()) {
			TermVector tv = new TermVector(rn, invIdx); // term vector for a document
			docVectors.add(tv);
		}

		// Sum up the tfidf of all relevant docs
		TermVector terms = new TermVector();
		for (TermVector tv: docVectors) {
			for (TermNode tn: tv.getTerms()) {
				String term = tn.getTerm();
				double weight = tn.getWeight(); // tf-idf
				terms.addTerm(term,weight);
			}
		}
		
		// Sort descending order by L2 normalized weight
		ArrayList<TermNode> termList = terms.getTerms();
		Collections.sort(termList); 

		// @@@ DEBUG: print top 5 tf-idf score terms
		int count = 0;
		for (TermNode t: termList) {
			System.out.println("DEBUG: " + t.getTerm() + " term weight=" + t.getWeight());
			if (++count >=5) break;
		}

		return termList.get(0).getTerm(); // term with highest tfidf score
	}
	

	/**
	 * Expand the query
	 * @return New query
	 */
//	public String expandx () {
//		lastQuery = queryHistory.get(queryHistory.size()-1);
//		
//		String augment = expandQuery();
//		String newQuery = lastQuery + " " + augment;  // @@@ test
//
//		return newQuery;
//	}
	
	/**
	 * Expand the query using the highest TF-IDF scored term as augment
	 * @return newQueryString
	 */
	public String expand() {
		lastQuery = queryHistory.get(queryHistory.size()-1);
		
		buildIndex();
		String augment1 = getTFIDFAugment1(); // Not Normalized
		String augment2 = getTFIDFAugment2(); // Normalized
		System.out.println("DEBUG: Augmented By: "+augment1+"(not normalized) vs. "+augment2+"(normalized)");
		
		String augment = augment2; // testing normalized
		System.out.println("Augmented By: " + augment);

		String newQuery = lastQuery + " " + augment;
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
				double weight = node.calculateWeight(df);
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
					double score = node.getScore() + tmp.getScore();
					node.setScore(score);
					
					// Remove the duplicate and continue
					terms.remove(j);
					j--;
				}
			}
		}
	}
	
	
}
