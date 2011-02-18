import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.*;

public class YahooTop10Results {
	
	ArrayList<ResultNode> _arr = null;	// Array of top 10 results
	int count = 0;
	float desiredPrec = 0;
	String originalQuery = null;
	
	/**
	 * Constructor
	 */
	public YahooTop10Results () {
		_arr = new ArrayList<ResultNode>();
	}
	
	/**
	 * Constructor with result data
	 * @param res A string of data in JSON format
	 */
	public YahooTop10Results(String res, String query, float prec) {
		_arr = new ArrayList<ResultNode>();
		originalQuery = query;
		desiredPrec = prec;
		buildArray(res);
	}
	
	/**
	 * Parses results data and compiles into an object array
	 * @param res A string of data in JSON format
	 */
	public void buildArray(String res) {
		
		Scanner scan = new Scanner(res);
		
		// Parse the no of results
		scan.findInLine("\"count\":\"(\\w+)\"");
		count = Integer.parseInt(scan.match().group(1));
		
		// Parse the results abstract, title and url
		String matchRes = "\"abstract\":\"(.*?)\",.*?\"title\":\"(.*?)\",\"url\":\"(.*?)\"";
		
		// Store each result into array
		for (int i = 1; i <= 10; i++) {
			
			// Match one result
			scan.findInLine(matchRes);

			// Store the three values into ResultNode
			String summary = scan.match().group(1);
			String title = scan.match().group(2);
			String url = scan.match().group(3);
			
			ResultNode node = new ResultNode(summary, title, url);
			
			_arr.add(node);
		}
		
		scan.close();
	}
	
	/**
	 * Prints the results and prompts for relevance judgment
	 * @param count No of results
	 */
	public void listResults() {
		
		System.out.println("Total no of results : "+count);

		System.out.println("Yahoo! Search Results:");
		System.out.println("======================");
		
		Scanner in = new Scanner(System.in);
		
		// Iterate over the results
		int i = 0;
		for (ResultNode node : _arr) {
			System.out.println("Result "+ ++i);
			System.out.println(node);
			// Ask user if the result is relevant
			getRelevance(in, node);
		}
		
		in.close();
		
		getFeedback(originalQuery); // @@@ Change when query is altered
	}
	
	/**
	 * Prompts user if result is relevant
	 * @param in Scanner for getting user input
	 * @param node The result node in question
	 */
	public void getRelevance(Scanner in, ResultNode node) {
		System.out.println("Relevant (Y/N)?");
		String rel = in.next("[YyNn]"); // @@@ Need more elegant exception handling here
		
		// Set the node's relevance
		node.isRelevant(rel);
	}
	
	/**
	 * Calculates the precision score of results in array
	 * @return a precision score
	 */
	public float getResultPrec() {
		float prec = 0;
		
		// Add up the number of relevant nodes
		for (ResultNode node : _arr) {
			if (node.relevant == true)
				prec++;
		}
		
		// Divide by 10 to get score
		return prec / 10;
	}
	
	/**
	 * Prints feedback summary after one pass
	 * @param query String containing original query
	 */
	public void getFeedback(String query) {
		
		System.out.println("======================");
		System.out.println("FEEDBACK SUMMARY");
		
		System.out.println("Query "+query);
		System.out.println("Precision "+getResultPrec());
		
		// Print this if the desired precision score is reached
		if (getResultPrec() >= desiredPrec)
			System.out.println("Desired precision reached, done");
		
		// @@@ what happens if not reached...
	}
	
	/**
	 * Class containing information about each result and its relevance
	 */
	public class ResultNode {
		
		String title;
		String url;
		String summary;
		
		// Tracks if the result is relevant
		Boolean relevant = false;
		
		/**
		 * Constructor for result node
		 * @param summary Result abstract
		 * @param title Result title
		 * @param url Result URL
		 */
		public ResultNode (String summary, String title, String url) {
			this.title = trim(title);
			this.url = trim(url);
			this.summary = trim(summary);
		}
		
		/**
		 * Prints the node
		 */
		public String toString() {
			String str = String.format("[\n URL: %s\n Title: %s\n Summary: %s\n]\n", url, title, summary);
			return str;
		}
		
		/**
		 * Sets the relevance of a result
		 * @param bool a String that is "N" or "Y"
		 */
		public void isRelevant (String bool) {
			if (bool.equalsIgnoreCase("Y"))
				relevant = true;
			else
				relevant = false;
		}
		
	}
	
	/**
	 * Removes HTML code and replaces escaped characters
	 * @param str A string from Yahoo
	 * @return A clean string
	 */
	public static String trim(String str) {
		String newStr = null;
		
		newStr = str.replaceAll("\\<.*?b\\>", "");
		newStr = newStr.replaceAll("\\\\/", "/");
		newStr = newStr.replaceAll("\\\\\"", "\"");

		return newStr;
	}
	
}
