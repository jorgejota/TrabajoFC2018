package crawly.controllers;
      
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.tfg.Extraccion.ReadPDF;
import com.tfg.librairy.LibrairyConnection;

@Controller
public class MainController {

	@Autowired
	private Environment env;

	@RequestMapping("/")
	public String index() {
		return "upload.html";
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public Response uploadFile(
			@RequestParam("uploadfile") MultipartFile uploadfile) {
		double[] respuesta = null;
		String filepath = "";
		try {
			String filename = uploadfile.getOriginalFilename();
			filepath = Paths.get(crearArchivo(filename)).toString();
			File miArchivo = new File(filepath);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(miArchivo));
			stream.write(uploadfile.getBytes());
			stream.close();
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
			return new Response("Bad Request ", null);
		}
		if(respuesta == null) {
			return new Response("Bad Request ", respuesta);
		}
		return new Response("Done ", respuesta);
	} 

	private double[] comprobarArchivo(String filepath) {
		File file = new File(filepath);
		String user = "jgalan";
		String password = "oeg2018";
		String keyWord = "light";
		String urlSpace = "http://librairy.linkeddata.es/jgalan-space/";
		String urlTopics = "http://librairy.linkeddata.es/jgalan-topics/";
		String textoPDF = "";
		try {
			textoPDF = new ReadPDF(file).run();
		} catch (IOException | ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		LibrairyConnection paraComprobar = new LibrairyConnection(keyWord,urlTopics,urlSpace,user,password);
		return paraComprobar.comprobarArchivo(textoPDF);
	}

	private String crearArchivo(String argumento) {
		String probar = argumento.substring(0, argumento.length()-4);
		String nuevaRuta = env.getProperty("crawly.paths.uploadedFiles") + "\\" + probar + ".pdf";
		int i = 1;
		String intentar = nuevaRuta;
		while((new File(intentar)).exists()) {
			intentar = env.getProperty("crawly.paths.uploadedFiles") + "\\" + probar +  "(" + i + ")" + ".pdf";
			i++;
		}
		return intentar;
	}

} 

