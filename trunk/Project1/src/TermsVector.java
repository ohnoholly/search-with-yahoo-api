/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class stores vector of term nodes for query expansion.
 ****/

import java.util.ArrayList;
import java.util.Vector;

public class TermsVector {
	
	// Vector of terms (nodes) 
	private Vector<TermNode> _terms = null;

	/**
	 * Constructor
	 * @param text
	 */
	public TermsVector(String text) {
		_terms = new Vector<TermNode>();
		parseTerms(text);
	}
	
	/**
	 * Constructor with an existing vector
	 * @param vector
	 */
	public TermsVector(Vector<TermNode> vector) {
		_terms = vector;
	}
	
	/**
	 * Parses a blob of text into an array of unique terms
	 * @param text
	 */
	private void parseTerms(String text) {
		
		// Create string array from full text, separated by spaces
		String[] allTerms = text.split(" ");
		
		// For each term, create a term node or count if already exists
		for (int i = 0; i < allTerms.length; i++) {
			String term = allTerms[i];
			
			TermNode tn = null;
			if ((tn = getTerm(term)) != null)
				countTerm(tn);
			else 
				_terms.add(new TermNode(term));
		}
	}
	
	/**
	 * Returns the node with the given term name
	 * @param term A string
	 * @return
	 */
	public TermNode getTerm(String term) {
		TermNode existingTerm = null;
		
		// Iterate over term nodes and see if there is a match
		for (TermNode node : _terms) {
			if (node.getTerm().equalsIgnoreCase(term)) {
				existingTerm = node;
				break;
			}
		}	
		return existingTerm;
	}
	
	/**
	 * Returns the node with the given index
	 * @param i index
	 * @return 
	 */
	public TermNode getTerm(int i) {
		return _terms.get(i);
	}
	
	/**
	 * Counts an existing term (incrementing by 1)
	 * @param node
	 */
	private void countTerm(TermNode node) {
		node.incrementFreq();
	}
	
	/**
	 * Returns size of the vector, given by internal array list
	 * @return
	 */
	public int size() {
		return _terms.size();
	}
	
	/**
	 * Returns the best terms based on highest score
	 * @param num The number of terms to return
	 * @return
	 */
	public String getBestTerms(int num) {
		
		String str = "";
		TermNode[] termArray = null;
		
		// Create a binary heap
		BinaryHeap<TermNode> heap = new BinaryHeap<TermNode>();
		for (TermNode n : _terms) {
			heap.insert(n);
		}
		
		// Get top terms
		for (int i = 0; i < num; i++) {
			termArray[i] = heap.deleteMax();
		}
		
		// Create string
		for (int j = 0; j < termArray.length; j++) {
			str += termArray[j].getTerm() + " ";
		}
		
		return str;
		
	}
}
