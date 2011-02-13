
public class SmartSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2 || args.length > 3)
			usage("Invalid Arguments");

		String searchStr = args[0];
		float precision = 0;
		String appid = null;
		if (args.length >= 3) 
			appid = args[2];
		else
			appid = "ypykm2bV34HB8360S0knusfiUrQYS5A3ZvDlsTIHh13Vw8BPYSUHNloyoJ2bSg--";
				
	    try {
			precision = Float.parseFloat(args[1]);
	    } catch (NumberFormatException e) {
	        System.err.println("Precision must be a real number between 0 and 1");
	        System.exit(1);
	    }

	    System.out.println("DEBUG: searchStr = " + searchStr);
	    System.out.println("DEBUG: precision = " + precision);
	    System.out.println("DEBUG: appid = " + appid);
	    
		YahooSearchBOSS yahoo = new YahooSearchBOSS(appid);
		//String result = yahoo.searchJSON("Apple Pie");
		String result = yahoo.searchXML(searchStr);		
		if (result != null) {
			System.out.println(result);
		}
	}
	
    public static void usage(String errMsg) {
    	if (errMsg != null)
    		System.err.println(errMsg);
    	usage();
    }

    public static void usage() {
    	// This is like $0 in perl/shell
    	StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
        StackTraceElement main = stack[stack.length - 1];
        String mainClass = main.getClassName ();
        
        System.err.println("Usage: " + mainClass + " <query> <precision> [<yahoo appId>]");
        System.exit(1);
    }
}
