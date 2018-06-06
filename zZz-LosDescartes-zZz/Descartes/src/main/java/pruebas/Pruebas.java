package pruebas;
//package demo;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.firefox.FirefoxDriver;
//
//import alandb.ModelToSave;
//
//public class Pruebas {
//	public static Integer numero = 12;
//	
//	/*
//	 * Me voy a dormir que tengo sueño
//	 * El caso es que si doy a un boton y me abre una ventana nueva cambio la Uri para que pueda descargarlo desde esa misma
//	 * 
//	 * Hay un caso que todavia me queda (pero es bastante complicado) que es el descargar desde una nueva ventana
//	 * y que dentro de esa ventana haya un boton de descargar (lo tengo en el caso de prueba).
//	 * 
//	 * Queda no que lo contenga la clase sino el 'tittle' (crear un metodo a parte que fusione los dos) y ver si es factible
//	 * hacer lo dicho arriba
//	 * 
//	 * Buenas noches
//	 */
//	public static void main(String[] args) {
//		List<ModelToSave> nuevas = new ArrayList<ModelToSave>();
//		String url = "https://www.google.es/";
//		//WebDriver driver = new HtmlUnitDriver();
//		WebDriver driver = new FirefoxDriver();
//		driver.get(url);
//		try {
//		List<WebElement> bueno = driver.findElements(By.xpath("//a[contains(@class, 'pdf')]"));
//		boolean descargado = false;
//		for (WebElement webElement : bueno) {
//			webElement.click();
//			//Para dejar que cargue la pagina
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			Set<String> winHandleBefore = driver.getWindowHandles();
//			String[] arrayString = winHandleBefore.toArray(new String[winHandleBefore.size()]);
//	        driver.switchTo().window(arrayString[arrayString.length-1]);
//			String urii = driver.getCurrentUrl();
//			descargado = descargar(urii);
//			if(arrayString.length > 1) {
//				driver.close();
//				driver.switchTo().window(arrayString[0]);
//			}
//			if(descargado)
//				break;
//		}
//		System.out.println("Terminado " + descargado);
//		}
//		catch (org.openqa.selenium.NoSuchElementException e) {
//			System.out.println("No existe");
//		}
//	}
//	
//	private static boolean descargar(String urlString)   {
//		URL url;
//		try {
//			System.out.println(urlString);
//			url = new URL(urlString);
//			InputStream in = url.openStream();
//			Files.copy(in, Paths.get("DownloadPdf/Archivo" + numero + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
//			numero++;
//			in.close();
//		} catch (IOException e) {
//			return false;
//		}
//		return true;
//	}
//}
