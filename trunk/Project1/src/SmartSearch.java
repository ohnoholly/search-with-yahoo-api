/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	Primary class
 ****/

public class SmartSearch {

	/**
	 * @param args Takes as arguments: query, precision goal and optionally appid
	 */
	public static void main(String[] args) {
		
		// Check if there are 2 or 3 arguments provided
		if (args.length < 2 || args.length > 3)
			usage("Invalid Arguments");

		// Set search string
		String searchStr = args[0];
		
		// Set appid (optional)
		String appid = (args.length >= 3) ? args[2] : 
			"ypykm2bV34HB8360S0knusfiUrQYS5A3ZvDlsTIHh13Vw8BPYSUHNloyoJ2bSg--";
				
		// Set precision goal
		float precisionGoal = 0;
	    try {
			precisionGoal = Float.parseFloat(args[1]);
	    } catch (NumberFormatException e) {
	        System.err.println("Precision must be a real number between 0 and 1");
	        System.exit(1);
	    }
	    
	    // Instantiate new Yahoo Search
		YahooSearchBOSS yahoo = new YahooSearchBOSS(appid);
		
		// Instantiate new Query Expansion
		QueryExpansion qex = new QueryExpansion(searchStr);
		
		try {
			for (String query = searchStr; ; query = qex.expand()) {
				
				// Return parameters to user
				System.out.println("Parameters:");
				System.out.println("Client key = " + appid);
				System.out.println("Query      = " + query);
				System.out.println("Precision  = " + precisionGoal);

				// Send query and parse result
				YahooTop10Results results = yahoo.search(query);  
				results.getUserFeedback();                        // Get user feedback
				float precision = results.getPrecision();         // Calculate result precision

				// Return feedback summary to user
				System.out.println("======================");
				System.out.println("FEEDBACK SUMMARY");
				System.out.println("Query " + query);
				System.out.println("Precision " + precision);

				// Check the precision result
				if (precision < precisionGoal) {
					// No relevant results
					if (precision <= 0) {
						System.out.println("Below desired precision, but can no longer augment the query");
						break;
					}
					// Below desired precision, expand query
					else {
						System.out.println("Still below the desired precision of " + precisionGoal);
						qex.updateResult(results); 
					}
				// Desired precision reached
				} else {
					System.out.println("Desired precision reached, done");
					break;
				}
			} // end for
		} catch(SmartSearchException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	} // end main
	
	/**
	 * Returns an error message and usage information
	 * @param errMsg A string with the desired error message
	 */
    public static void usage(String errMsg) {
    	if (errMsg != null)
    		System.err.println(errMsg);
    	usage();
    }

    /**
     * Returns information about the expected usage
     */
    public static void usage() {
    	// This is like $0 in perl/shell
    	StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
        StackTraceElement main = stack[stack.length - 1];
        String mainClass = main.getClassName ();
        
        System.err.println("Usage: " + mainClass + " <query> <precision> [<yahoo appId>]");
        System.exit(1);
    }
}
