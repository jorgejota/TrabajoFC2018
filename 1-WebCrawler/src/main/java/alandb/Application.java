package alandb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class Application {
	public Application(String nombreDirectorio, String keyWords, boolean allJoin) {
		this.nombreDirectorio = nombreDirectorio;
		this.palabraBuscar = keyWords;
		this.allJoin = allJoin;
	}
	public Application(String nombreDirectorio, String keyWords) {
		this.nombreDirectorio = nombreDirectorio;
		this.palabraBuscar = keyWords;
		this.allJoin = false;
	}

	public JsonObject objMain;
	public Integer numero = 0;
	public List<Integer> aDesargar = new ArrayList<Integer>();
	public WebDriver driver;
	public Integer extraccionActual; 
	public String nombreDirectorio;
	public String palabraBuscar = "contamination";
	public boolean allJoin;
	public File previousFile;
	public List<String> previousLinks;

	public void run() {
		objMain = new JsonObject();
		loadPreviousDownloads();
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		String url = "http://alandb.darksky.org/";
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-popup-blocking");
		Map<String,Object> pref = new HashMap<>();
		pref.put("download.default_directory", nombreDirectorio);
		pref.put("browser.helperApps.neverAsk.saveToDisk", nombreDirectorio);
		pref.put("download.directory_upgrade", true);
		pref.put("download.prompt_for_download", false);
		pref.put("safebrowsing.enabled", true);
		pref.put("profile.default_content_settings.popups", 0);
		pref.put("plugins.always_open_pdf_externally", true);
		options.setExperimentalOption("prefs", pref);
		//options.addArguments("--headless");
		driver = new ChromeDriver(options);
		//switches to new tab
		/*DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability("screen-resolution", "1280x1024");
		caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs.exe");
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--webdriver-loglevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		driver = new PhantomJSDriver(caps);*/
		driver.get(url);
		//Abrimos una nueva pestaña para la descarga de pdf
		((JavascriptExecutor)driver).executeScript("window.open()");
		ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
		driver.switchTo().window(tabs.get(0)); 
		driver.findElement(By.id("quickSearchName")).sendKeys(palabraBuscar);
		driver.findElement(By.xpath("//div[@id='querySubmit']//input[@type='submit']")).click();
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

	public boolean comprobarRecorrido(String link) {
		if(previousLinks.contains(link))
			return false;
		return true;
	}

	public boolean adescargarPdf(String urlPasada) {
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
		File directorioEnFile = new File(nombreDirectorio);
		int previousLenght = directorioEnFile.listFiles().length;
		try {
			System.out.println("Pasamos la URL " + urlPasada);
			driver.get(urlPasada);
		}catch(org.openqa.selenium.WebDriverException e) {
			System.out.println("Pues ha saltado");
			return false;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			Thread.interrupted();
		}
		driver.switchTo().window(tabs.get(0));
		//Se ha descargado un nuevo PDF
		if(directorioEnFile.listFiles().length > previousLenght) {
			return true;
		}
		return false;
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
			if(comprobarRecorrido(k)) {
				driver.get(k);
				JsonObject objson = new JsonObject();
				String nombreCarpeta;
				if(!allJoin)
					nombreCarpeta = crearCarpeta("Extraccion");
				else
					nombreCarpeta = nombreDirectorio;
				try {
					objson.addProperty("titulo", driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='3']")).getText());
				}catch(org.openqa.selenium.NoSuchElementException e) {}
				System.out.println("    Comenzamos la extraccion " + extraccionActual + driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='3']")).getText());
				try {
					objson.addProperty("abstractText", driver.findElement(By.cssSelector("td[class='otherfieldsbg'][colspan='5']")).getText());
				}catch(org.openqa.selenium.NoSuchElementException e) {}
				try {
					objson.addProperty("year", Integer.parseInt(driver.findElement(By.cssSelector("a[href^=\"show.php?year=\"]")).getText()));
				}catch(org.openqa.selenium.NoSuchElementException e) {}
				ArrayList<String> authors = new ArrayList<>();
				try {
					String[] resultadoAutor = driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='5']")).getText().split(";");
					authors = new ArrayList<>(Arrays.asList(resultadoAutor));
				}catch(org.openqa.selenium.NoSuchElementException e) {}
				JsonArray nuevaArr = new JsonArray();
				for (String str: authors) {
					nuevaArr.add(str);
				}
				objson.add("authors", nuevaArr);
				ArrayList<String> keyWords = new ArrayList<>();
				try {
					String[] keyWordsResultado = driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='5']")).getText().split(";");
					keyWords = new ArrayList<>(Arrays.asList(keyWordsResultado));
				}catch(org.openqa.selenium.NoSuchElementException e) {}
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
				if(v != null) {
					if(!adescargarPdf(v)) {
						driver.get(v);
						tryExtract(nombreCarpeta);
					}
				}
				extraccionActual++;
				previousLinks.add(k);
				try {
					Files.write(previousFile.toPath(), (k + "\n").getBytes(), StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println();
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
	public void accederAlMeta(String meta, String attribute) {

	}
	public void tryExtract(String nombreCarpeta) {
		try {
			System.out.println("Primer metodo");
			WebElement uuu = driver.findElement(By.xpath("//meta[@name='citation_pdf_url']"));
			adescargarPdf(uuu.getAttribute("content"));
			return;
		}catch( org.openqa.selenium.NoSuchElementException e) {
		}
		try {
			System.out.println("Segundo metodo");
			WebElement e = driver.findElement(By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'pdf')]"));		
			String url = e.getAttribute("href");
			if(url == null) {
				if(e.getTagName().equals("i"))
					url = e.findElement(By.xpath("..")).getAttribute("href");
				if(e.getTagName().equals("span")) {
					url = e.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("href");
				}
			}
			if(url != null) {
				adescargarPdf(url);
				return;
			}
		} catch( org.openqa.selenium.NoSuchElementException e) {
		}
		try {
			System.out.println("Tercer metodo");
			WebElement e = driver.findElement(By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'download')]"));		
			String url = e.getAttribute("href");
			if(url == null) {
				if(e.getTagName().equals("i"))
					url = e.findElement(By.xpath("..")).getAttribute("href");
				if(e.getTagName().equals("span")) {
					url = e.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("href");
				}
			}
			if(url != null) {
				adescargarPdf(url);
				return;
			}
		} catch ( org.openqa.selenium.NoSuchElementException e) {
		}
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

	private String crearCarpeta(String name) {
		String nuevaRuta = this.nombreDirectorio + "\\" + name + this.extraccionActual;
		int i = 1;
		String intentar = nuevaRuta;
		while(!(new File(intentar)).mkdirs()) {
			intentar = nuevaRuta + "(" + i + ")";
			i++;
		}
		return intentar + "\\";
	}
}
