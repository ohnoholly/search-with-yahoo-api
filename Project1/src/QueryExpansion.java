/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class contains the query expansion algorithm.
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
	
	// Rocchio Constants
	private final static double alpha = 1;
	private final static double beta = .75;
	private final static double gamma = .15;
	
	// Relevant web page documents
	//private Vector<WebPage> relDocs = null;
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
	 * Create the inverted index from search results
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
	
}
