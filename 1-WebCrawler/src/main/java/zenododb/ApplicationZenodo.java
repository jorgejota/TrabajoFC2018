package zenododb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ApplicationZenodo {
	public ApplicationZenodo(String nombreDirectorio, String keyWords) {
		this.nombreDirectorio = nombreDirectorio;
		this.palabraBuscar = keyWords;
	}
	public JsonObject objMain;
	public String url;
	public Integer numeroResultados;
	public Integer paginaActual;
	public WebDriver driver;
	public Integer extraccionActual; 
	public String[] type = {"pdf"};
	public String nombreDirectorio;
	public String palabraBuscar;

	public void run() {
		objMain = new JsonObject();
		url = "https://zenodo.org/search?size=20";
		//driver = new HtmlUnitDriver(true);
		/*ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--headless");
		driver = new ChromeDriver(options);*/
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability("screen-resolution", "1280x1024");
		caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "src/main/resources/phantomjs.exe");
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--webdriver-loglevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		driver = new PhantomJSDriver(caps);
		url = chooseTopic(url,type);
		url = chooseSearch(url, palabraBuscar);
		driver.get(url + "&page=1");
		String resultados = driver.findElement(By.xpath("//ng-pluralize[@count='vm.invenioSearchResults.hits.total']")).getText();
		if(resultados.equals("No results.") || resultados.isEmpty()) {
			System.out.println("No se han encontrado resultados");
			return;
		}
		String[] imprimir = resultados.split(" ");
		numeroResultados = Integer.parseInt(imprimir[1]);
		paginaActual = 1;
		extraccionActual = 1;
		boolean avanzamos = true;
		while(avanzamos) {	
			waitJavaScript();
			List<String> uris = newURI(driver);
			extract(uris, driver);
			avanzamos = avanzar(driver);
		}
		driver.close();
	}
	
	public List<String> newURI(WebDriver driver) {
		List<WebElement> elementos = driver.findElements(By.cssSelector("a[class='ng-binding'][href^=\"/record\"]"));
		List<String> uris = new ArrayList<String>();
		for(WebElement e: elementos) {
			uris.add(e.getAttribute("href"));
		}
		return uris;
	}
	public void extract(List<String> uris, WebDriver driver){
		waitJavaScript();
		JsonObject objson;
		for(String s: uris) {
			objson = new JsonObject();
			driver.get(s);
			Document doc = Jsoup.parse(driver.getPageSource());
			String nombreCarpeta = crearCarpeta();
			downloadObjects(doc,nombreCarpeta);
			objson.addProperty("titulo",  doc.select("meta[name=citation_title]").attr("content"));
			objson.addProperty("fecha",  doc.select("meta[name=citation_publication_date]").attr("content"));
			objson.addProperty("abstract", doc.select("meta[name=description]").attr("content"));
			Elements authors = doc.select("meta[name=citation_author]");
			JsonArray nuevaArr = new JsonArray();
			for (Element element : authors) {
				nuevaArr.add(element.attr("content"));
			}
			objson.add("authors", nuevaArr);
			authors = doc.select("meta[name=citation_keywords]");
			nuevaArr = new JsonArray();
			for (Element element : authors) {
				nuevaArr.add(element.attr("content"));
			}
			objson.add("keyWords", nuevaArr);
			objson.addProperty("doi", doc.select("meta[name=citation_doi]").attr("content"));
			objson.addProperty("licence", doc.select("a[rel=license]").text());
			File jsonFile = new File(nombreCarpeta + "\\MetaDatos" + extraccionActual +".json");
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
				writer.write(objson.toString());
				writer.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
			objMain.add("Extraccion" + extraccionActual, objson);
			extraccionActual++;
		}
	}

	public void downloadObjects(Document doc, String nombreCarpeta) {
		//Pdf Download
		Elements downloadLinks = doc.select("meta[name=citation_pdf_url]");
		for (Element element : downloadLinks) {
			String pdfUrl = element.attr("content");
			try {
				URL url = new URL(pdfUrl);
				InputStream in = url.openStream();
				Files.copy(in, Paths.get(nombreCarpeta + "\\Descarga" + extraccionActual + ".pdf"));
				in.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		//		//Image download
		//		downloadLinks = doc.select("link[rel=alternate][type=image/png]");
		//		for (Element element : downloadLinks) {
		//			element.attr("href");
		//		}
		//		downloadLinks = doc.select("link[rel=alternate][type=image/jpeg]");
		//		for (Element element : downloadLinks) {
		//			element.attr("href");
		//		}
		//		//Word download
		//		downloadLinks = doc.select("link[rel=alternate][type=application/vnd.openxmlformats-officedocument.wordprocessingml.document]");
		//		for (Element element : downloadLinks) {
		//			element.attr("href");
		//		}
	}

	public boolean avanzar(WebDriver driver){
		if(numeroResultados <= 20) {
			File jsonFile = new File(nombreDirectorio + "\\MetaDatosGlobal" + ".json");
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
				writer.write(objMain.toString());
				writer.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		numeroResultados -= 20;
		paginaActual = paginaActual+1;
		driver.get(url + "&page=" + paginaActual);
		return true;
	}
	public String chooseTopic(String myUrl,String[] type){
		for(String topic:type)
			myUrl += "&file_type=" + topic;
		return myUrl;
	}
	public String chooseSearch(String myUrl,String selection){
		if(selection!=null && !selection.isEmpty())
			myUrl += "&q="+selection;
		return myUrl;
	}

	public boolean waitJavaScript() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		final JavascriptExecutor js = (JavascriptExecutor)driver;
		ExpectedCondition<Boolean> jQuery = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					return ((Long)js.executeScript("return jQuery.active") == 0);
				}
				catch (Exception e) {
					return true;
				}
			}
		};
		ExpectedCondition<Boolean> javaScript = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return js.executeScript("return document.readyState")
						.toString().equals("complete");
			}
		};
		return wait.until(jQuery) && wait.until(javaScript);
	}
	
	private String crearCarpeta() {
		String nuevaRuta = this.nombreDirectorio + "\\" + "Extraccion" + this.extraccionActual;
		int i = 1;
		String intentar = nuevaRuta;
		while(!(new File(intentar)).mkdirs()) {
			intentar = nuevaRuta + "(" + i + ")";
			i++;
		}
		return intentar + "\\";
	}

}
