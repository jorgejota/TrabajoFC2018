package alandb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class Pruebecilla {
	public static void main(String[] args) throws IOException {
		String prueba = "https://www.google.es/?gws_rd=ssl";
		//		HttpClient client = HttpClientBuilder.create().build();
		//		HttpGet request = new HttpGet(prueba);
		//		HttpResponse response = client.execute(request);
		//		Header contenttypeheader = response.getFirstHeader("Content-Type");
		//		System.out.println(contenttypeheader);		
		//		System.exit(0);
		//		try {
		//			estoMetodo1(prueba);
		//		}catch(IOException e) {
		//
		//		}
		//		System.out.println("+++++++++++++++++++");
		//		hacerPeticion(prueba);
		//		String url = "https://papers.ssrn.com/sol3/papers.cfm?abstract_id=466720";
				System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--start-maximized");
				options.addArguments("--disable-popup-blocking");
				Map<String,Object> pref = new HashMap<>();
				pref.put("download.default_directory", "C:\\Users\\jorge\\Desktop\\LIGHT20");
				pref.put("browser.helperApps.neverAsk.saveToDisk", "C:\\Users\\jorge\\Desktop\\LIGHT20");
				pref.put("download.directory_upgrade", true);
				pref.put("download.prompt_for_download", false);
				pref.put("safebrowsing.enabled", true);
				pref.put("profile.default_content_settings.popups", 0);
				pref.put("plugins.always_open_pdf_externally", true);
				//options.addArguments("--headless");
				options.setExperimentalOption("prefs", pref);
				WebDriver driver = new ChromeDriver(options);
				driver.get(prueba);
				WebElement a = driver.findElement(By.xpath("//input[@id='lst-ib']"));
				System.out.println();
				System.out.println("AESPERAAR");


		//		System.setProperty("http.agent", "Mozilla");



		//				Client c = Client.create();
		//				System.out.println("PORQUE ES UN BUCLE");
		//				WebResource wr = c.resource("http://iopscience.iop.org/article/10.1086/661918/pdf");
		//				System.out.println("PORQUE ES UN BUCLE");
		//				ClientResponse resp = wr.accept("application/pdf").header("user-agent", "Mozilla").get(ClientResponse.class);
		//				System.out.println("PORQUE ES UN BUCLE");
		//				if (resp.getStatus() != 200) {
		//					System.out.println("PORQUE ES UN BUCLE");
		//				    throw new RuntimeException("Failed http error code :" + resp.getStatus());
		//				}
		//				System.out.println(resp.getmedi);
	}

	public static void estoMetodo1(String urssl) throws IOException {
		URL url1 =
				new URL(urssl);
		byte[] ba1 = new byte[1024];
		int baLength;
		FileOutputStream fos1 = new FileOutputStream("download.pdf");

		// Contacting the URL
		System.out.print("Connecting to " + url1.toString() + " ... ");
		URLConnection urlConn = url1.openConnection();
		// Checking whether the URL contains a PDF
		if (!urlConn.getContentType().equalsIgnoreCase("application/pdf")) {
			System.out.println("FAILED.\n[Sorry. This is not a PDF.]");
		}else {
			System.out.println("This is a pdf");
		}
	}

	public static void hacerPeticion(String urlString) {
		System.out.println("URL PASADA: " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection;
			connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(40000);
			connection.setInstanceFollowRedirects(false);
			connection.addRequestProperty("User-Agent", "Mozilla");
			//connection.addRequestProperty("Referer", "google.com");
			connection.connect();
			System.out.println(connection.getContentType());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
