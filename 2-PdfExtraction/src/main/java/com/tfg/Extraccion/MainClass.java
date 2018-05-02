package com.tfg.Extraccion;

import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

public class MainClass {

	private static ExtractionMode miExtraction;

	public static void main(String[] args) {
		System.out.println("Seleccione un modo de operacion:");
		File[] archivos = selector1();
		miExtraction = selector2();
		if(miExtraction != ExtractionMode.COMPLETE) {
			System.out.println("Desactivado temporalmente");
			System.exit(0);
		}
		System.out.println("Comienzo de la extraccion");
		for (File file : archivos) {
			if(miExtraction == ExtractionMode.COMPLETE)
				try {
					new ReadPDF(file, miExtraction).run();
				} catch (IOException | ParserConfigurationException e) {
					e.printStackTrace();
				}
		}
		System.exit(0);
	}

	private static File[] selector1() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		JButton open = new JButton();
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		FileNameExtensionFilter pdffilter = new FileNameExtensionFilter(
				"pdf files (*.pdf)", "pdf");
		fc.setDialogTitle("Selecciona los archivo(s) PDF");
		fc.setFileFilter(pdffilter);
		fc.setAcceptAllFileFilterUsed(false);
		if(fc.showSaveDialog(open) == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFiles();
		}
		else {
			if(preguntarPorSalida())
				System.exit(0);
			return selector1();
		}
	}
	private static boolean preguntarPorSalida() {
		String[] options = new String[] {"Yes", "No"};
		int response = JOptionPane.showOptionDialog(null, "¿Esta seguro de que quiere salir?", null,
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);
		if(response == 0)
			return true;
		return false;
	}

	private static ExtractionMode selector2(){
		String[] modo = {"Extraccion completa", "Extraccion por paginas", "Extraccion por marcadores (si exiten)"};
		JFrame frame = new JFrame("Extraccion");
		String elegido =  (String) JOptionPane.showInputDialog(frame, 
				"Seleccione un modo de extraccion",
				null,
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				modo, 
				modo[0]);
		if(elegido == null)
			if(preguntarPorSalida())
				System.exit(0);
			else
				return selector2();
		if(elegido.equals("Extraccion por paginas"))
			return ExtractionMode.PAGES;
		if(elegido.equals("Extraccion completa"))
			return ExtractionMode.COMPLETE;
		return ExtractionMode.BOOKMARK;
	}

	//	private static int[] seleccionarRango(){
	//		String textoPoner;
	//		if(miExtraction == ExtractionMode.BOOKMARK)
	//			textoPoner = "bookmark";
	//		else
	//			textoPoner = "paginas";
	//		
	//		return null;
	//	}
}








//	public static String crearDirectorio(String nombre) {
//		File folder = new File(nombre);
//		if (!folder.exists()) {
//			folder.mkdir();
//			return nombre;
//		}
//		else {
//			int i = 1;
//			while(true) {
//				folder = new File(nombre + "(" + i + ")");
//				if (!folder.exists()) {
//					folder.mkdir();
//					break;
//				}
//			}
//			return nombre + "(" + i + ")";
//		}
//	}
//	private static void seleccion1() {
//	while(true) {
//		System.out.println("1 - Extraccion completa de todos los PDF de la carpeta");
//		System.out.println("2 - Extraccion de un PDF en concreto");
//		System.out.println("9 - Salir");
//		sentence = scanner.nextLine();
//		if(sentence.equals("1")) {
//			System.out.println("Pasamos a una condicion");
//			return;
//		}
//		if(sentence.equals("2")) {
//			System.out.println("Pasamos a la segunda");
//			return;
//		}
//		if(sentence.equals("9")) {
//			System.exit(0);
//		}
//		printAyuda();
//	}
//}
//private static void printAyuda() {
//	System.out.println("Mal uso. Por favor, introduzca uno de los numeros descritos a continuacion.");
//}
//private static void selector2() {
//String modoOperacion1 = scanner.nextLine();
//System.out.println("Seleccione un modo de extraccion:");
//do {
//	System.out.println("1 - Extraccion por paginas");
//	System.out.println("2 - Extraccion por marcadores (Compruebe si los tiene)");
//	System.out.println("3 - Extraccion completa");
//	scanner = new Scanner(System.in);
//	sentence = scanner.nextLine();
//}
//while(secondCondiction(sentence));
//String modoOperacion2 = scanner.nextLine();
//}
//public static boolean secondCondiction(String a) {
//switch (a) {
//case "1":
//case "2":
//case "3":
//	return true;
//default:
//	System.out.println("Mal uso. Por favor introduce uno de los numeros descritos a continuacion:");
//	return false;
//}
//}

