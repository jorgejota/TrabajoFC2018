package com.tfg.Extraccion;

import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import technology.tabula.CommandLineApp;

public class ReadPDF {
	private PDDocument document;
	private PDPageTree myPageTree;
	private JsonObject objMain;
	private String myRuta;
	private File file;
	private ExtractionMode mode; 
	private List<Integer> miLista;
	private String textoPrincipalDelPDF;
	private boolean fixText;
	private File carpetaOut;
	private boolean onlyText;
	
	public ReadPDF(File file, ExtractionMode mode, List<Integer> miLista, boolean fixText, File carpetaOut) {
		this.file = file;
		this.mode = mode;
		this.miLista = miLista;
		this.fixText = fixText;
		this.carpetaOut = carpetaOut;
	}
	public ReadPDF(File file, ExtractionMode mode, List<Integer> miLista, boolean fixText, File carpetaOut, boolean onlyText) {
		this.file = file;
		this.mode = mode;
		this.miLista = miLista;
		this.fixText = fixText;
		this.carpetaOut = carpetaOut;
		this.onlyText = onlyText;
	}

	public String run() throws InvalidPasswordException, IOException, ParserConfigurationException {
		//Cargamos el PDF
		document = PDDocument.load(file);
		myPageTree = document.getPages();

		//Creaccion de la carpeta 
		if(carpetaOut == null) {
		myRuta = file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4);
		}else {
			myRuta = carpetaOut.getAbsolutePath() + "\\" + file.getName().substring(0,file.getName().length()-4);
		}
		if(onlyText) {
			extraerTexto();
			return textoPrincipalDelPDF;
		}
		myRuta = crearCarpeta();

		//Extraemos los metadatos
		extraerMetaDatos();

		//Extraer Imagenes
		extraerImagenes();

		switch (mode) {
		case BOOKMARK:
			//Extraccion de los BookMarks
			extraerBookMarks();
			break;
		case PAGES:
			//Lo dividimos en subpaginas
			//Splitter splitter = new Splitter();
			break;
		default:
			//Extraer Texto completo
			extraerTexto();
			break;
		}

		//Extraer tabla
		try {
			extraerTabla();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println(textoPrincipalDelPDF);
		imprimirTexto();
		return textoPrincipalDelPDF;
	}

