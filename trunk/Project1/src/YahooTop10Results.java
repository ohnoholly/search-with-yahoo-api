import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

public class YahooTop10Results {
	
	ArrayList<ResultNode> _arr = null;	// Array of top 10 results

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
	public YahooTop10Results(String res) {
		_arr = new ArrayList<ResultNode>();
		buildArray(res);
	}
	
	/**
	 * Parses results data and compiles into an object array
	 * @param res A string of data in JSON format
	 */
	public void buildArray(String res) {
		
		Scanner scan = new Scanner(res);
		
		// Parse the no of results
		String strcount = scan.findInLine("\"count\":\"(\\w+)\"");
		int count = Integer.parseInt(scan.match().group(1));
		
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
		
		listResults(count);
		
		scan.close();
	}
	
	public void listResults(int count) {
		
		System.out.println("Total no of results : "+count);

		System.out.println("Yahoo! Search Results:");
		System.out.println("======================");
		
		int i = 0;
		for (ResultNode node : _arr) {
			System.out.println("Result "+ ++i);
			System.out.println(node);
			// @@@ Add code to ask user if relevant
		}
		
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
			// @@@ Need to sanitize the strings to remove HTML code
			this.title = title;
			this.url = url;
			this.summary = summary;
		}
		
		/**
		 * Prints the node
		 */
		public String toString() {
			String str = String.format("[\n URL: %s\n Title: %s\n Summary: %s\n]\n", url, title, summary);
			return str;
		}
		
		/**
		 * Accepts a string with "N" or "Y" to alter node's relevance
		 * @param bool a String that is "N" or "Y"
		 */
		public void isRelevant (String bool) {
			
			if (bool.equalsIgnoreCase("Y"))
				relevant = true;
			else
				relevant = false;
			
		}
		
	}
	
}
