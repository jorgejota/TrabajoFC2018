package com.tfg1.Batch;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchDiario {

	//@Scheduled(fixedDelay=5000)
	//second, minute, hour, day of month, month, day(s) of week
	//@Scheduled(cron = "0 0 12 * * ?")
	public void ejecutarSiempre(ApplicationArguments args) {
		System.out.println(args.getOptionNames());
//		String rutaCarpeta = "C:\\Users\\jorge\\Desktop\\DownloadPDF";
//		File carpetaPDF = new File(rutaCarpeta);
//		creacionCarpeta(carpetaPDF);
	}

	@Scheduled(fixedDelay=5000)
	public void ejecutarAhora() {
		System.out.println("Viva España");
	}
	public void creacionCarpeta(File file) {
		if(!file.exists()) {
			try {
				if (!file.createNewFile()) {
					System.out.println("ERROR al crear la carpeta " + file.getAbsolutePath());
					System.exit(1);
				}
			} catch (IOException ioe) {
				System.out.println("ERROR al crear la carpeta " + file.getAbsolutePath());
				ioe.printStackTrace();
				System.exit(1);
			}
		}
	}
}
