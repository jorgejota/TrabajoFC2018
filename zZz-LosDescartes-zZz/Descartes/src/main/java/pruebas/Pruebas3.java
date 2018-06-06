package pruebas;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Pruebas3 {
	public static Integer numero = 0;
	public static void main(String[] args) throws IOException {
		//String a = "https://www.google.es/";
		String a = "http://oa.upm.es/49679/1/TFG_JAVIER_BARREIRO_ORTIZ.pdf";
		URL url = new URL(a);
		hacerPeticion(a);
	}
	public static boolean hacerPeticion(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
			URLConnection u = url.openConnection();
			String type = u.getHeaderField("Content-Type");
			System.out.println("type: "+ type);
			InputStream in = url.openStream();
			Files.copy(in, Paths.get("DownloadPdf/HOLLAAAAA" + numero + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
			numero++;
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return true;
	}
	public static boolean esPdf(String urlString) {
		if(urlString == null || urlString.isEmpty() ||
				!urlString.substring(urlString.length()-3).equals("pdf")) {
			System.out.println("No se lo que me has pasado ... : " + urlString);
			return false;
		}
		return true;
	}
	public static void otraAlternativa(URL url1) throws IOException {


	    byte[] ba1 = new byte[1024];
	    int baLength;
	    FileOutputStream fos1 = new FileOutputStream("DownloadPdf/download.pdf");

	    try {
	      // Contacting the URL
	      System.out.print("Connecting to " + url1.toString() + " ... ");
	      URLConnection urlConn = url1.openConnection();

	      // Checking whether the URL contains a PDF
	      if (!urlConn.getContentType().equalsIgnoreCase("application/pdf")) {
	          System.out.println("FAILED.\n[Sorry. This is not a PDF.]");
	      } else {
	        try {

	          // Read the PDF from the URL and save to a local file
	          InputStream is1 = url1.openStream();
	          while ((baLength = is1.read(ba1)) != -1) {
	              fos1.write(ba1, 0, baLength);
	          }
	          fos1.flush();
	          fos1.close();
	          is1.close();
	        } catch (IOException ce) {
	          System.out.println("FAILED.\n[" + ce.getMessage() + "]\n");
	        }
	      }

	    } catch (NullPointerException npe) {
	      System.out.println("FAILED.\n[" + npe.getMessage() + "]\n");
	    }
	}
}
