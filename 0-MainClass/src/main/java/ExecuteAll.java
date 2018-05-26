import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import com.tfg.Extraccion.ExtractionMode;
import com.tfg.Extraccion.ReadPDF;
import com.tfg.librairy.LibrairyConnection;

import zenododb.ApplicationZenodo;

public class ExecuteAll {

	private static String stopWords;

	public static void main(String[] args) {
		String source = "ALL";
		String keyWord = "light pollution";
		String carpeta = "C:\\Users\\jorge\\Desktop\\LIGHT30";
		long inicio = System.nanoTime();
		long total = System.nanoTime();
		//new Application(carpeta,"", true).run();
		System.out.println("EXTRACCION DE DARKSKY TARDA: " + (System.nanoTime() - inicio));
		inicio = System.nanoTime();
		new ApplicationZenodo(carpeta,keyWord,true).run();
		System.exit(0);
		System.out.println("EXTRACCION DE ZENODO TARDA: " + (System.nanoTime() - inicio));
		List<String> listaTexto = new ArrayList<String>();
		List<File> misPDF = comprobacionArchivo(carpeta);
		List<Integer> numeros = new ArrayList<Integer>();
		File myCarpetaEnFila = new File(carpeta);
		boolean fixText = false;
		inicio = System.nanoTime();
		stopWords = leoDeArchivo();
		int j = 0;
		for (File file : misPDF) {
			if(j == 800)
				break;
			try {
				String textoPDF = new ReadPDF(file,ExtractionMode.COMPLETE,numeros,fixText,myCarpetaEnFila,true).run();
				if(textoIngles(textoPDF, file.getAbsolutePath())) {
					if(j>400)
						listaTexto.add(textoPDF);
					j++;
				}
			} catch (IOException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		System.out.println("EXTRACCION DE "+ misPDF.size() + " PDFs TARDA: " + (System.nanoTime() - inicio));
		inicio = System.nanoTime(); 
		LibrairyConnection paraUsar = new LibrairyConnection(listaTexto,stopWords,"lightPollution");
		paraUsar.sacarTopicos();
		System.out.println("Press 1 to continue, another number to stop ...");
		Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		sc.close();
		if(i!=1)
			System.exit(0);
		paraUsar.entrenarModelo();
		LibrairyConnection paraComprobar = new LibrairyConnection(listaTexto,"lightPollution");
		paraComprobar.comprobarArchivo();
		System.out.println("CONEXION CON LIBRAIRY TARDA : " + (System.nanoTime() - inicio));
		System.out.println("TIEMPO DE EJECUCION TOTAL: " + (System.nanoTime() - total));
	}

	private static boolean textoIngles(String myTexto, String myPath) {
		//load all languages:
		List<LanguageProfile> languageProfiles;
		try {
			languageProfiles = new LanguageProfileReader().readAllBuiltIn();
			//build language detector:
			LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
					.withProfiles(languageProfiles)
					.build();
			//create a text object factory
			TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
			//query:
			TextObject textObject = textObjectFactory.forText(myTexto);
			Optional<LdLocale> lang = languageDetector.detect(textObject);
			String resultado = lang.toString();
			String elverdad = resultado.substring(resultado.length()-3, resultado.length()-1);
			if(elverdad.equals("en"))
				return true;
			System.out.println("Para " + myPath + " : "+ resultado);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static String leoDeArchivo() {
		String caracteresFixear = "";
		File file = new File("src/main/resources/commonWords");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				caracteresFixear += line + " ";
			}
		}catch(FileNotFoundException e1) {
			e1.printStackTrace();
		}catch(IOException e2) {
			e2.printStackTrace();
		}
		return caracteresFixear;
	}

	private static List<File> comprobacionArchivo(String archivo) {
		File pdfOrFolder = null;
		try {
			pdfOrFolder = new File(archivo);
			if(!pdfOrFolder.exists()) {
				System.out.println("The input file or folder doesn´t exist.");
				System.exit(1);
			}
		}catch(java.lang.NullPointerException e) {
			System.out.println("The input file or folder doesn´t exist.");
			System.exit(1);
		}
		List<File> aDevolver = new ArrayList<>();
		if(pdfOrFolder.isDirectory()) {
			for (File isAPDF : pdfOrFolder.listFiles()) {
				String nombreArchivo = isAPDF.getName();
				try {
					String extension = isAPDF.getName().substring(nombreArchivo.length()-4, nombreArchivo.length());
					if(extension.equalsIgnoreCase(".pdf")){
						aDevolver.add(isAPDF); 
					}
				}catch(java.lang.StringIndexOutOfBoundsException e) {
					//Para evitar archivos con longitud menor a tres caracteres
				}
			}
			if(aDevolver.isEmpty()) {
				System.out.println("The folder doesn´t contains a PDF.");
				System.exit(1);
			}
			return aDevolver; //Es un directorio
		}
		else {
			String nombreArchivo = pdfOrFolder.getName();
			try {
				String extension = nombreArchivo.substring(nombreArchivo.length()-4, nombreArchivo.length());
				if(extension.equals(".pdf")){
					aDevolver.add(pdfOrFolder);
					return aDevolver;
				}
			}catch(java.lang.StringIndexOutOfBoundsException e) {
				//Para evitar archivos con longitud menor a tres caracteres
			}
			aDevolver.add(pdfOrFolder);
			return aDevolver;
		}
	}

}
