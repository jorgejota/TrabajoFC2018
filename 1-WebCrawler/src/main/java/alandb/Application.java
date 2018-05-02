package alandb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

public class Application {
	public Application(String nombreDirectorio, String keyWords) {
		this.nombreDirectorio = nombreDirectorio;
		this.palabraBuscar = keyWords;
	}
	public JsonObject objMain;
	public Integer numero = 0;
	public List<Integer> aDesargar = new ArrayList<Integer>();
	public WebDriver driver;
	public Integer extraccionActual; 
	public String nombreDirectorio;
	public String palabraBuscar = "contamination";

	public void run() {
		objMain = new JsonObject();
		//System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		String url = "http://alandb.darksky.org/";
		
		/*ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--headless");
		driver = new ChromeDriver();*/
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability("screen-resolution", "1280x1024");
		caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs.exe");
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--webdriver-loglevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		driver = new PhantomJSDriver(caps);
		driver.get(url);
		driver.findElement(By.id("quickSearchName")).sendKeys(palabraBuscar);
		driver.findElement(By.xpath("//div[@id='querySubmit']//input[@type='submit']")).click();
		extraccionActual = 1;
		boolean avanzamos = true;
		while(avanzamos) {
			waitJavaScript();
			HashMap<String,String> uris = newURI();
			url = driver.getCurrentUrl();
			extract(uris);
			driver.get(url);
			avanzamos = avanzar();
		}
		driver.close();
	}
	public HashMap<String,String> newURI() {
		List<WebElement> elementos = driver.findElements(By.cssSelector("a[href^=\"show.php?record=\"]"));
		HashMap<String,String> uris = new HashMap<String, String>();
		for(WebElement e: elementos) {
			String url;
			try {
				url = e.findElement(By.xpath("following-sibling::*")).getAttribute("href");
			}catch (org.openqa.selenium.NoSuchElementException exp) {
				url = null;
			}
			uris.put(e.getAttribute("href"),url);
		}
		return uris;
	}
	public void extract(HashMap<String,String> uris){
		waitJavaScript();
		uris.forEach((k, v) -> {
			driver.get(k);
			JsonObject objson = new JsonObject();
			String nombreCarpeta = crearCarpeta();
			objson.addProperty("titulo", driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='3']")).getText());
			objson.addProperty("abstractText", driver.findElement(By.cssSelector("td[class='otherfieldsbg'][colspan='5']")).getText());
			objson.addProperty("year", Integer.parseInt(driver.findElement(By.cssSelector("a[href^=\"show.php?year=\"]")).getText()));
			String[] authors = driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='5']")).getText().split(";");
			JsonArray nuevaArr = new JsonArray();
			for (String str: authors) {
				nuevaArr.add(str);
			}
			objson.add("authors", nuevaArr);
			String[] keyWords = authors = driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='5']")).getText().split(";");
			nuevaArr = new JsonArray();
			for (String str: keyWords) {
				nuevaArr.add(str);
			}
			objson.add("keywords", nuevaArr);
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
			if(v != null) {
				if(esPdf(v)) {
					hacerPeticion(v, nombreCarpeta);
				}
				else {
					driver.get(v);
					tryExtract(nombreCarpeta);
				}
			}
		});
	}
	public boolean avanzar(){
		try {
			driver.findElement(By.xpath("//a[contains(@title,'display next results page')]")).click();
			return true;
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		}
	}
	public void tryExtract(String nombreCarpeta) {
		try {
			Document doc = Jsoup.connect(driver.getCurrentUrl()).get();
			Elements a = doc.select("meta[name=citation_pdf_url]");
			if(a.size() != 0) {
				if(hacerPeticion(a.get(0).attr("content"),nombreCarpeta))
					return;
			}
			a = doc.select("a:contains(PDF)");
			if(a.size() != 0) {
				if(hacerPeticion(a.get(0).absUrl("href"),nombreCarpeta))
					return;
			}
			a = doc.select("a:contains(DOWNLOAD)");
			if(a.size() != 0) {
				if(hacerPeticion(a.get(0).absUrl("href"),nombreCarpeta))
					return;
			}
			try {
				WebElement e = driver.findElement(By.xpath("//*[contains(text(),'pdf')]"));
				String url = e.getAttribute("href");
				if(url != null)
					hacerPeticion(url,nombreCarpeta);
			} catch( org.openqa.selenium.NoSuchElementException e) {

			}
			try {
				WebElement e = driver.findElement(By.xpath("//*[contains(text(),'download')]"));
				String url = e.getAttribute("href");
				if(url != null)
					hacerPeticion(url,nombreCarpeta);
			} catch ( org.openqa.selenium.NoSuchElementException e) {

			}
		}catch( org.jsoup.HttpStatusException ex) {
			System.out.println("La pagina " + driver.getCurrentUrl() + "\n ¡NO ADMITE ROBOTS!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * TryExtract antiguo. El nuevo esta basado en algoritmo justificado.
	 */
	//	public void tryExtract() {
	//		waitJavaScript();
	//		List<WebElement> nuevo = new ArrayList<WebElement>();
	//		try {
	//			nuevo = driver.findElements(By.xpath("//a[contains(@href, 'pdf')]"));
	//			for (WebElement webElement : nuevo) {
	//				String uri = webElement.getAttribute("href");
	//				System.out.println("Hemos obtenido el link: " + uri);
	//				hacerPeticion(uri);
	//			}	
	//		}
	//		//Bloque para hacer click
	//		catch (org.openqa.selenium.NoSuchElementException e) {
	//			System.out.println("Puede que haya un boton vamos a ver");
	//			//Creo que no lanza excepcion, solo una lista vacia
	//			List<WebElement> listElementClass = driver.findElements(By.xpath("//a[contains(@class, 'pdf')]"));
	//			List<WebElement> listElementTittle = driver.findElements(By.xpath("//a[contains(@tittle, 'pdf')]"));
	//			if(!change(listElementClass))
	//				change(listElementTittle);
	//		}
	//		//Nuevo bloque donde hacer click -> Invalid URI
	//		/*catch (org.openqa.selenium.WebDriverException e) {
	//			nuevo.click();
	//			String uriPdf = driver.getCurrentUrl();
	//			hacerPeticion(uriPdf);
	//		}*/
	//		//Opcion para debuguear
	//		catch (Exception e) {
	//			System.out.println("Hay que tratar esta excepcion");
	//			System.exit(0);
	//		}
	//	}

	/**
	 * En caso de que fuera necesario cambiar de pesta�a.
	 * @param lista
	 * @return
	 */
	//	public boolean change(List<WebElement> lista) {
	//		boolean descargado = false;
	//		String currentURI = driver.getCurrentUrl();
	//		for (WebElement webElement : lista) {
	//			webElement.click();
	//			//Para dejar que cargue la pagina
	//			waitJavaScript();
	//			Set<String> winHandleBefore = driver.getWindowHandles();
	//			if(winHandleBefore.size() > 1) {
	//				String[] arrayString = winHandleBefore.toArray(new String[winHandleBefore.size()]);
	//				driver.switchTo().window(arrayString[arrayString.length-1]);
	//				String urii = driver.getCurrentUrl();
	//				descargado = hacerPeticion(urii);
	//				driver.close();
	//				driver.switchTo().window(arrayString[0]);
	//			}
	//			else {
	//				String urii = driver.getCurrentUrl();
	//				descargado = hacerPeticion(urii);
	//			}
	//			if(descargado)
	//				break;
	//		}
	//		return descargado;
	//	}

	public boolean hacerPeticion(String urlString, String nombreCarpeta) {
		if(!esPdf(urlString))
			return false;
		URL url;
		try {
			url = new URL(urlString);
			InputStream in = url.openStream();
			Files.copy(in, Paths.get(nombreCarpeta + "\\Descarga" + extraccionActual + ".pdf"));
			numero++;
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean esPdf(String urlString) {
		URL url;
		URLConnection u;
		try {
			url = new URL(urlString);
			u = url.openConnection();
			if(urlString == null || urlString.isEmpty() ||
					(u.getHeaderField("Content-Type") != null && !u.getHeaderField("Content-Type").equals("application/pdf"))){
				return false;
			}
		} catch (IOException e) {
			return false;
		}
		return true;
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
