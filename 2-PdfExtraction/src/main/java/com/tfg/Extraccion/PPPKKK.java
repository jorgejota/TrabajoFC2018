package com.tfg.Extraccion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PPPKKK {
	public static void main(String[] args) throws IOException {
		String content;
		content = new String(Files.readAllBytes(Paths.get("C:\\Users\\jorge\\Desktop\\aaa.txt")));
		Pattern che1 = Pattern.compile("RESUMEN(.*?)ABSTRACT",Pattern.DOTALL);
		Matcher reg1 = che1.matcher(content);
		if(reg1.find()) {
			String encontrado = reg1.group(0);
			System.out.println(encontrado);
		}
	}
}
