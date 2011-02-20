/**
 * Class containing information about each result and its relevance
 */
public class ResultNode {

	private String title;
	private String url;
	private String summary;

	// Tracks if the result is relevant
	private boolean relevant = false;

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
	 * Sets the relevance of a result
	 * @param bool a String that is "N" or "Y"
	 */
	public void isRelevant (String bool) {
		if (bool.equalsIgnoreCase("Y"))
			relevant = true;
		else
			relevant = false;
	}
	public boolean isRelevant () {
		return relevant;
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

	/**
	 * Prints the node
	 */
	public String toString() {
		String str = String.format("[\n URL: %s\n Title: %s\n Summary: %s\n]\n", url, title, summary);
		return str;
	}
}

