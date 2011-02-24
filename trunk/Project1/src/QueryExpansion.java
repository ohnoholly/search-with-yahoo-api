/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class contains the query expansion algorithm.
 ****/

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;

public class QueryExpansion {
	
	private ArrayList<String> queryHistory = new ArrayList<String>();
	private YahooTop10Results result = null;
	private String lastQuery;
	private InvertedIndex invIdx = new InvertedIndex();
	
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
	public String getNormalizedAugment() {
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

		// quick way to index all the previously used search terms
		// check the new found terms against previous query to make sure there are no duplicates
		HashSet<String> lastQueryWords = new HashSet<String>(Arrays.asList(lastQuery.split(" ")));

		// augment with up to 2 top tf-idf score terms
		double weightDiffThreshold = 0.2; // weight is less than 20% difference
		String augment = null;
		double augmentWeight = 0;
		for (int i=0; i < termList.size(); i++) {
			TermNode t = termList.get(i);
			String term = t.getTerm();
			double weight = t.getWeight(); 
			
			// do not re-query terms that are in the previous query
			if (lastQueryWords.contains(term)) {
				System.out.println("term "+ term +" ("+weight+") is in previous query. Skipping.");
				continue;
			}
			//System.out.println("DEBUG: " + term + " weight=" + t.getWeight());
			
			// if the top 2 terms are close together in the score
			// use both terms in the next search
			if ((augment != null) && (weight > 0)) {
				// tie break results that are close
				double weightDiff = (augmentWeight - weight) / augmentWeight;
				if (weightDiff < weightDiffThreshold) {
					augment += " " + term;
				}
				break;
			} else {
				augment = term;
				augmentWeight = weight;
			}			
		}
		return augment;

	}

	/**
	 *  Compute the tf-idf vector for each document 
	 *  @@@ term with best combined tf-idf score will be the augment -- to be refined
	 */
	public String getAugment() {
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
	 * Expand the query using the highest TF-IDF scored term as augment
	 * @return newQueryString
	 */
	public String expand() {
		lastQuery = queryHistory.get(queryHistory.size()-1);
		
		buildIndex();
		//String augment1 = getTFIDFAugment1(); // Not Normalized
		String augment = getNormalizedAugment(); // Normalized
		System.out.println("Augmented By: " + augment);

		String newQuery = lastQuery + " " + augment;
		return newQuery;
	}
	
}
