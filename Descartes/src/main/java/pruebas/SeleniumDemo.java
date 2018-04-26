package pruebas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class SeleniumDemo {
	public static void main(String[] args) throws InterruptedException {
		WebDriver htmlunitdriver = new HtmlUnitDriver();
		htmlunitdriver.get("https://www.google.es/");
		System.out.println(htmlunitdriver.getTitle());
	}
	
	public static void mainThread(){
		
	}
	
}
