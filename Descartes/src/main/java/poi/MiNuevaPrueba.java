package poi;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;


//Creditos a: https://stackoverflow.com/questions/44280677/how-to-get-pictures-and-tables-from-docx-document-using-apache-poi
public class MiNuevaPrueba {
	public static void main(String[] args) {
		String myRuta = "C:\\Users\\jorge\\Desktop\\";
		String fileName = myRuta + "4.docx"; 
		readWordDocument(fileName);
		//extrarer(fileName);
	}
	public static void readWordDocument(String archivo) { 
		try { 
			String fileName = archivo; 
			if(!(fileName.endsWith(".doc") || fileName.endsWith(".docx"))) { 
				throw new FileFormatException(); 
			} else { 
				XWPFDocument doc = new XWPFDocument(new FileInputStream(fileName)); 
				List<XWPFTable> table = doc.getTables(); 
				int i = 1;
				for (XWPFTable xwpfTable : table) { 
					System.out.println("Tabla numero " + i + "\n");
					List<XWPFTableRow> row = xwpfTable.getRows(); 
					for (XWPFTableRow xwpfTableRow : row) { 
						List<XWPFTableCell> cell = xwpfTableRow.getTableCells(); 
						for (XWPFTableCell xwpfTableCell : cell) { 
							if(xwpfTableCell!=null) { 
								System.out.println(xwpfTableCell.getText()); 
								List<XWPFTable> itable = xwpfTableCell.getTables(); 
								if(itable.size()!=0) { 
									for (XWPFTable xwpfiTable : itable) { 
										List<XWPFTableRow> irow = xwpfiTable.getRows(); 
										for (XWPFTableRow xwpfiTableRow : irow) { 
											List<XWPFTableCell> icell = xwpfiTableRow.getTableCells(); 
											for (XWPFTableCell xwpfiTableCell : icell) { 
												if(xwpfiTableCell!=null) 
												{   
													System.out.println(xwpfiTableCell.getText()); 
												} 
											} 
										} 
									} 
								} 
							} 
						} 
						System.out.println("---------------");
					}
					i++;
				} 
				doc.close();
			} 
		} catch(FileFormatException e) { 
			e.printStackTrace(); 
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
	}
	public static void extractImages(String src){
		try{
			//create file inputstream to read from a binary file
			FileInputStream fs=new FileInputStream(src);
			//create office word 2007+ document object to wrap the word file
			XWPFDocument docx=new XWPFDocument(fs);
			//get all images from the document and store them in the list piclist
			List<XWPFPictureData> piclist=docx.getAllPictures();
			//traverse through the list and write each image to a file
			Iterator<XWPFPictureData> iterator=piclist.iterator();
			int i=0;
			while(iterator.hasNext()){
				XWPFPictureData pic=iterator.next();
				byte[] bytepic=pic.getData();
				BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytepic));
				ImageIO.write(imag, "jpg", new File("D:/imagefromword"+i+".jpg"));
				i++;
			}
			docx.close();

		}catch(Exception e){System.exit(-1);}

	}
	public static void extrarer(String archivo) {
		XWPFDocument docx;
		try {
			docx = new XWPFDocument(new FileInputStream(archivo));
			XWPFWordExtractor we = new XWPFWordExtractor(docx);
			System.out.println(we.getText());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//using XWPFWordExtractor Class
	}
}
