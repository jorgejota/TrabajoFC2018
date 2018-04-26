//package chooseOneOption;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import javax.imageio.ImageIO;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.util.PDFTextStripper;
//import org.apache.pdfbox.util.Splitter;
//import org.xhtmlrenderer.simple.PDFRenderer;
//
//public class MyTabla {
//	public static void main(String[] args) throws IOException {
//		String ruta = "C:\\Users\\jorge\\Desktop\\WorkSpace-Eclipse\\crawler\\DownloadPdf\\";
//		File file = new File(ruta + "Descargas2.pdf");
//		
//		//Leer un documento entero
//		PDDocument document = PDDocument.load(file);
//		PDFTextStripper pdfStripper = new PDFTextStripper();
//		
//		/*Dividirlo en paginas
//		 *Cuidado! Aqui las paginas se guardaran una pagina por detras (empieza en 0!)*/
//		Splitter splitter = new Splitter();
//		List<PDDocument> Pages = splitter.split(document);
//		String text = pdfStripper.getText(Pages.get(20));
//		System.out.println(text);
//		
//		/*Extraer imagenes --> Funciona solo en las versiones mas nuevas
//		 *Te permite extraer por partes, pero lo mismo es muy complicado
//		PDFRenderer renderer = new PDFRenderer(document);
//		BufferedImage image = renderer.renderImage(0);
//		ImageIO.write(image, "JPEG", new File(ruta + "unaImagen.png"));*/
//		
//		
//		document.close();
//	}
//}
