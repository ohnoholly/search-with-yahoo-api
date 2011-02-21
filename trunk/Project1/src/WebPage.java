/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class stores data of a web page.
 ****/

public class WebPage {
	
	// The full text of a web page
	private String fullText;
	
	/**
	 * Constructor
	 * @param data HTML document
	 */
	public WebPage(String data) {
		fullText = stripHTML(data);
	}
	
	/**
	 * Simplifies data by removing HTML tags
	 * @param str data
	 * @return text without HTML
	 */
	public String stripHTML(String str) {
		String newStr = str.replaceAll("\n", "");
		newStr = str.replaceAll("\\<.*?\\>", "");
		return newStr;
	}
	
	/**
	 * Returns full text of HTML doc
	 * @return
	 */
	public String getFullText() {
		return fullText;
	}

}
