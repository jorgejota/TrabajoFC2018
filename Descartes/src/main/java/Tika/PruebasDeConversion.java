package Tika;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

public class PruebasDeConversion {
	public static void main(String[] args) throws IOException {
		DocumentConverter converter = new DocumentConverter();
		String myRuta = "C:\\Users\\jorge\\Desktop\\TrabajoFC\\CasoDePreuba\\";
		String fileName = myRuta + "CasoDePrueba3.docx"; 
		Result<String> result = converter.convertToHtml(new File(fileName));
		String html = result.getValue(); // The generated HTML
		System.out.println(html);
		Set<String> warnings = result.getWarnings(); // Any warnings during conversion
	}
}
