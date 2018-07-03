package crawly.controllers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
import com.tfg.Extraccion.RangeExtraction;
import com.tfg.Extraccion.ExtractionMode;
import com.tfg.Extraccion.ReadPDF;
import com.tfg.LibrairyConection.LibrairyConnection;

@Controller
public class MainController {

	@Autowired
	private Environment env;

	@RequestMapping("/")
	public String index() {
		return "index.html";
	}

	@RequestMapping(value = "/uploadFile2", method = RequestMethod.POST)
	@ResponseBody
	public Response uploadFile2(
			@RequestParam("uploadfile") ArrayList<MultipartFile> uploadfile) {
		double[] respuesta = null;
		String filepath = "";
		for (MultipartFile d : uploadfile) {
			String filename = d.getOriginalFilename();
			System.out.println(filename);
		}
		return null;
	}
			
	@RequestMapping(value = "/checkPDF", method = RequestMethod.GET)
	@ResponseBody
	public Response checkPDF(
			@RequestParam("checkPDF") MultipartFile[] multipartFile,
			@RequestParam("modePDF") String modePDF,
			@RequestParam("fixPDF") String fixTextString,
			@RequestParam("allResources") String allResourcesString,
			@RequestParam("importantNumbers") String importantNumbers,
			HttpServletResponse httpServletResponse) {
		Boolean fixText = Boolean.valueOf(fixTextString);
		Boolean allResources = Boolean.valueOf(allResourcesString);
		System.out.println("Me llega una peticion :D");
		String filepath = "";
		String rutaOutputFolder = env.getProperty("crawly.paths.pdfFiles");
		File outputfolder = new File(rutaOutputFolder);
		for (MultipartFile uploadfile : multipartFile) {
			List<RangeExtraction> numerosMarcadores = new ArrayList<>();
			List<RangeExtraction> numerosPaginas = new ArrayList<>();
			try {
				String filename = uploadfile.getOriginalFilename();
				filepath = Paths.get(crearArchivo(filename,"crawly.paths.pdfFiles")).toString();
				File miArchivo = new File(filepath);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(miArchivo));
				stream.write(uploadfile.getBytes());
				stream.close();
				ExtractionMode modoExtraccion = ExtractionMode.COMPLETE;
				if(modePDF=="butPages") {
					modoExtraccion = ExtractionMode.PAGES;
					numerosPaginas = comprobacionDeArgumentos(importantNumbers);
				}
				if(modePDF=="butBook") {
					modoExtraccion = ExtractionMode.BOOKMARK;
					numerosMarcadores = comprobacionDeArgumentos(importantNumbers);
				}
				String rutaCarpeta = new ReadPDF(miArchivo,modoExtraccion,numerosMarcadores,numerosPaginas,fixText,outputfolder,false).run();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FileOutputStream fos = new FileOutputStream(rutaOutputFolder + "\\download.zip");
				ZipOutputStream zos = new ZipOutputStream(fos);
				addToZipFile(rutaCarpeta, zos);
				zos.close();
				fos.close();
				byte[] enviar = convertToArrayByte(rutaOutputFolder + "\\download.zip");
		        Cookie cookie = new Cookie("fileDownload", "true");
		        cookie.setPath("/");
		        httpServletResponse.addCookie(cookie);
		        httpServletResponse.setContentType("application/zip");
		        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=files.zip");
		        httpServletResponse.getOutputStream().write(enviar);
			}catch (Exception e) {
				File borrar = new File(filepath);
				if(borrar.exists())
					borrar.delete();
				System.out.println(e.getMessage());
				//return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				return new Response("Bad Request ", null, 1);
			}
		}
		return null;
	} 

	@RequestMapping(value = "/makeModel", method = RequestMethod.GET)
	@ResponseBody
	public Response makeModel(@RequestParam("urlLibrairy") String urlLibrairy,
			@RequestParam("user") String user,
			@RequestParam("password") String password,
			@RequestParam("topic") String topic,
			@RequestParam("correo") String correo,
			@RequestParam("nDocuments") String nDocuments) {
		
		return null;
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public Response uploadFile(
			@RequestParam("uploadfile") MultipartFile uploadfile) {
		System.out.println("Llegamos a entrar aqui");
		double[] respuesta = null;
		String filepath = "";
		try {
			String filename = uploadfile.getOriginalFilename();
			filepath = Paths.get(crearArchivo(filename,"crawly.paths.uploadedFiles")).toString();
			File miArchivo = new File(filepath);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(miArchivo));
			stream.write(uploadfile.getBytes());
			stream.close();
			String textoAPasar = leemosElPDF(filepath);
			if(textoAPasar == null)
				return new Response("It is not possible to read the pdf", respuesta, 1);
			if(!textoIngles(textoAPasar)) {
				return new Response("The PDF is not in English or does not contain enough information", respuesta, 1);
			}
			respuesta = comprobarArchivo(filepath);
			if(miArchivo.delete()){
				System.out.println(miArchivo.getName() + " is deleted!");
			}else{
				System.out.println("Delete operation is failed.");
			}
		}
		catch (Exception e) {
			File borrar = new File(filepath);
			if(borrar.exists())
				borrar.delete();
			System.out.println(e.getMessage());
			//return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			return new Response("Bad Request ", null, 1);
		}
		if(respuesta == null) {
			return new Response("Bad Request ", respuesta, 1);
		}
		return new Response("Done ", respuesta, 0);
	} 

	private String leemosElPDF(String filepath) {
		File file = new File(filepath);
		try {
			return new ReadPDF(file).run();
		} catch (IOException | ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	private double[] comprobarArchivo(String textoPDF) {
		String user = "jgalan";
		String password = "oeg2018";
		String keyWord = "light";
		String urlSpace = "http://librairy.linkeddata.es/jgalan-space/";
		String urlTopics = "http://librairy.linkeddata.es/jgalan-topics/";
		LibrairyConnection paraComprobar = new LibrairyConnection(keyWord,urlTopics,urlSpace,user,password);
		return paraComprobar.comprobarArchivo(textoPDF);
	}

	private boolean textoIngles(String myTexto) {
		List<LanguageProfile> languageProfiles;
		try {
			languageProfiles = new LanguageProfileReader().readAllBuiltIn();
			LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
					.withProfiles(languageProfiles)
					.build();
			TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
			TextObject textObject = textObjectFactory.forText(myTexto);
			Optional<LdLocale> lang = languageDetector.detect(textObject);
			String resultado = lang.toString();
			System.out.println("Que me dice el texto " + resultado);
			String elverdad = resultado.substring(resultado.length()-3, resultado.length()-1);
			if(elverdad.equals("en") || elverdad.equals("en"))
				return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String crearArchivo(String argumento, String argument) {
		String probar = argumento.substring(0, argumento.length()-4);
		String nuevaRuta = env.getProperty(argument) + "\\" + probar + ".pdf";
		int i = 1;
		String intentar = nuevaRuta;
		while((new File(intentar)).exists()) {
			intentar = env.getProperty(argument) + "\\" + probar +  "(" + i + ")" + ".pdf";
			i++;
		}
		return intentar;
	}

	private static List<RangeExtraction> comprobacionDeArgumentos(String auxiliar) {
		List<RangeExtraction> numbers = new ArrayList<>();
		String[] arguments = auxiliar.split(",");
		for (String string : arguments) {
			if(string.contains("-")) {
				String[] listaNumeros = string.split("-");
				//El programa no permite rango multiple al menos que este separado por una coma
				if(listaNumeros.length != 2) {
					System.out.println("The bookmark or page introduced is not a number");
					System.exit(1);
				}
				try {
					int numeroInicial = Integer.parseInt(listaNumeros[0]);
					int numeroFinal = Integer.parseInt(listaNumeros[1]);
					if(numeroFinal < numeroInicial) {
						System.out.println("The bookmark or page introduced is not a number");
						System.exit(1);
					}
					RangeExtraction rango = new RangeExtraction(numeroInicial,numeroFinal);
					numbers.add(rango);
				}catch(java.lang.NumberFormatException e) {
					System.out.println("The bookmark or page introduced is not a number");
					System.exit(1);
				}
			}
			else {
				try {
					int numeroInicial = Integer.parseInt(string);
					RangeExtraction rango = new RangeExtraction(numeroInicial);
					numbers.add(rango);
				}catch(java.lang.NumberFormatException e) {
					System.out.println("The bookmark or page introduced is not a number");
					System.exit(1);
				}
			}
		}
		return numbers;
	}
	private static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		zos.closeEntry();
		fis.close();
	}
	private byte[] convertToArrayByte(String zipFnm) {
		FileInputStream fileInputStream=null;
		File file = new File(zipFnm);
		byte[] bFile = new byte[(int) file.length()];
		try{
			//convert file into array of bytes
			fileInputStream = new FileInputStream(zipFnm);
			fileInputStream.read(bFile);
			fileInputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return bFile;
	}  
}

