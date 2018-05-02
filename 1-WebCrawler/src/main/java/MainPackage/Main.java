package MainPackage;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import alandb.Application;
import zenododb.ApplicationZenodo;

public class Main {
	private static File carpeta;
	private static String keyWord;

	public static void main(String[] args) {
		String elegido = selector();
		keyWord = selector2();
		carpeta = selector3();
		System.out.println("A continuacion, encontrara un directorio con el mismo nombre que sus palabras clave");
		System.out.println("Espere a la extraccion");
		if(elegido.equals("Darksky")) {
			extractDarksky();
		}
		else {
			if(elegido.equals("Zenodo")) {
				extractZenodo();
			}
			else {
				extractZenodo();
				extractDarksky();
			}
		}
		System.exit(0);
	}

	private static void extractDarksky() {
		System.out.println("Procediendo a la extracción en AlanDB");
		new Application(crearCarpeta("Darksky"),keyWord).run();
	}

	private static void extractZenodo() {
		System.out.println("Procediendo a la extracción en Zenodo");
		new ApplicationZenodo(crearCarpeta("Zenodo"),keyWord).run();
	}

	private static String selector(){
		String[] modo = {"Zenodo", "Darksky", "Ambos"};
		JFrame frame = new JFrame("Extraccion");
		String elegido =  (String) JOptionPane.showInputDialog(frame, 
				"Seleccione una fuente de informacion",
				null,
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				modo, 
				modo[0]);
		if(elegido == null)
			if(preguntarPorSalida())
				System.exit(0);
			else
				return selector();
		return elegido;
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

	private static String selector2() {
		JFrame frame = new JFrame("KeyWord");
		return JOptionPane.showInputDialog(frame, "Introduzca palabra(s) clave(s)");
	}

	private static File selector3() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		JButton open = new JButton();
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Seleccione donde guardar sus resultados");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(fc.showSaveDialog(open) == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		}
		else {
			if(preguntarPorSalida())
				System.exit(0);
			return selector3();
		}
	}

	private static String crearCarpeta(String argumento) {
		String nuevaRuta =  carpeta.getAbsolutePath() + "\\" + argumento;
		int i = 1;
		String intentar = nuevaRuta;
		while(!(new File(intentar)).mkdirs()) {
			intentar = nuevaRuta + "(" + i + ")";
			i++;
		}
		return intentar + "\\";
	}
}




//public static boolean firstCondiction(String a) {
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
//System.out.println("Introduzca el numero de la base de datos con la que quieres trabajar:");
//String sentence;
//Scanner scanner;
//do {
//	System.out.println("1 - Zenodo");
//	System.out.println("2 - AlanDB");
//	System.out.println("3 - Ambas bases de datos");
//	scanner = new Scanner(System.in);
//	sentence = scanner.nextLine();
//}
//while(firstCondiction(sentence));