package zenododb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ApplicationZenodo {
	public ApplicationZenodo(String nombreDirectorio, String keyWords, boolean allJoin) {
		this.nombreDirectorio = nombreDirectorio;
		this.palabraBuscar = keyWords;
		this.allJoin = allJoin;
		this.previousLinks = new ArrayList<>();
	}
	public ApplicationZenodo(String nombreDirectorio, String keyWords, boolean allJoin, boolean json) {
		this.nombreDirectorio = nombreDirectorio;
		this.palabraBuscar = keyWords;
		this.allJoin = allJoin;
		this.previousLinks = new ArrayList<>();
		this.json = json;
	}
	public ApplicationZenodo(String nombreDirectorio, String keyWords) {
		this.nombreDirectorio = nombreDirectorio;
		this.palabraBuscar = keyWords;
		this.allJoin = false;
		this.previousLinks = new ArrayList<>();
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
	public File previousFile;
	public List<String> previousLinks;
	public boolean allJoin;

	//Para CKAN
	public boolean json;
	public List<JsonObject> listaJson;
	
	public List<JsonObject> extractAllJson(){
		listaJson = new ArrayList<>();
		run();
		return listaJson;
	}
	
	public void run() {
		java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
		loadPreviousDownloads();
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		objMain = new JsonObject();
		url = "https://zenodo.org/search?size=20";
		//driver = new HtmlUnitDriver(true);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-popup-blocking");
		//options.addArguments("--headless");
		driver = new ChromeDriver(options);
		/*DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability("screen-resolution", "1280x1024");
		caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "src/main/resources/phantomjs.exe");
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--webdriver-loglevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		driver = new PhantomJSDriver(caps);*/
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
		System.out.println(numeroResultados);
		paginaActual = 1;
		extraccionActual = 1;
		boolean avanzamos = true;
		while(avanzamos) {	
			List<String> uris = newURI(driver);
			extract(uris, driver);
			avanzamos = avanzar(driver);
		}
		driver.close();
	}

	public List<String> newURI(WebDriver driver) {
		waitJavaScript();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		List<WebElement> elementos = driver.findElements(By.xpath("//h4//a[@class='ng-binding']"));
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
			if(comprobarRecorrido(s)) {
				objson = new JsonObject();
				driver.get(s);
				Document doc = Jsoup.parse(driver.getPageSource());
				String nombreCarpeta;
				if(!allJoin)
					nombreCarpeta = crearCarpeta();
				else
					nombreCarpeta = nombreDirectorio;
				List<String> descargadosMios = downloadObjects(doc,nombreCarpeta);
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
				nuevaArr = new JsonArray();
				for (String element : descargadosMios) {
					nuevaArr.add(element);
				}
				objson.add("downloadObjects", nuevaArr);
				File jsonFile = new File(nombreCarpeta + "\\ZenodoMeta" + extraccionActual +".json");
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
					writer.write(objson.toString());
					writer.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
				objMain.add("Extraccion" + extraccionActual, objson);
				if(json)
					this.listaJson.add(objson);
				extraccionActual++;
				previousLinks.add(s);
				try {
					Files.write(previousFile.toPath(), (s + "\n").getBytes(), StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println();
			}
		}
	}

	public boolean comprobarRecorrido(String link) {
		if(previousLinks.contains(link))
			return false;
		return true;
	}

	public void loadPreviousDownloads(){
		previousFile = new File(nombreDirectorio + "\\DOWNLOADURLs.txt");
		previousLinks = new ArrayList<>();
		if(!previousFile.exists()) {
			try {
				previousFile.createNewFile();
				extraccionActual = 1;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try (BufferedReader br = new BufferedReader(new FileReader(previousFile))) {
				String line;
				while ((line = br.readLine()) != null) {
					previousLinks.add(line);
				}
				extraccionActual = previousLinks.size() - 1;
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void descargarObjeto(String urlMia,String nameDescargar, String nombreCarpeta) {
		try {
			URL url = new URL(urlMia);
			InputStream in = url.openStream();
			Files.copy(in, Paths.get(nombreCarpeta + "\\" + nameDescargar));
			in.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List<String> downloadObjects(Document doc, String nombreCarpeta) {
		//Pdf Download
		List<String> descargados = new ArrayList<>();
		Elements downloadLinks = doc.select("meta[name=citation_pdf_url]");
		String aDescargar = "";
		for (Element element : downloadLinks) {
			String pdfUrl = element.attr("content");
			System.out.println(pdfUrl);
			Pattern pattern = Pattern.compile("files\\/(.*)");
			Matcher matcher = pattern.matcher(pdfUrl);
			if(matcher.find()) {
				aDescargar = matcher.group(1);
			}
			else {
				int random = (int )(Math.random() * (this.numeroResultados * 2) + 1);
				aDescargar = "PdfZenodo" + random + ".pdf";
			}
			descargados.add(aDescargar);
			descargarObjeto(pdfUrl,aDescargar,nombreCarpeta);
		}

		//Image download -- PNG
		downloadLinks = doc.select("link[rel=alternate][type=image/png]");
		for (Element element : downloadLinks) {
			String imageUrl = element.attr("href");
			Pattern pattern = Pattern.compile("files\\/(.*?)");
			String aCompilar = driver.getCurrentUrl();
			Matcher matcher = pattern.matcher(aCompilar);
			if(matcher.find()) {
				aDescargar = matcher.group(1);
			}
			else {
				int random = (int )(Math.random() * (this.numeroResultados * 2) + 1);
				aDescargar = "ImageZenodo" + random + ".png";
			}
			descargados.add(aDescargar);
			descargarObjeto(imageUrl,aDescargar,nombreCarpeta);
		}

		/*
		 * Comprobar que el usuario quiere descargar las imagenes y los DOC tambien.
		 * Comprobar que estos existen dentro de la URL pasada o SE TE PARA EL PROGRAMA
		 */
		//		//Image download -- JPEG
		//		downloadLinks = doc.select("link[rel=alternate][type=image/jpeg]");
		//		for (Element element : downloadLinks) {
		//			element.attr("href");
		//			String imageUrl = element.attr("href");
		//			Pattern pattern = Pattern.compile("files\\/(.*?)");
		//			String aCompilar = driver.getCurrentUrl();
		//			Matcher matcher = pattern.matcher(aCompilar);
		//			if(matcher.find()) {
		//				aDescargar = matcher.group(1);
		//			}
		//			else {
		//				int random = (int )(Math.random() * (this.numeroResultados * 2) + 1);
		//				aDescargar = "ImageZenodo" + random + ".jpeg";
		//			}
		//			descargados.add(aDescargar);
		//			descargarObjeto(imageUrl,aDescargar,nombreCarpeta);
		//		}
		//		
		//		//Word download
		//		downloadLinks = doc.select("link[rel=alternate][type=application/vnd.openxmlformats-officedocument.wordprocessingml.document]");
		//		for (Element element : downloadLinks) {
		//			element.attr("href");
		//			String imageUrl = element.attr("href");
		//			Pattern pattern = Pattern.compile("files\\/(.*?)");
		//			String aCompilar = driver.getCurrentUrl();
		//			Matcher matcher = pattern.matcher(aCompilar);
		//			if(matcher.find()) {
		//				aDescargar = matcher.group(1);
		//			}
		//			else {
		//				int random = (int )(Math.random() * (this.numeroResultados * 2) + 1);
		//				aDescargar = "ImageZenodo" + random + ".docx";
		//			}
		//			descargados.add(aDescargar);
		//			descargarObjeto(imageUrl,aDescargar,nombreCarpeta);
		//		}

		return descargados;
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
		myUrl += "&access_right=open";
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
