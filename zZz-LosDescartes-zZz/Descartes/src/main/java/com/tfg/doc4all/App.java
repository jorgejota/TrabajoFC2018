package com.tfg.doc4all;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

public class App {
	public static void main(String[] args) throws FileNotFoundException {
	    File inputdocxfile = new File("C:\\Users\\jorge\\Desktop\\Mis Casos de Prueba\\wltdo");

	    File outputpdffile = new File("C:\\Users\\jorge\\Desktop\\Mis Casos de Prueba\\wltdo\\"
	            + "EstoNOES.docx");
	    IConverter converter = LocalConverter.builder().baseFolder(inputdocxfile)
	            .workerPool(20, 25, 2, TimeUnit.SECONDS).processTimeout(5, TimeUnit.SECONDS).build();

	    Future<Boolean> conversion = converter.convert(inputdocxfile).as(DocumentType.PDF).to(outputpdffile)
	            .as(DocumentType.MS_WORD).prioritizeWith(1000).schedule();
	    
	    
	}
}
