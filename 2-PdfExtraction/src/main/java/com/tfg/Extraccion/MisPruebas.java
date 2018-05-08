package com.tfg.Extraccion;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;

public class MisPruebas {
	public static void main(String[] args) throws IOException {
		String asda = "C:\\Users\\jorge\\Desktop\\BORRALOYA.txt";
		String contents = new String(Files.readAllBytes(Paths.get(asda)));
		String elNuevo = "Tune type\\s*Number of tunes\\s*Relative Number\\s*barndances\\s*298\\s*2\\,92%\\s*hornpipes\\s*843\\s*8\\,26%\\s*jigs\\s*2\\,666\\s*26\\,14%\\s*mazurkas\\s*116\\s*1\\,14%\\s*polkas\\s*695\\s*6\\,81%\\s*reels\\s*3\\,864\\s*37\\,88%\\s*slides\\s*228\\s*2\\,24%\\s*slip jigs\\s*380\\s*3\\,73%\\s*strathspey\\s*329\\s*3\\,32%\\s*three-twos\\s*78\\s*0\\,76%\\s*waltz\\s*703\\s*6\\,89%\\s*(Table .*?)\\n";
		Pattern asdada = Pattern.compile(elNuevo,Pattern.DOTALL);
		Matcher mimaccher = asdada.matcher(contents); 
		if(mimaccher.find())
			System.out.println(mimaccher.group(1));
		else
			System.out.println("FALLO");
	}
}