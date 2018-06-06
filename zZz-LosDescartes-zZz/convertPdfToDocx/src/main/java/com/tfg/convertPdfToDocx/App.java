package com.tfg.convertPdfToDocx;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException{
    	XWPFDocument doc = new XWPFDocument();
    	String myRuta = "C:\\Users\\jorge\\Desktop\\TrabajoFC\\CasoDePreuba\\";
		String pdf = myRuta + "CasoDePrueba.pdf"; 
    	PdfReader reader = new PdfReader(pdf);
    	PdfReaderContentParser parser = new PdfReaderContentParser(reader);
    	for (int i = 1; i <= reader.getNumberOfPages(); i++) {
    	    TextExtractionStrategy strategy =
    	      parser.processContent(i, new LocationTextExtractionStrategy());
    	    String text = strategy.getResultantText();
    	    XWPFParagraph p = doc.createParagraph();
    	    XWPFRun run = p.createRun();
    	    run.setText(text);
    	    run.addBreak(BreakType.PAGE);
    	}
    	FileOutputStream out = new FileOutputStream(myRuta + "iCasoDePrueba3(2).docx");
    	doc.write(out);
    	// Close all open files
    }
}
