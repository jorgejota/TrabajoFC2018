package firstExtraction;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;

public class TikaToXml {
	public static void main(String[] args) throws IOException{
		String ruta = "C:\\Users\\jorge\\Desktop\\WorkSpace-Eclipse\\crawler\\DownloadPdf\\";
		PdfReader reader = new PdfReader(new FileInputStream(new File(ruta + "Descargas2.pdf")));

		for(int i =0; i < reader.getXrefSize(); i++)
		{
			PdfObject pdfobj = reader.getPdfObject(i);
			if(pdfobj != null)
			{
				if (!pdfobj.isStream()) {
					//throw new Exception("Not a stream");
				}
				else
				{
					PdfStream stream = (PdfStream) pdfobj;
					PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
					if (pdfsubtype == null) {
						//  throw new Exception("Not an image stream");

					}
					else
					{
						if (!pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
							//throw new Exception("Not an image stream");
						}       
						else
						{
							// now you have a PDF stream object with an image
							byte[] img = PdfReader.getStreamBytesRaw((PRStream) stream);
							// but you don't know anything about the image format.
							// you'll have to get info from the stream dictionary
							System.out.println("----img ------");
							System.out.println("height:" + stream.get(PdfName.HEIGHT));
							System.out.println("width:" + stream.get(PdfName.WIDTH));   
							int height = new Integer(stream.get(PdfName.HEIGHT).toString()).intValue();
							int width = new Integer(stream.get(PdfName.WIDTH).toString()).intValue();
							System.out.println("bitspercomponent:" +
									stream.get(PdfName.BITSPERCOMPONENT));

							java.awt.Image image = Toolkit.getDefaultToolkit().createImage(img);
							BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
							Graphics2D g2 = bi.createGraphics();
							ImageIO.write(bi, "PNG",new File(ruta + i + ".png"));
						}

					}
				}
				//					...             
				//					// or you could try making a java.awt.Image from the array:
				//					j

			}
		}
}
}
