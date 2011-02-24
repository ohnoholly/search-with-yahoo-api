import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Inverted Index 
 * terms => list of documents that contain the term (the values)
 */
public class InvertedIndex {
	private TreeSet<Integer> documents = new TreeSet<Integer>(); // all document ids
	private HashMap<String,ArrayList<DocumentIndexNode>> index
		= new HashMap<String,ArrayList<DocumentIndexNode>>();

	private HashMap<String,Double> idfCache = new HashMap<String,Double>();
	private boolean idfCacheClean = true;
	
	public void clear() {
		index.clear();
		idfCache.clear();
		documents.clear();
	}
	

	public TreeSet<Integer> getDocuments () {
		return documents;
	}
	
	// return the list of documents containing the term
	public ArrayList<DocumentIndexNode> getDocumentList (String term) {
		if (index.containsKey(term)) {
			return index.get(term);
		} else {
			return null;
		}
	}

	// return the document node of the specified document containing the term
	public DocumentIndexNode getDocumentIndexNode(String term, int doc) {
		ArrayList<DocumentIndexNode> doclist = getDocumentList(term);
		return getDocumentIndexNode(doclist, doc);
	}

	// return the document node of the specified document from a list of documents
	public DocumentIndexNode getDocumentIndexNode(ArrayList<DocumentIndexNode> doclist, int doc) {
		if (doclist != null) {
			for (DocumentIndexNode d: doclist) {
				if (d.getDocId() == doc) {
					return d; 
				}
			}
		}
		return null;
	}

	public Set<String> getTerms() {
		return index.keySet();
	}
	// add a new position of a term to the document 
	public void addTerm(String term, int doc, int pos) {
		ArrayList<DocumentIndexNode> doclist = getDocumentList(term);
		DocumentIndexNode d;
		if (doclist == null) { // new term
			doclist = new ArrayList<DocumentIndexNode>();
			d = new DocumentIndexNode(doc);
			doclist.add(d);
			index.put(term, doclist);
		} else {
			d = getDocumentIndexNode(doclist, doc);
			if (d == null) { // new document for a known term
				d = new DocumentIndexNode(doc);
				doclist.add(d);
			}
		}
		d.addPosition(pos);
		documents.add(doc); // add to the document set, if it is not already there
		idfCacheClean = false;  // mark cache dirty, and idf will be re-calculated
	}

	public int df (String term) {
		ArrayList<DocumentIndexNode> doclist = getDocumentList(term);
		if (doclist == null) { // new term
			return 0;
		} else {
			return doclist.size();
		}
	}

	// calculate the inverted document frequency of a term
	public double idf (String term) {

		// Use cached value when possible
		if (idfCacheClean) {
			// If IDF cache is clean, use the IDF value in it.
			if (idfCache.containsKey(term)) {
				return idfCache.get(term);
			}
		} else { 
			// dirty cache - remove all the values in cache
			idfCache.clear(); 
			idfCacheClean = true;
		}

		// Compute IDF
		double N = documents.size(); // cast to double to force floating-point division later
		int df = df(term); 
		// add 1 to avoid division by zero
		// this should not happen because idf is calculated for terms that are present 
		if (df == 0) df = 1;
		double idf = Math.log10(N/df);
		if (term.equals("bill") || term.equals("information")) //@@@ DEBUG		
			System.out.println("N="+N+", df="+df+", idf("+term+") = "+idf);
		idfCache.put(term, idf); // update cache
		return idf;
	}

	public double tfidf (String term, int doc) {
		DocumentIndexNode d = getDocumentIndexNode(term,doc);
		return tfidf(term, d);
	}

	public double tfidf (String term, DocumentIndexNode d) {
		int tf = (d == null) ? 0 : d.tf();
		double idf = idf(term);
		double tfidf = tf * idf;
		if (term.equals("bill") || term.equals("information")) //@@@ DEBUG
			System.out.println("tfidf("+term+","+d+") = "+tfidf);
		return tfidf;
	}
	
	public void addDocument (ResultNode rn) {
		int docid = rn.getDocId();
		ArrayList<String> terms = rn.getTerms();
		for (int pos=0; pos<terms.size(); pos++) {
			String term = terms.get(pos);
			addTerm(term,docid,pos);
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String term: index.keySet()) {
			sb.append(term+":");
			ArrayList<DocumentIndexNode> doclist = getDocumentList(term);
			//int count = 0;
			for (DocumentIndexNode d: doclist) {
				sb.append(" " + d);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	/**
	 * The document nodes in the inverted index
	 * Each node represents a term's positions in one document
	 */
	private class DocumentIndexNode {
		private int docid;
		// locations of a term within the document
		private ArrayList<Integer> positions = new ArrayList<Integer>();
		public DocumentIndexNode (int d) {
			docid = d;
		}
		public int getDocId () {
			return docid;
		}
		public int tf() { // Term Frequency
			return positions.size();
		}
		public void addPosition(int pos) {
			positions.add(pos);
		}
		public String toString () {
			StringBuilder sb = new StringBuilder();
			sb.append("[" + docid + ":");
			for (int i: positions) {
				sb.append(" " + i);
			}
			sb.append("]");
			return sb.toString();
		}
	}
}