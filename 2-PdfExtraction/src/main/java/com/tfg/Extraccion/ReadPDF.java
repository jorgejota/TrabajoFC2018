package com.tfg.Extraccion;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.Splitter;
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

public class ReadPDF {

	private static PDDocument document;
	private static PDPageTree myPageTree;
	public static void main(String args[]) throws InvalidPasswordException, IOException {
		PdfMetaData myMetaData;
		ArrayList<PdfBookMarks> myBookMarks;
		String myRuta;
		String fileName;
		
		//Loading an existing document
		myRuta = "C:\\Users\\jorge\\Desktop\\TrabajoFC\\";
		File file = new File(myRuta + "CasoDePreuba\\CasoDePrueba2.pdf");
		fileName = file.getName().substring(0, file.getName().length()-4);
		myRuta = crearCarpeta(myRuta, fileName);
		File destination = new File(myRuta + fileName + ".pdf");
		Files.copy(file.toPath(), destination.toPath());
		document = PDDocument.load(file);
		myPageTree = document.getPages();
		myMetaData = new PdfMetaData();
		myBookMarks = new ArrayList<PdfBookMarks>();
		//Loading an existing document
		
		
		//Creditos: https://stackoverflow.com/questions/26143942/extract-footer-data-of-pdf-in-java
		PDPage pagee = (PDPage)document.getDocumentCatalog().getPages().get( 12 );
		textoLocalizado(pagee);

		//Extraemos los metadatos
		PDDocumentInformation info = document.getDocumentInformation();
		myMetaData.numberOfPages=document.getNumberOfPages(); 
		myMetaData.title=info.getTitle(); 
		myMetaData.author=info.getAuthor(); 
		myMetaData.subject=info.getSubject();
		myMetaData.keywords=info.getKeywords(); 
		myMetaData.creator=info.getCreator(); 
		myMetaData.producer=info.getProducer(); 
		myMetaData.creationDate=info.getCreationDate();
		myMetaData.modificationDate=info.getModificationDate();
		myMetaData.trapped=info.getTrapped();
		//Extraemos los metadatos


		//--Extract BookMarks
		System.out.println("BookMark straction");
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
			System.out.println("Procediendo a busqueda manual");
			//Aqui lo puedes hacer por 1/I/...
			//Mira esto a ver si lo entiendes luego: https://stackoverflow.com/questions/44982486/how-to-select-pdf-page-using-bookmark-in-pdf-box
		}
		//--Extract BookMarks


		//--Dividiendo documento por paginas. ¿Por otra cosa mas?
		Splitter splitter = new Splitter();
		//--Dividiendo documento por paginas. ¿Por otra cosa mas?


		//--ExtractText
		PDFTextStripper pdfStripper = new PDFTextStripper();
		String text = pdfStripper.getText(document);
		Files.write(Paths.get(myRuta + "Texto.txt"), text.getBytes());
		//--ExtractText


		//--ImageExtract
		PDPageTree list = document.getPages();
		new File(myRuta + "Images").mkdirs();
		int i = 1;
		for (PDPage page : list) {
			PDResources pdResources = page.getResources();
			for (COSName c : pdResources.getXObjectNames()) {
				PDXObject o = pdResources.getXObject(c);
				if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
					File fileee = new File(myRuta + "Images\\" + "Imagen" + i + ".png");
					i++;
					ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject)o).getImage(), "png", fileee);
				}
			}
		}
		document.close();
		//--ImageExtract
	}
	
	public static PdfBookMarks extraerHijos(PDOutlineItem item){
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
	
	public static String crearCarpeta(String myRuta, String nombreFichero) {
		String nuevaRuta = myRuta + "Resultados\\" + nombreFichero;
		int i = 1;
		String intentar = nuevaRuta;
		while(!(new File(intentar)).mkdirs()) {
			intentar = nuevaRuta + "(" + i + ")";
			i++;
		}
		return intentar + "\\";
	}
	
	//Creditos a: https://docs.oracle.com/javase/tutorial/2d/geometry/primitives.html
	
	//Para quitarte footer y header
	public static void textoLocalizado(PDPage pagee) {
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
}
