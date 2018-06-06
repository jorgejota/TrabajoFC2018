import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class PuesPorGrafica {

	public static void main(String[] args) {
		boolean fixText = false;
		List<Integer> numeros = new ArrayList<>();

		CommandLineParser parser = new DefaultParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(buildOptions(), args);
			if (line.hasOption('h') || !line.hasOption('i')) {
				printHelp();
				System.exit(0);
			}
			String pdfOrFolder = line.getOptionValue('i');
			List<File> misPDF = comprobacionArchivo(pdfOrFolder);
			if(line.hasOption('o')) {
				File comprobacion = new File(line.getOptionValue('o'));
				if(!comprobacion.exists()) {
					System.out.println("The output folder doesn´t exits");
					System.exit(1);
				}
				if(!comprobacion.isDirectory()) {
					System.out.println("The output isn´t a folder");
					System.exit(1);
				}
			}
			if(line.hasOption('f'))
				fixText = true;
			if(line.hasOption('e')) {
				String nuevo = line.getOptionValue('e');
				numeros = comprobacionDeArgumentos(nuevo);
			}
			
		}catch (ParseException exp) {
			exp.printStackTrace();
			System.err.println("Error: " + exp.getMessage());
			System.exit(1);
		}
	}
	public static Options buildOptions() {
		Options o = new Options();
		o.addOption("h", "help", false, "Indicate how yo use the program.");
		o.addOption("f", "fix", false, "[EXPERIMENTAL] Force PDF to be extracted adjunting words, deleting files, deleting footers, .. By default, NO");
		o.addOption(Option.builder("i")
				.longOpt("input")
				.desc("[REQUIRED] Absolute Pdf or folder with PDF location path. Ex: /Users/thoqbk/table.pdf")
				.hasArg()
				.argName("input PDF or FOLDER")
				.build());
		o.addOption(Option.builder("o")
				.longOpt("output")
				.desc("Absolute output file. By default the folder on i or the parent. Ex: /Users/thoqbk/results")
				.hasArg()
				.argName("output FOLDER")
				.build());
		o.addOption(Option.builder("e")
				.longOpt("extraction")
				.desc("[REQUIRED] Guess the portion of the page to analyze per page. Three types:\n"+
						"BOOKMARKS: If the PDF has BOOKMARKS we extract all content from them. Selected using comma separated or list of ranges. Examples: --number 1-3,5-7, --number 3.\n"+
						"PAGES: Same than BOOKMARKS. Examples: --number 1-3,5-7, --number 3,5,7 or --number 3.\n By default, the extraction is COMPLETE")
				.hasArg()
				.argName("NUMBERS")
				.build());	
		return o;
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("PDFExtractor", "Mised argument", buildOptions(), "", true);
	}

	private static List<File> comprobacionArchivo(String archivo) {
		File pdfOrFolder = null;
		try {
			pdfOrFolder = new File(archivo);
			if(!pdfOrFolder.exists()) {
				System.out.println("The input file or folder doesn´t exist.");
				System.exit(1);
			}
		}catch(java.lang.NullPointerException e) {
			System.out.println("The input file or folder doesn´t exist.");
			System.exit(1);
		}
		List<File> aDevolver = new ArrayList<>();
		if(pdfOrFolder.isDirectory()) {
			for (File isAPDF : pdfOrFolder.listFiles()) {
				String nombreArchivo = isAPDF.getName();
				try {
					String extension = isAPDF.getName().substring(nombreArchivo.length()-4, nombreArchivo.length());
					if(extension.equalsIgnoreCase(".pdf")){
						aDevolver.add(isAPDF); 
					}
				}catch(java.lang.StringIndexOutOfBoundsException e) {
					//Para evitar archivos con longitud menor a tres caracteres
				}
			}
			if(aDevolver.isEmpty()) {
				System.out.println("The folder doesn´t contains a PDF.");
				System.exit(1);
			}
			return aDevolver; //Es un directorio
		}
		else {
			String nombreArchivo = pdfOrFolder.getName();
			try {
				String extension = nombreArchivo.substring(nombreArchivo.length()-4, nombreArchivo.length());
				if(extension.equals(".pdf")){
					aDevolver.add(pdfOrFolder);
					return aDevolver;
				}
			}catch(java.lang.StringIndexOutOfBoundsException e) {
				//Para evitar archivos con longitud menor a tres caracteres
			}
			aDevolver.add(pdfOrFolder);
			return aDevolver;
		}
	}

	private static List<Integer> comprobacionDeArgumentos(String auxiliar) {
		List<Integer> numbers = new ArrayList<>();
		String[] arguments = auxiliar.split(",");
		for (String string : arguments) {
			System.out.println(string);
			String[] listaNumeros = string.split("-");
			for (String string2 : listaNumeros) {
				try {
					int numero = Integer.parseInt(string2);
					if(!numbers.contains(numero))
						numbers.add(numero);
				}catch(java.lang.NumberFormatException e) {
					System.out.println("The bookmark or page introduced is not a number");
					System.exit(1);
				}
			}
		}
		return numbers;
	}
}
