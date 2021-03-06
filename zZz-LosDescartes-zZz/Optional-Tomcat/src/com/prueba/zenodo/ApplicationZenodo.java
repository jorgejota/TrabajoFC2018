package com.prueba.zenodo;

import java.io.IOException;
import java.io.InputStream;
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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ApplicationZenodo {
	public static Integer numeroPagina = 1;
	public static Integer aRecorrer;
	//public static void main(String[] args) {
	public ApplicationZenodo() {

	}
	public static String miiPrueba() {
		String url = "https://zenodo.org/search?page=1&size=5";
		WebDriver driver = new HtmlUnitDriver();
		//WebDriver driver = new FirefoxDriver();
		String[] type = {"pdf"};
		url = chooseTopic(url,type);
		String palabraBuscar = "ordenador";
		url = chooseSearch(url, palabraBuscar);
		driver.get(url);
		String resultados = driver.findElement(By.xpath("//ng-pluralize[@count='vm.invenioSearchResults.hits.total']")).getText();
		return driver.getCurrentUrl();
		/*if(resultados.equals("No results.")) {
			System.out.println("No se han encontrado resultados");
			System.exit(0);
		}
		String[] auxiliar = resultados.split(" ");
		Integer.parseInt(auxiliar[1]);
		while(true) {
			List<String> uris = newURI(driver);
			url = driver.getCurrentUrl();
			//extract(uris, driver);
			driver.get(url);
			avanzar(driver);
		}*/
	}
	public static List<String> newURI(WebDriver driver) {
		List<WebElement> elementos = driver.findElements(By.cssSelector("a[class='ng-binding'][href^=\"/record\"]"));
		List<String> uris = new ArrayList<String>();
		for(WebElement e: elementos) {
			uris.add(e.getAttribute("href"));
		}
		return uris;
	}
	public static void extract(List<String> uris, WebDriver driver){
		for (String string : uris) {
			System.out.println(string);
		}
		System.out.println();System.out.println();
		for(String s: uris) {
			dormirse();
			System.out.println(s);
			driver.get(s);
			ZenodoToSave meter = new ZenodoToSave();
			meter.titulo = driver.findElement(By.xpath("//div[@class='col-sm-8 col-md-8 col-left']//h1")).getText();
			meter.date = driver.findElement(By.xpath("//div[@class='well metadata']")).getText();
			List<WebElement> keyW = driver.findElements(By.xpath("//div[@class='col-sm-8 col-md-8 col-left']//p[not(*)]"));
			for(WebElement k: keyW) {
				meter.abstractText += k.getText();
			}
			System.out.println("Abstract:\n" + meter.abstractText);
			//meter.abstractText = driver.findElement(By.xpath("//p[@*]"));
			keyW = driver.findElements(By.xpath("//div[@class='col-sm-8 col-md-8 col-left']//p//span[@class='text-muted']"));
			System.out.println("Autores");
			for(WebElement k: keyW) {
				meter.authors.add(k.getText());
			}
			for(String sss: meter.authors) {
				System.out.println(sss);
			}
			System.out.println("Keywords");
			keyW = driver.findElements(By.xpath("//a[contains(@href,'/search?q=keywords')]//span"));
			for(WebElement k: keyW) {
				meter.keywords.add(k.getText());
			}
			for(String sss: meter.keywords) {
				System.out.println(sss);
			}
			/*
			 * meter.abstractText = driver.findElement(By.cssSelector("td[class='otherfieldsbg'][colspan='5']")).getText();
			 * meter.author = driver.findElement(By.cssSelector("td[class='mainfieldsbg'][colspan='5']")).getText().split(";");
			List<WebElement> keyW = driver.findElements(By.xpath("//a[@class='label-link']//span"));
			for(WebElement k: keyW) {
				meter.keywords.add(k.getText());
			}
			for(String k: meter.keywords){
				System.out.println(k);
			}
			System.out.println();System.out.println();System.out.println();*/
		}
	}
	public static void avanzar(WebDriver driver){
		try {
			WebElement nuevo = driver.findElement(By.xpath("//li[@class='disabled']//a"));
			System.out.println(nuevo);
			System.out.println("Fin de las busquedas");
			System.exit(0);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			driver.findElement(By.xpath("//a[@alt='Go to page 2']")).click();
		}
	}
	public static String chooseTopic(String myUrl,String[] type){
        for(String topic:type)
            myUrl += "&file_type=" + topic;
        return myUrl;
	}
	public static String chooseSearch(String myUrl,String selection){
	     if(selection!=null && !selection.isEmpty())
	        myUrl += "&q="+selection;
	     return myUrl;
	}
	public static void dormirse() {
		//Para evitar ser baneado
		int min = 2000; int max = 3500;
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		try {
			Thread.sleep(randomNum);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static String twoString(String a, String b) {
		if(a!=null && b!=null)
			return a+b;
		return null;
	}
}
