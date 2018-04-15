package chooseOneOption;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

public class MyPrueba {
	public static void main(String[] args) throws InvalidPasswordException, IOException {
		String ruta = "C:\\Users\\jorge\\Desktop\\WorkSpace-Eclipse\\crawler\\DownloadPdf\\";
		File file = new File(ruta + "Descargas2.pdf");
		
		//Leer un documento entero
		PDDocument document = PDDocument.load(file);
		PDFTextStripper pdfStripper = new PDFTextStripper();
		
		/*Dividirlo en paginas
		 *Cuidado! Aqui las paginas se guardaran una pagina por detras (empieza en 0!)*/
		Splitter splitter = new Splitter();
		List<PDDocument> Pages = splitter.split(document);
		String text = pdfStripper.getText(Pages.get(114));
		System.out.println(text);
		
		/*Extraer imagenes
		 *Te permite extraer por partes, pero lo mismo es muy complicado*/
		PDFRenderer renderer = new PDFRenderer(document);
		BufferedImage image = renderer.renderImage(0);
		ImageIO.write(image, "JPEG", new File(ruta + "unaImagen.png"));
		
		
		document.close();
	}
}
