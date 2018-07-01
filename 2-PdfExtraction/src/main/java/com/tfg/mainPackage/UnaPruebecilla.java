package com.tfg.mainPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

public class UnaPruebecilla {
	public static void main(String[] args) throws IOException {
		File a = new File("C:\\Users\\jorge\\Desktop\\SUBIDA\\water-10-00464(2).pdf");
		System.out.println("-----" + a.getAbsolutePath());
		if(a.delete())
			System.out.println("Guay");
		else
			System.out.println("MAL");
	}

	private static boolean textoIngles(String myTexto) {
		//load all languages:
		List<LanguageProfile> languageProfiles;
		try {
			languageProfiles = new LanguageProfileReader().readAllBuiltIn();
			//build language detector:
			LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
					.withProfiles(languageProfiles)
					.build();
			//create a text object factory
			TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
			//query:
			TextObject textObject = textObjectFactory.forText(myTexto);
			Optional<LdLocale> lang = languageDetector.detect(textObject);
			String resultado = lang.toString();
			String elverdad = resultado.substring(resultado.length()-3, resultado.length()-1);
			if(elverdad.equals("en") || elverdad.equals("en"))
				return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
