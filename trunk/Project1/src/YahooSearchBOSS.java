import java.net.*;
import java.io.*;

public class YahooSearchBOSS {
	String _appid = null;
	String _urlbase = "http://boss.yahooapis.com/ysearch/web/v1/";

	public YahooSearchBOSS (String appid) {
		_appid = appid;
	}
	public YahooSearchBOSS () {
		_appid = "ypykm2bV34HB8360S0knusfiUrQYS5A3ZvDlsTIHh13Vw8BPYSUHNloyoJ2bSg--";
	}
	
	public String search (String term, String format) {
		String result = null;
		try {
			//String termenc = URLEncoder.encode("\"" + term + "\"","UTF-8");
			String termenc = URLEncoder.encode(term,"UTF-8");
			String url = _urlbase + termenc + "?appid=" + _appid + "&format="+format;
			System.out.println("URL=" + url); // @@@ debug
			result = urlGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String searchXML (String term) {
		return search(term,"xml");
	}
	public String searchJSON (String term) {
		return search(term,"json");
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
