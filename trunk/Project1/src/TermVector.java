import java.util.HashMap;
import java.util.ArrayList;

public class TermVector {
	private HashMap<String,TermNode> tfidfVector = new HashMap<String,TermNode>();
	private double l2norm = 0; // L2-norm

	/**
	 * Constructor for centroid
	 */
	public TermVector () {
		
	}
	
	/**
	 * Constructor for document terms 	
	 * @param rn Result Node
	 * @param invIdx Inverted Index of all results
	 */
	public TermVector (ResultNode rn, InvertedIndex invIdx) {
		buildDocumentVector(rn,invIdx);
		l2normalize();
	}
	
	/**
	 * Compute the TFIDF term vector for a document in Result Node
	 * @param rn Result Node
	 * @param invIdx Inverted Index of all results
	 */
	public void buildDocumentVector (ResultNode rn, InvertedIndex invIdx) {
		int docid = rn.getDocId();
		for (String term: rn.getTerms()) {
			if (tfidfVector.containsKey(term)) 
				// seen this term in the doc, no need to re-calculate tf-idf
				continue;
			
			double tfidf = invIdx.tfidf(term, docid);
			TermNode tn = new TermNode(term,tfidf);
			tfidfVector.put(term, tn);
		}
	}

	/**
	 * Calculate the L2-norm (or Euclidean distance) of the tf-idf vector
	 * @return L2 norm 
	 */
	public double calculateL2Norm () {
		double sqrsum = 0;
		for (String term: tfidfVector.keySet()) {
			double termTFIDF = tfidfVector.get(term).getWeight();
			sqrsum += Math.pow(termTFIDF, 2);
		}
		return Math.sqrt(sqrsum);
	}
	
	/**
	 * L2 normalize the vector, and store each weight in TermNode
	 */
	public void l2normalize () {
		if (l2norm == 0) {
			l2norm = calculateL2Norm();
		}
		for (String term: tfidfVector.keySet()) {
			TermNode tn = tfidfVector.get(term);
			double weight = tn.getWeight();
			double normalized = weight / l2norm;
			tn.setNormalizedWeight(normalized);
			//System.out.println("DEBUG: normalized("+term+")="+weight+"/"+l2norm+"="+normalized);
		}
	}

	// The following methods are used only in centroid calculation
	
	public ArrayList<TermNode> getTerms () {
		return new ArrayList<TermNode>(tfidfVector.values());
	}

//	public TermNode getTerm(String term) {
//		return tfidfVector.get(term);
//	}
//	
//	public boolean contains (String term) {
//		return tfidfVector.containsKey(term);
//	}
	
	public void addTerm (String term, double weight) {
		if (tfidfVector.containsKey(term)) {
			TermNode tn = tfidfVector.get(term);
			double w = tn.getWeight() + weight;
			tn.setWeight(w);
			tn.setNormalizedWeight(w);
		} else {
			TermNode tn = new TermNode(term);
			tn.setWeight(weight);
			tn.setNormalizedWeight(weight);
			tfidfVector.put(term, tn);
		}
	}
}
