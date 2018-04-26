package com.tfg.Extraccion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.tfg.Extraccion.ReadPDF.ExtractionMode;

public class MainClass {
	public static void main(String[] args) {
		System.out.println("Seleccione un modo de operacion:");
		String sentence;
		Scanner scanner;
		do {
			System.out.println("1 - Extraccion completa de todos los PDF de la carpeta");
			System.out.println("2 - Extraccion de un PDF en concreto");
			scanner = new Scanner(System.in);
			sentence = scanner.nextLine();
		}
		while(firstCondiction(sentence));
		String modoOperacion1 = scanner.nextLine();
		System.out.println("Seleccione un modo de extraccion:");
		do {
			System.out.println("1 - Extraccion por paginas");
			System.out.println("2 - Extraccion por marcadores (Compruebe si los tiene)");
			System.out.println("3 - Extraccion completa");
			scanner = new Scanner(System.in);
			sentence = scanner.nextLine();
		}
		while(secondCondiction(sentence));
		String modoOperacion2 = scanner.nextLine();
		scanner.close();
		System.out.println("Comienzo de la extraccion");
		String directorioActual = System.getProperty("user.dir");
		File folder = new File(directorioActual);
		File[] currentFiles = folder.listFiles();
		List<File> listaDeArchivos = new ArrayList<>();
		for (File file : currentFiles) {
			if (file.isFile()) {
				listaDeArchivos.add(file);
			}
		}
		for (File file : listaDeArchivos) {
			ReadPDF lector = new ReadPDF(directorioActual,file,ExtractionMode.COMPLETE);
		}
	}
	public static boolean firstCondiction(String a) {
		switch (a) {
		case "1":
		case "2":
			return true;
		default:
			System.out.println("Mal uso. Por favor introduce uno de los numeros descritos a continuacion:");
			return false;
		}
	}
	public static boolean secondCondiction(String a) {
		switch (a) {
		case "1":
		case "2":
		case "3":
			return true;
		default:
			System.out.println("Mal uso. Por favor introduce uno de los numeros descritos a continuacion:");
			return false;
		}
	}
	public static String crearDirectorio(String nombre) {
		File folder = new File(nombre);
		if (!folder.exists()) {
			folder.mkdir();
			return nombre;
		}
		else {
			int i = 1;
			while(true) {
				folder = new File(nombre + "(" + i + ")");
				if (!folder.exists()) {
					folder.mkdir();
					break;
				}
			}
			return nombre + "(" + i + ")";
		}
	}
}
