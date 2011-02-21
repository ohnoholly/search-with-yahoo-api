/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class stores term nodes, which are used for query expansion.
 ****/

public class TermNode implements Comparable<TermNode> {

	private String termName;		// The term name
	private int termFrequency;		// Frequency of term in a document or set of documents
	private float score;			// Score, calculated by weight and factor
	
	private float weight;			// Weight tf*idf
	
	/**
	 * Constructor
	 * @param name the term
	 */
	public TermNode(String name) {
		termName = name;
		termFrequency = 1;
	}
	
	/**
	 * Increments the frequency of the term
	 */
	public void incrementFreq() {
		termFrequency++;
	}
	
	/**
	 * Returns the term name
	 * @return
	 */
	public String getTerm() {
		return termName;
	}
	
	/**
	 * Sets the score of the term
	 * @param score
	 */
	public void setScore(float score) {
		this.score = score;
	}
	
	/**
	 * Returns the score of the term
	 * @return
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Calculates weight
	 * @param df the number of documents
	 */
	public float calculateWeight(float df) {
		
		weight = termFrequency; // @@@ test
		// weight = tf * idf
		
		return weight;
	}

	/**
	 * Compares two nodes using score
	 */
	public int compareTo(TermNode tn) {
		if (this.score > tn.score)
			return 1;
		else if (this.score == tn.score)
			return 0;
		else
			return -1;
	}

}