	public void imprimirTexto() {
		try {
			Files.write(Paths.get(myRuta + "Texto" + ".txt"), textoPrincipalDelPDF.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public PdfBookMarks extraerHijos(PDOutlineItem item){
		PdfBookMarks aDevolver = new PdfBookMarks();
		aDevolver.setTitulo(item.getTitle());
		//Localizacion de texto
		try {
			PDPage miPage = item.findDestinationPage(document);
			System.out.println("Aqui empieza: " + item.getTitle());
			textoLocalizado(miPage);
			System.out.println("Aqui acaba: " + item.getTitle());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Localizacion de texto 
		PDOutlineItem child = item.getFirstChild();
		while(child != null){
			aDevolver.setHijo(extraerHijos(child));
			child = child.getNextSibling();
		}
		return aDevolver;
	}

	public String crearCarpeta() {
		String nuevaRuta = myRuta + "-resultados";
		int i = 1;
		String intentar = nuevaRuta;
		while(!(new File(intentar)).mkdirs()) {
			intentar = nuevaRuta + "(" + i + ")";
			i++;
		}
		return intentar + "\\";
	}

	//Para quitarte footer y header
	public void textoLocalizado(PDPage pagee) {
		Rectangle2D region = new Rectangle2D.Double(0f, 0f, 595f, 757.8f);
		System.out.println(pagee.toString());
		//Aplicar operacion para reducir entre un 5 y un 10%
		/*System.out.println(pagee.getMediaBox().getHeight());
		System.out.println(pagee.getMediaBox().getWidth());
		System.out.println(pagee.getMediaBox().getLowerLeftX());
		System.out.println(pagee.getMediaBox().getLowerLeftYs());*/
		String regionName = "region";
		PDFTextStripperByArea stripper;
		int paginaActual = myPageTree.indexOf(pagee) + 1;
		System.out.println("paginaActual " + paginaActual);
		try {
			stripper = new PDFTextStripperByArea();
			stripper.addRegion(regionName, region);
			stripper.extractRegions(pagee);
			System.out.println("Region is "+ stripper.getTextForRegion("region"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extraerMetaDatos() {
		objMain = new JsonObject();
		String comprobacion;
		PDDocumentInformation info = document.getDocumentInformation();
		objMain.addProperty("NumberOfPages", document.getNumberOfPages());
		comprobacion = info.getTitle()!=null?info.getTitle():"";
		objMain.addProperty("Tittle", comprobacion);
		comprobacion = info.getAuthor()!=null?info.getAuthor():"";
		objMain.addProperty("Author", comprobacion);
		comprobacion = info.getSubject()!=null?info.getSubject():"";
		objMain.addProperty("Subject", comprobacion);
		comprobacion = info.getKeywords()!=null?info.getKeywords():"";
		objMain.addProperty("Keywords", comprobacion);
		comprobacion = info.getCreator()!=null?info.getCreator():"";
		objMain.addProperty("Creator", comprobacion);
		comprobacion = info.getProducer()!=null?info.getProducer():"";
		objMain.addProperty("Producer", comprobacion);
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		comprobacion = info.getCreationDate()!=null?formato.format(info.getCreationDate().getTime()):"";
		objMain.addProperty("CreationDate",comprobacion);
		comprobacion = info.getModificationDate()!=null?formato.format(info.getModificationDate().getTime()):"";
		objMain.addProperty("ModificationDate", comprobacion);
		comprobacion = info.getTrapped()!=null?info.getTrapped():"";
		objMain.addProperty("Trapped", comprobacion);
		File jsonFile = new File(myRuta + "MetaDatos" + ".json");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
			writer.write(objMain.toString());
			writer.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extraerTexto() {
		PDFTextStripper pdfStripper;
		try {
			pdfStripper = new PDFTextStripper();
			String text = pdfStripper.getText(document);
			if(fixText) {
				Pattern pattern = Pattern.compile("(\\S+)\\s*-\\s*\\n(\\S+)",Pattern.MULTILINE);
				Matcher matcher = pattern.matcher(text);
				while(matcher.find()) {
					//https://stackoverflow.com/questions/5357460/python-regex-matching-a-parenthesis-within-parenthesis?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
					String firstS = matcher.group(1);
					String secondS = matcher.group(2);
					String sustitucion = firstS + secondS + "\n";
					firstS = fixearString(firstS);
					secondS = fixearString(secondS);
					text = text.replaceAll(firstS+"\\s*-\\s*\\n"+secondS+"\\s", sustitucion);
				}
			}
			textoPrincipalDelPDF = text;
		} catch (IOException |  java.util.regex.PatternSyntaxException e) {
			e.printStackTrace();
			textoPrincipalDelPDF = "";
			return;
		} 
	}

	public static String fixearString(String aRempladar) {
		String xx = aRempladar;
		xx = xx.replace("(", "\\(").replace("[", "\\[").replace("{", "\\{").replace(".", "\\.").replace(":", "\\:").
				replace(")", "\\)").replace("]", "\\]").replace("}", "\\}").replace(",", "\\,")	.replace(";", "\\;");
		return xx;
	}

	public void extraerImagenes() {
		PDPageTree list = document.getPages();
		new File(myRuta + "Images").mkdirs();
		int i = 1;
		for (PDPage page : list) {
			PDResources pdResources = page.getResources();
			for (COSName c : pdResources.getXObjectNames()) {
				try {
					PDXObject o = pdResources.getXObject(c);
					//					COSStream stream = o.getCOSObject();
					if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
						//						PDFormXObject pdxObjectForm = new PDFormXObject(stream);
						//						System.out.println("Nos interesa" + pdxObjectForm.getBBox().getLowerLeftY());
						//					    System.out.println("Nos da igual" + pdxObjectForm.getBBox().getUpperRightY());
						File fileee = new File(myRuta + "Images\\" + "Imagen" + i + ".png");
						i++;
						ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject)o).getImage(), "png", fileee);
					}
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void extraerTabla() throws IOException, ParseException, org.json.simple.parser.ParseException{
		File writeJson = new File(myRuta + "InfoTablas.json");
		//-l lattice // -t stream
		try (ExitCodeCaptor exitCodeCaptor = new ExitCodeCaptor()) {
			exitCodeCaptor.run(() -> {
				CommandLineApp.main(new String[]{"--pages", "all", "-f", "JSON", "-l", "-g", "-o",
						writeJson.getAbsolutePath(), file.getAbsolutePath()});
			});
			if (exitCodeCaptor.getStatus() != 0)
				throw new IOException("Failed PDF convert with error code " + exitCodeCaptor.getStatus());
		}
		String aEscribir = myRuta + "tablas";
		new File(myRuta + "tablas").mkdirs();
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(new FileReader(writeJson));
		if(fixText) {
			CSVWriter csvWriter;
			int i = 0;
			for (Object obj1 : a){
				String rutaDelCSV = aEscribir + "\\Tabla_" + i + ".csv";
				File esoEsribir = new File(rutaDelCSV);
				csvWriter = new CSVWriter(new FileWriter(esoEsribir));
				JSONObject objetoAuxiliar = (JSONObject) obj1;
				JSONArray dataArray = (JSONArray) objetoAuxiliar.get("data");
				int filas = 0;
				int columnas = 0;
				for (Object obj2 : dataArray){
					filas++;
					ArrayList<String> aMeter = new ArrayList<>();
					for (Object obj3 : (JSONArray) obj2){
						JSONObject resultadoFinal = (JSONObject) obj3;
						String miResultado = (String) resultadoFinal.get("text");
						aMeter.add(miResultado);
					}
					csvWriter.writeNext(aMeter.toArray(new String[aMeter.size()]));
				}
				csvWriter.close();
				if((filas == 0 || filas == 1) || (columnas == 0 || columnas == 1)) {
					File aBorrar = new File(rutaDelCSV);
					aBorrar.delete();
				}
				else {
					try {
						sacarDelTexto(aEscribir, rutaDelCSV);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				i++;
			}
		}
	}

	public void sacarDelTexto(String aEscribir, String rutaCSV) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(rutaCSV));
		String[] nextLine;
		String myFinalString = ""; 
		while ((nextLine = reader.readNext()) != null) {
			List<String[]> auxiliarString = new ArrayList<>();
			for (String string : nextLine) {
				auxiliarString.add(string.split("\n"));
			}
			for (int j = 0; j < nextLine[0].split("\n").length; j++) {
				for (String[] strings : auxiliarString) {
					try {
						String stringDeMeter = fixearString(strings[j]);
						myFinalString += stringDeMeter + "\\s*";
					}catch(java.lang.ArrayIndexOutOfBoundsException e) {
						continue;
					}

				}
			}
		}
		reader.close();
		//Comprobacion de que un String sea vacio
		if(myFinalString.contains("[\\\\s*]+")) {
			File aBorrar = new File(rutaCSV);
			aBorrar.delete();
			return;
		}
		String aIntentar = myFinalString + "(Table .*?)\\n";
		System.out.println(aIntentar);
		Pattern asdada = Pattern.compile(aIntentar,Pattern.DOTALL);
		Matcher mimaccher = asdada.matcher(textoPrincipalDelPDF); 
		if(mimaccher.find()) {
			String miTexto = mimaccher.group(1);
			textoPrincipalDelPDF = textoPrincipalDelPDF.replaceAll(miTexto, "");
			miTexto = miTexto.replaceAll("\r", "");
			File aBorrar = new File(rutaCSV);
			File fileRemplace2 = new File(aEscribir + "\\" + miTexto + ".csv");
			if (aBorrar.renameTo(fileRemplace2))
				aBorrar.delete();
			else
				System.out.println("No se ha podido remplazar el nombre " + rutaCSV + " por " + fileRemplace2);
		}
		textoPrincipalDelPDF = textoPrincipalDelPDF .replaceAll(myFinalString, "");
	}

	public void extraerBookMarks() {
		ArrayList<PdfBookMarks> myBookMarks;
		myBookMarks = new ArrayList<PdfBookMarks>();
		PDDocumentOutline root =  document.getDocumentCatalog().getDocumentOutline();
		try {
			PDOutlineItem item = root.getFirstChild();
			while( item != null ){
				myBookMarks.add(extraerHijos(item));
				item = item.getNextSibling();
			}
		}
		catch(java.lang.NullPointerException e) {
			System.out.println("No se han encontrado marcadores/bookmarks");
			//System.out.println("Procediendo a busqueda manual");
			//Aqui lo puedes hacer por 1/I/...
			//Mira esto a ver si lo entiendes luego: https://stackoverflow.com/questions/44982486/how-to-select-pdf-page-using-bookmark-in-pdf-box
		}
	}
}
