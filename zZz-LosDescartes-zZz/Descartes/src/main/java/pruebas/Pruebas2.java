package pruebas;
//package demo;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.firefox.FirefoxDriver;
//
//import alandb.ModelToSave;
//
//public class Pruebas2 {
//	public static Integer numero = 0;
//	public static void main(String[] args) {
//		List<ModelToSave> nuevas = new ArrayList<ModelToSave>();
//		String url = "https://www.google.es/";
//		//WebDriver driver = new HtmlUnitDriver();
//		WebDriver driver = new FirefoxDriver();
//		driver.get(url);
//		try {
//			List<WebElement> nuevo = driver.findElements(By.xpath("//a[contains(@href, 'pdf')]"));
//			for (WebElement webElement : nuevo) {
//				String uri = webElement.getAttribute("href");
//				System.out.println("Hemos obtenido el link: " + uri);
//				hacerPeticion(uri);
//			}	
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//	public static boolean esPdf(String urlString) {
//		if(urlString == null || urlString.isEmpty() ||
//				!urlString.substring(urlString.length()-3).equals("pdf")) {
//			System.out.println("MAAAAAAAAAAAAAAAAAAAAL COMO NO ES PDF");
//			return false;
//		}
//		return true;
//	}
//	public static boolean hacerPeticion(String urlString) {
//		if(!esPdf(urlString))
//			return false;
//		URL url;
//		try {
//			url = new URL(urlString);
//			InputStream in = url.openStream();
//			Files.copy(in, Paths.get("DownloadPdf/Prueba" + numero + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
//			numero++;
//			in.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//		return true;
//	}
//
//}
