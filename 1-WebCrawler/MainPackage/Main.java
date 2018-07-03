package MainPackage;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import alandb.Application;
import zenododb.ApplicationZenodo;

public class Main {

	public static String miCarpeta;
	public static String keyWord;
	
	public static void main(String[] args) {
		String source = "ALL";
		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(buildOptions(), args);
			if (line.hasOption('h') || !line.hasOption('k') || !line.hasOption('i')) {
				printHelp();
				System.exit(0);
			}
			keyWord = line.getOptionValue('k');
			if(line.hasOption('s')) {
				String optionDeS = line.getOptionValue('s');
				source = comprobarSource(optionDeS);
			}
			miCarpeta = comprobarCarpeta(line.getOptionValue('i'));
			if(source.equals("Darksky")) {
				extractDarksky();
			}
			else {
				if(source.equals("Zenodo")) {
					extractZenodo();
				}
				else {
					extractZenodo();
					extractDarksky();
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}

	private static void extractDarksky() {
		System.out.println("Procediendo a la extracción en AlanDB");
		new Application(crearCarpeta("Darksky"),keyWord).run();
	}

	private static void extractZenodo() {
		System.out.println("Procediendo a la extracción en Zenodo");
		new ApplicationZenodo(crearCarpeta("Zenodo"),keyWord).run();
	}
	
	public static Options buildOptions() {
		Options o = new Options();
		o.addOption("h", "help", false, "Indicate how yo use the program.");
		//o.addOption("f", "fix", false, "[EXPERIMENTAL] Force PDF to be extracted adjunting words, deleting files, deleting footers, .. By default, OFF");
		o.addOption(Option.builder("i")
				.longOpt("input")
				.desc("[REQUIRED] Input folder where download the content. Ex: /Users/jesus/aFolder")
				.hasArg()
				.argName("inputFolder")
				.build());
		o.addOption(Option.builder("s")
				.longOpt("sources")
				.desc("[OPTIONAL] Choose the information source. (ZENODO, DARKSKY, ALL). Default: ALL")
				.hasArg()
				.argName("sourceWeb")
				.build());
		o.addOption(Option.builder("k")
				.longOpt("keyword")
				.desc("[REQUIRED] Keyword to search the PDF files")
				.hasArg()
				.argName("keywords")
				.build());
		return o;
	}
	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("PDFExtractor", "Mised argument", buildOptions(), "", true);
	}
	private static String comprobarSource(String optionDeS) {
		if(optionDeS.equalsIgnoreCase("Zenodo"))
			return "Zenodo";
		if(optionDeS.equalsIgnoreCase("DARKSKY"))
			return "Darksky";
		if(optionDeS.equalsIgnoreCase("ALL"))
			return "ALL";
		else {
			System.out.println("Wrong source information.");
			System.exit(1);
			return null;
		}	
	}
	private static String crearCarpeta(String argumento) {
		String nuevaRuta = miCarpeta + "\\" + argumento;
		int i = 1;
		String intentar = nuevaRuta;
		while(!(new File(intentar)).mkdirs()) {
			intentar = nuevaRuta + "(" + i + ")";
			i++;
		}
		return intentar + "\\";
	}
	private static String comprobarCarpeta(String carpeta) {
		try {
			File pdfOrFolder = new File(carpeta);
			if(!pdfOrFolder.exists()) {
				System.out.println("The input folder doesn´t exist.");
				System.exit(1);
			}
			if(!pdfOrFolder.isDirectory()) {
				System.out.println("The input file is not a folder.");
				System.exit(1);
			}
		}catch(java.lang.NullPointerException e) {
			System.out.println("The input folder doesn´t exist.");
			System.exit(1);
		}
		return carpeta;
	}
}
