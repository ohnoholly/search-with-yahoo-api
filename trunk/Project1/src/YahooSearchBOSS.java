import java.net.*;
import java.io.*;

public class YahooSearchBOSS {
	private String _appid = null;
	private String _urlbase = "http://boss.yahooapis.com/ysearch/web/v1/";

	public YahooSearchBOSS (String appid) {
		_appid = appid;
	}
	
	public YahooTop10Results search (String term) throws SmartSearchException {
		String result = search(term,"json");
		if (result == null) {
			throw new SmartSearchException("Your search string '" + term + "' yielded no result.");
		}

		YahooTop10Results results = new YahooTop10Results(result);
		return results;
	}
	public String search (String term, String format) {
		String result = null;
		try {
			//String termenc = URLEncoder.encode("\"" + term + "\"","UTF-8");
			String termenc = URLEncoder.encode(term,"UTF-8");
			String url = _urlbase + termenc + "?appid=" + _appid + "&format="+format;
			System.out.println("URL: " + url); // @@@ debug
			result = urlGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String urlGet (String urlstr) throws MalformedURLException, IOException {
		URL url = new URL(urlstr);
		URLConnection urlconn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
        String inputLine;
        StringBuilder sb = new StringBuilder(1000);
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine + "\n");
        }
        in.close();
        return sb.toString();
	}
	
}
