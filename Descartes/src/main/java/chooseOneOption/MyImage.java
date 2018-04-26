package chooseOneOption;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

//
//import java.io.File;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDResources;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
//
//public class MyImage{
//	
//	public static void main(String[] args) {
//		String ruta = "C:\\Users\\jorge\\Desktop\\WorkSpace-Eclipse\\crawler\\DownloadPdf\\";
//		File file = new File(ruta + "201431791048338.pdf");
//		PDDocument document = PDDocument.load(file);
//		List pages = document.getDocumentCatalog().getAllPages();
//		Iterator iter = pages.iterator();
//		while( iter.hasNext() )
//		{
//		    PDPage page = (PDPage)iter.next();
//		    PDResources resources = page.getResources();
//		    Map images = resources.getImages();
//		    if( images != null )
//		    {
//		        Iterator imageIter = images.keySet().iterator();
//		        while( imageIter.hasNext() )
//		        {
//		            String key = (String)imageIter.next();
//		            PDXObjectImage image = (PDXObjectImage)images.get( key );
//		            String name = getUniqueFileName( key, image.getSuffix() );
//		            System.out.println( "Writing image:" + name );
//		            image.write2file( name );
//		        }
//		    }
//		}
//	}
//}






//From: https://stackoverflow.com/questions/8705163/extract-images-from-pdf-using-pdfbox
//import java.io.File;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDResources;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
//
//@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
//public class MyImage {
//	public static void main(String[] args) {
//		try {
//			String ruta = "C:\\Users\\jorge\\Desktop\\";
//			String sourceDir = ruta + "\\WorkSpace-Eclipse\\crawler\\DownloadPdf\\Descargas2.pdf";// Paste pdf files in PDFCopy folder to read
//			String destinationDir = ruta + "Images\\";
//			File oldFile = new File(sourceDir);
//			if (oldFile.exists()) {
//				PDDocument document = PDDocument.load(sourceDir);
//				List<PDPage> list = document.getDocumentCatalog().getAllPages();
//				String fileName = oldFile.getName().replace(".pdf", "_cover");
//				int totalImages = 1;
//				for (PDPage page : list) {
//					PDResources pdResources = page.getResources();
//
//					Map pageImages = pdResources.getImages();
//					if (pageImages != null) {
//
//						Iterator imageIter = pageImages.keySet().iterator();
//						while (imageIter.hasNext()) {
//							String key = (String) imageIter.next();
//							PDXObjectImage pdxObjectImage = (PDXObjectImage) pageImages.get(key);
//							pdxObjectImage.write2file(destinationDir + fileName+ "_" + totalImages);
//							totalImages++;
//						}
//					}
//				}
//			} else {
//				System.err.println("File not exists");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}





//From https://stackoverflow.com/questions/8705163/extract-images-from-pdf-using-pdfbox
//public class MyImage {
//	public static void main(String[] args) throws InvalidPasswordException, IOException {
//		String ruta = "C:\\Users\\jorge\\Desktop\\WorkSpace-Eclipse\\crawler\\DownloadPdf\\";
//		File file = new File(ruta + "Descargas2.pdf");
//		PDDocument document = PDDocument.load(file);
//	    PDPageTree list = document.getPages();
//	    for (PDPage page : list) {
//	        PDResources pdResources = page.getResources();
//	        for (COSName c : pdResources.getXObjectNames()) {
//	            PDXObject o = pdResources.getXObject(c);
//	            System.out.println(o.getClass());
//	            if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
//	                File fileee = new File("C:\\Users\\jorge\\Desktop\\Images2\\" + System.nanoTime() + ".png");
//	                ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject)o).getImage(), "png", fileee);
//	            }
//	        }
//	    }
//	}
//}




//Buscar
