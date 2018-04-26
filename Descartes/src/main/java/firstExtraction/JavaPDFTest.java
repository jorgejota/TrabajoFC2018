package firstExtraction;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class JavaPDFTest {

    public static void main(String[] args) throws IOException {
    	
    	try {
    	    PDDocument document = null;
    	    document = PDDocument.load(new File("test.pdf"));
    	    document.getClass();
    	    if (!document.isEncrypted()) {
    	        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
    	        stripper.setSortByPosition(true);
    	        PDFTextStripper Tstripper = new PDFTextStripper();
    	        String st = Tstripper.getText(document);
    	        System.out.println("Text:" + st);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
}    
}