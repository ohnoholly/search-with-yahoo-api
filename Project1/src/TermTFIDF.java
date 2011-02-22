public class TermTFIDF implements Comparable<TermTFIDF> {
	private String term;
	private double tfidf;
	
	public TermTFIDF (String term, double tfidf) {
		this.term = term;
		this.tfidf = tfidf;
	}
	public String getTerm() {
		return term;
	}
	public double getTfIdf() {
		return tfidf;
	}
	public void put(String term, double tfidf) {
		this.term = term;
		this.tfidf = tfidf;
	}
	
	/**
	 * Sort by tfidf value in descending order
	 */
	public int compareTo(TermTFIDF arg0) {
		if (tfidf < arg0.tfidf) {
			return 1;
		} else if (tfidf == arg0.tfidf) {
			return 0;
		} else {
			return -1;
		}
	}
}
