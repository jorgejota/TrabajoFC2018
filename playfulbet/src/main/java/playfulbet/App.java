package playfulbet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xpath.compiler.Keywords;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import net.bytebuddy.dynamic.scaffold.InstrumentedType.Frozen;

public class App {
	public static Integer numero = 0;
	public static List<Integer> aDesargar = new ArrayList<Integer>();
	private static Integer coins = 0;
	private static String raizPrincipal = "https://playfulbet.com";

	public static void main(String[] args) {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		String url = "https://playfulbet.com/users/sign_in?locale=es";
		//WebDriver driver = new HtmlUnitDriver();
		WebDriver driver = new FirefoxDriver();
		driver.get(url);
		driver.findElement(By.xpath("//input[@id='user_login']")).sendKeys("jorgejotahoyo@gmail.com");
		driver.findElement(By.xpath("//input[@id='user_password']")).sendKeys("caracol550");
		dormirse();
		driver.findElement(By.xpath("//button[@class='btn btn--full btn--big btn--submit push-bit']")).click();
		try {
			driver.findElement(By.xpath("//button[@class='btn btn--succes btn--huge gtm-event-submit']")).click();
		}
		catch (org.openqa.selenium.ElementNotVisibleException e){}
		catch (org.openqa.selenium.NoSuchElementException e) {}
		try {
			driver.findElement(By.xpath("//a[@class='link-outline'][@rel='nofollow']")).click();
		}
		catch (org.openqa.selenium.ElementNotVisibleException e){}
		catch (org.openqa.selenium.NoSuchElementException e) {}
		String coinsAuxiliares = driver.findElement(By.xpath("//b[@class='active-coins lnum']")).getText();
		coins = conversor(coinsAuxiliares);
		if(coins == 0)
			System.exit(0);
		driver.findElement(By.xpath("//a[contains(@class,'im-sidebar-events')]")).click();
		//while(true) {
		List<String> uris = newURI(driver);
		for (String string : uris) {
			System.out.println(string);
		}
		url = driver.getCurrentUrl();
		extract(uris, driver,coins);
		/*driver.get(url);
			avanzar(driver);*/
		//}
	}

	public static Integer conversor(String token) {
		String[] nuevo = token.split("\\.");
		String coinsActuales = "";
		for(String n: nuevo) {
			coinsActuales += n;
		}
		return Integer.parseInt(coinsActuales);
	}

	public static List<String> newURI(WebDriver driver) {
		driver.get("https://playfulbet.com/eventos");
		dormirse();
		String miiii = driver.getPageSource();
		Document d = Jsoup.parse(miiii);
		Elements elem = d.select("div.event-content > a");
		List<String> uris = new ArrayList<String>();
		for(Element eeee: elem) {
			uris.add(eeee.attr("href"));
		}	
		return uris;
	}

	public static void extract(List<String> uris, WebDriver driver,Integer coinsss){
		double[] cuotas = new double[3];
		for(String s: uris) {
			dormirse();
			driver.get(raizPrincipal + s);
			Document d = Jsoup.parse("miiii");
			Elements elem = d.select("span.right");
			List<WebElement> extraer;
			try {
				extraer = driver.findElements(By.xpath("div[@class='bet-option']//span[@class='right']"));
			}
			catch (org.openqa.selenium.ElementNotVisibleException e){}
			catch (org.openqa.selenium.NoSuchElementException e) {}
			try {
				driver.findElement(By.xpath("//button[@class='btn btn--succes btn--huge gtm-event-submit']")).click();
			}
			catch (org.openqa.selenium.ElementNotVisibleException e){}
			catch (org.openqa.selenium.NoSuchElementException e) {}
			try {
				driver.findElement(By.xpath("//button[@class='btn btn--succes btn--huge gtm-event-submit']")).click();
			}
			catch (org.openqa.selenium.ElementNotVisibleException e){}
			catch (org.openqa.selenium.NoSuchElementException e) {}
			
			List<WebElement> botones = driver.findElements(By.xpath("div[@class='bet-option']"));
			if(botones.size() == 0) {
				System.out.println("Es 0");
				System.exit(0);
			}
			System.out.println(driver.getPageSource());
			int i = 0;
			for(WebElement e: extraer) {
				cuotas[i] = Double.parseDouble(e.getText());
			}
			for (int j = 0; j < i; j++) {
				if(cuotas[j] < 1.36) {
					dormirseLento();
					botones.get(j).click();
					dormirseLento();
					driver.findElement(By.xpath("a[contains(@class=,'gtm-event-allin')]")).click();
					dormirseLento();
					System.out.println("VAMONOS");
					//driver.findElement(By.xpath("button[@id='play-action'][@data-disable-with='Jugando...']")).click();
					acabarEjecucion(driver);
				}
			}
		}
	}
	public static void acabarEjecucion(WebDriver driver) {
		driver.findElement(By.xpath("//a[contains(@class,'js-logout')]")).click();
		System.exit(0);
	}

	/*public static void avanzar(WebDriver driver){
		try {
			driver.findElement(By.xpath("//a[contains(@title,'display next results page')]")).click();
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			System.out.println("Fin de las busquedas");
			System.exit(0);
		}
	}*/

	public static void dormirse() {
		//Para evitar ser baneado
		int min = 2000; int max = 7000;
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		try {
			Thread.sleep(randomNum);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void dormirseLento() {
		//Para evitar ser baneado
		int min = 1500; int max = 3500;
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		try {
			Thread.sleep(randomNum);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
