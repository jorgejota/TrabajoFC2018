package playfulbet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Prueba {
	public static void main(String[] args) throws IOException {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		String url = "https://playfulbet.com/users/sign_in?locale=es";
		WebDriver driver = new HtmlUnitDriver();
		double[] cuotas = new double[3];
		driver.get("https://playfulbet.com/eventos/fc-bayern-basketball-alba-berlin-272096");
		List<WebElement> extraer = driver.findElements(By.xpath("div[@class='bet-option']//span[@class='right']"));
		List<WebElement> botones = driver.findElements(By.xpath("a[@class='js-bet-option gtm-event-option-form']"));
		String miiii = driver.getPageSource();
		System.out.println(miiii);
		Document d = Jsoup.parse(miiii);
		Elements elem = d.select("span.right");
		List<String> uris = new ArrayList<String>();
		for(Element eeee: elem) {
			uris.add(eeee.text());
			System.out.println(eeee.text());
		}	
		int i = 0;
		for(WebElement e: extraer) {
			cuotas[i] = Double.parseDouble(e.getText());
		}
	}
}
