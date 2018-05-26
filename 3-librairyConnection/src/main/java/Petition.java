import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

public class Petition {
//	public static void main(String args[]) throws IOException {
//		HttpClient httpclient = HttpClients.createDefault();
//		//HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");
//		HttpGet httpget = new HttpGet("http://lab4.librairy.linkeddata.es/jgalan-topics/documents");
//		// Request parameters and other properties.
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("u", "jgalan:oeg2018"));
//		params.add(new BasicNameValuePair("X", "GET \"http://lab4.librairy.linkeddata.es/jgalan-topics/documents\""));
//		params.add(new BasicNameValuePair("H", "\"accept: application/json\""));
//		//    	params.add(new BasicNameValuePair("param-1", "12345"));
//		//    	params.add(new BasicNameValuePair("param-2", "Hello!"));
//		//    	httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//
//		//Execute and get the response.
//		HttpResponse response = httpclient.execute(httpget);
//		HttpEntity entity = response.getEntity();
//
//		if (entity != null) {
//			InputStream instream = entity.getContent();
//			System.out.println(response.getStatusLine().getStatusCode());
//			try {
//				// do something useful
//			} finally {
//				instream.close();
//			}
//		}
//	}
	public static void main(String[] args) {
		String usuario = "j"
        String stringUrl = "https://qualysapi.qualys.eu/api/2.0/fo/report/?action=list";
        URL url = new URL(stringUrl);
        URLConnection uc = url.openConnection();
        uc.setRequestProperty("X-Requested-With", "Curl");
        String userpass = "username" + ":" + "password";
        String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
        uc.setRequestProperty("Authorization", basicAuth);
        InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
        // read this input
    }
}