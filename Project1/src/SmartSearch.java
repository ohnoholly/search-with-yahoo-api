/****
 *  Nicole Lee (), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	Primary class
 ****/


public class SmartSearch {

	/**
	 * @param args Takes as arguments query, precision and optionally appid
	 */
	public static void main(String[] args) {
		
		// Check if there are 2 or 3 arguments provided
		if (args.length < 2 || args.length > 3)
			usage("Invalid Arguments");

		// Search string
		String searchStr = args[0];
		
		// Appid (optional)
		String appid = null;
		if (args.length >= 3) 		
			appid = args[2];
		else
			appid = "ypykm2bV34HB8360S0knusfiUrQYS5A3ZvDlsTIHh13Vw8BPYSUHNloyoJ2bSg--";
				
		// Precision
		float precision = 0;
	    try {
			precision = Float.parseFloat(args[1]);
	    } catch (NumberFormatException e) {
	        System.err.println("Precision must be a real number between 0 and 1");
	        System.exit(1);
	    }
	    
	    // Return parameters to user
	    System.out.println("Parameters:");
	    System.out.format("Client key\t= %1$s\nQuery\t\t= %2$s\nPrecision\t= %3$s\n", appid, searchStr, precision);
	    
	    // Instantiate new Yahoo Search
		YahooSearchBOSS yahoo = new YahooSearchBOSS(appid);
		
		// Capture result of search in JSON format
		String result = yahoo.searchJSON(searchStr);
		
		// Print results if not null
		if (result != null) {
			//System.out.println(result);
			YahooTop10Results results = new YahooTop10Results(result, searchStr, precision);
			results.listResults();
		}
		
	}
	
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
