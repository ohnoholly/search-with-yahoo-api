import java.util.ArrayList;

public class QueryExpansion {
	private ArrayList<String> queryHistory = new ArrayList<String>();
	private YahooTop10Results result = null;
	
	public QueryExpansion (String initalQueryString) {
		queryHistory.add(initalQueryString);
	}
	
	public void updateResult (YahooTop10Results r) {
		result = r;
	}
	
	public String expand () {
		String lastQuery = queryHistory.get(queryHistory.size()-1);
		String augment = "bill"; // @@@ test
		String newQuery = lastQuery + " " + augment;  // @@@ test

		// @@@ Expansion Algorithm - To be implemented
		// @@@ mimicking the reference program...
		System.out.println("Indexing results ....");
		System.out.println("Indexing results ....");
		System.out.println("Augmenting by  " + augment);

		return newQuery;
	}
}
