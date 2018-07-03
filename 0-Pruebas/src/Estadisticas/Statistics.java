//package Estadisticas;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.SortedSet;
//import java.util.TreeSet;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//public class Statistics {
//	public static HashMap<String, Integer> dominio;
//	public static HashMap<String, String> dominioEjemplo;
//	public static WebDriver driver;
//
//	
//	public static void main(String[] args) {
//		dominio = new HashMap<String, Integer>();
//		dominioEjemplo = new HashMap<String, String>();
//		dominio.put("No existe dominio", 0);
//		dominioEjemplo.put("No existe dominio", "*******");
//		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
//		String url = "http://alandb.darksky.org/";
//	    driver = new ChromeDriver();
//		driver.get(url);
//		driver.findElement(By.xpath("//div[@id='querySubmit']//input[@type='submit']")).click();
//		while(true) {
//			waitJavaScript();
//			HashMap<String,String> uris = newURI(driver);
//			url = driver.getCurrentUrl();
//			extract(uris, driver);
//			driver.get(url);
//			avanzar(driver);
//		}
//	}
//	public static HashMap<String,String> newURI(WebDriver driver) {
//		List<WebElement> elementos = driver.findElements(By.cssSelector("a[href^=\"show.php?record=\"]"));
//		HashMap<String,String> uris = new HashMap<String, String>();
//		for(WebElement e: elementos) {
//			String url;
//			try {
//			url = e.findElement(By.xpath("following-sibling::*")).getAttribute("href");
//			}catch (org.openqa.selenium.NoSuchElementException exp) {
//				url = null;
//			}
//			uris.put(e.getAttribute("href"),url);
//		}
//		return uris;
//	}
//	public static void extract(HashMap<String,String> uris, WebDriver driver){
//		waitJavaScript();
//		for(Map.Entry<String, String> entry : uris.entrySet()) {
//		    String urlDB = entry.getKey();
//		    String externalURL = entry.getValue();
//		    if(externalURL != null) {
//		    	driver.get(externalURL);
//		    	tryExtract(driver);
//		    }
//		}
//	}
//	public static void avanzar(WebDriver driver){
//		try {
//			driver.findElement(By.xpath("//a[contains(@title,'display next results page')]")).click();
//		}
//		catch (org.openqa.selenium.NoSuchElementException e) {
//			System.out.println("Fin de las busquedas");
//			mostrarLista();
//		}
//	}
//	public static void tryExtract(WebDriver driver) {
//		driver.getCurrentUrl();
//	    Pattern pattern = Pattern.compile("http[s]?:\\/\\/(.*?)\\/");
//	    String aCompilar = driver.getCurrentUrl();
//	    Matcher matcher = pattern.matcher(aCompilar);
//	    if(matcher.find()) {
//	    	String myDominio = matcher.group(1);
//	    	if(dominio.get(myDominio) == null) {
//	    		dominio.put(myDominio, 1);
//	    		dominioEjemplo.put(myDominio, aCompilar);
//	    	}
//	    	else
//	    		dominio.replace(myDominio, dominio.get(myDominio)+1);
//	    }
//	    else {
//	    	System.out.println("Algo ha pasado: " + aCompilar);
//	    	dominio.replace("No existe dominio", dominio.get("No existe dominio")+ 1);
//	    	dominioEjemplo.replace("No existe dominio", aCompilar);
//	    }
//	}
//
//	public static boolean waitJavaScript() {
//	    WebDriverWait wait = new WebDriverWait(driver, 10);
//	    final JavascriptExecutor js = (JavascriptExecutor)driver;
//	    ExpectedCondition<Boolean> jQuery = new ExpectedCondition<Boolean>() {
//	      public Boolean apply(WebDriver driver) {
//	        try {
//	          return ((Long)js.executeScript("return jQuery.active") == 0);
//	        }
//	        catch (Exception e) {
//	          return true;
//	        }
//	      }
//	    };
//	    ExpectedCondition<Boolean> javaScript = new ExpectedCondition<Boolean>() {
//	      public Boolean apply(WebDriver driver) {
//	        return js.executeScript("return document.readyState")
//	            .toString().equals("complete");
//	      }
//	    };
//	  return wait.until(jQuery) && wait.until(javaScript);
//	}
//	
//	public static void mostrarLista() {
//		try {
//		FileWriter fw = new FileWriter("C:\\Users\\jorge\\Desktop\\TrabajoFC\\ResultadosALANGlobal.txt"); 
//		SortedSet<String> keys = new TreeSet<String>(dominio.keySet());
//		fw.write("-----------------------------------" + "\n");
//		for (String string : keys) {
//			fw.write("Dominio: " + string + "\n");
//			fw.write("Encontrados: " + dominio.get(string) + "\n");
//			fw.write("Ejemplo: " + dominioEjemplo.get(string) + "\n");
//			fw.write("-----------------------------------" + "\n");
//		}
//		fw.close();
//		System.exit(0);
//		}catch (IOException e) {
//			System.out.println("Hay un error");
//		}
//	}
//	
//	public static void ordenarLista() {
//		dominio = new HashMap<String, Integer>();
//		dominioEjemplo = new HashMap<String, String>();
//	    Pattern pattern = Pattern.compile("Dominio: (.*)\nEncontrados: (.*)\nEjemplo: (.*)",Pattern.MULTILINE);
//	    String content = "";
//		try {
//			content = new String(Files.readAllBytes(Paths.get("C:\\Users\\jorge\\Desktop\\TrabajoFC\\ResultadosALANGlobal.txt")));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	    Matcher matcher = pattern.matcher(content);
//	    while(matcher.find()) {
//	    	dominio.put(matcher.group(1), Integer.parseInt(matcher.group(2)));
//	    	dominioEjemplo.put(matcher.group(1),matcher.group(3));
//	    }
//	    Object[] a = dominio.entrySet().toArray();
//	    Arrays.sort(a, new Comparator() {
//	        public int compare(Object o1, Object o2) {
//	            return ((Map.Entry<String, Integer>) o2).getValue()
//	                       .compareTo(((Map.Entry<String, Integer>) o1).getValue());
//	        }
//	    });
//	    Integer array[] = new Integer[16];
//	    for (int i = 0; i < array.length; i++) {
//			array[i] = 0;
//		}
//	    int i = 0;
//	    for (Object e : a) {
//	    	if(((Map.Entry<String, Integer>) e).getValue()<16)
//	    		i++;
//	    		//array[((Map.Entry<String, Integer>) e).getValue()] += 1;
//	    	else
//	        System.out.println(((Map.Entry<String, Integer>) e).getKey());// + " con "
//	                //+ ((Map.Entry<String, Integer>) e).getValue() + " articulos.");// + dominioEjemplo.get(((Map.Entry<String, Integer>) e).getKey()));
//	    }
////	    for (int i = 0; i < array.length; i++) {
////			System.out.println("Con longitud " + i + " : " + array[i]);
////		}
//	}
//}
