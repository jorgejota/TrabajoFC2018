package com.tfg.librairy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONValue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class LibrairyConnection {

	public LibrairyConnection(List<String> textos, String stopWords, String typeTopic) {
		this.textos = textos;
		mequedo = 0;
		this.client = Client.create();
		this.stopWords = stopWords;
		this.typeTopic = typeTopic;
	}

	public LibrairyConnection(List<String> textos,String typeTopic) {
		this.textos = textos;
		mequedo = 0;
		this.client = Client.create();
		this.typeTopic = typeTopic;
		this.stopWords = "";
	}

	private List<String> textos;
	private Integer mequedo;
	private Client client;
	private String stopWords;
	private String typeTopic;
	/*
	 * Creditos a: http://www.mkyong.com/webservices/jax-rs/restful-java-client-with-jersey-client/
	 */

	public void sacarTopicos() {
		//Por si quieres guardarte las trazas del programa.
		/*File errores = new File(carpeta + "\\ERRORES.txt");
		if(!errores.exists()) {
			try {
				errores.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}*/
		for (int i = mequedo; i < textos.size(); i++) {
			System.out.println("Pasamos el texto " + i);
			String textoFixeado = fixearString(textos.get(i));
			documentsPOST("id" + i, "name" + i, textoFixeado,0);
			mequedo = i;
		}
		System.out.println("\nVamos a realizar las dimensiones");
		dimensionsPOST();
		dimensionsGET();
	}
	
	public void entrenarModelo() {
		for (int i = 0; i < textos.size(); i++) {
			String vectorTexto = shapePOST(fixearString(textos.get(i)));
			pointsPOST("id" + i,"name" + i,vectorTexto);
		}
	}
	
	public void comprobarArchivo() {
		for (int i = 0; i < textos.size(); i++) {
			String vectorTexto = shapePOST(fixearString(textos.get(i)));
			neighboursPOST(""+i,vectorTexto);
		}
	}

	public ClientResponse manejarCliente(String url,String input) {
		try {
			this.client.destroy();
			this.client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("jgalan", "oeg2018"));
			WebResource webResource = this.client.resource(url);
			ClientResponse aDevolver;
			if(input != null) 
				return webResource.type("application/json").post(ClientResponse.class, input);
			else
				return webResource.accept("application/json").get(ClientResponse.class);
		}catch(com.sun.jersey.api.client.ClientHandlerException ex) {
			System.out.println("Conexion caducada");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
			}
			return this.manejarCliente(url,input);
		}
	}

	public void borrarElemento() {
		System.out.println("Vamos a eliminar elementos");
		try {
			documentsDELETE();
		}catch(com.sun.jersey.api.client.ClientHandlerException ex) {
			System.out.println("Conexion caducada en BORRAR ELEMENTOS");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
			}
			this.borrarElemento();
		}
	}

	private String fixearString(String prueba) {
		//		String aDevolver = prueba;
		//		//aDevolver = aDevolver.replaceAll("[\\x00-\\x09\\x11\\x12\\x14-\\x1F\\x7F]", "");
		//		aDevolver = aDevolver.replaceAll("\"","\\\"");
		//		aDevolver = aDevolver.replaceAll("\\p{Cc}", "");
		//		//aDevolver = aDevolver.replaceAll("'", "\\\"");
		//		return Base64.getEncoder().encodeToString(prueba.getBytes());
		return JSONValue.escape(prueba);
	}
	private void documentsGET() {
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("jgalan", "oeg2018"));
		WebResource webResource = client
				.resource("http://librairy.linkeddata.es/jgalan-topics/documents");
		ClientResponse response = webResource.accept("application/json")
				.get(ClientResponse.class);
		if (response.getStatus() != 200) {
			try {
				Thread.sleep(10000);
				documentsGET();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void documentsPOST(String ssID,String ssNAME,String ssTEXT, int tryCounter) {
		String ssLABELS = "[  ]";
		String input = "{  \"id\": \""+ssID+"\",  "
				+ "\"labels\": "+ssLABELS+",  "
				+ "\"name\": \""+ssNAME+"\",  "
				+ "\"text\": \""+ssTEXT+"\"}";
		ClientResponse response = manejarCliente("http://librairy.linkeddata.es/jgalan-topics/documents", input);
		int status = response.getStatus();
		if (status == 400 || status == 401 || status == 402 || status == 403 || status == 404 || status == 503) {
			try {
				if(tryCounter > 5 && status == 400) {
					System.out.println("FALLO AL ESCRIBIR EL ARCHIVO " + ssID + ": " + response.getEntity(String.class));
					return;
				}
				System.out.println("STATUS = " + status);
				String output = response.getEntity(String.class);
				System.out.println(output);
				Thread.sleep(4000);
				documentsPOST(ssID,ssNAME,ssTEXT,tryCounter+1);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("A problem has ocurred with a thread");
				System.exit(1);
			}
		}
		System.out.println("Subido el archivo " + ssID + " correctamente. Codigo " + status);
	}

	public void documentsDELETE() {
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("jgalan", "oeg2018"));
		WebResource webResource = client
				.resource("http://lab4.librairy.linkeddata.es/jgalan-topics/documents");
		ClientResponse response = webResource.accept("application/json")
				.delete(ClientResponse.class);
		int status = response.getStatus();
		if (status == 200 || status == 202 || status == 204) 
			return;
		try {
			Thread.sleep(10000);
			documentsDELETE();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void dimensionsGET() {
		ClientResponse response = manejarCliente("http://librairy.linkeddata.es/jgalan-topics/dimensions", null);
		if (response.getStatus() != 200) {
			try {
				Thread.sleep(180000);
				dimensionsGET();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String laRespuesta = obtenerElJson(response.getEntity(String.class),"dimensions");
		if(laRespuesta.equals("[]")) {
			try {
				Thread.sleep(40000);
				dimensionsGET();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Mis topicos son: ");
			System.out.println(laRespuesta);
		}
	}

	private String obtenerElJson(String laRespuesta, String queObtener) {
		System.out.println("Mi respuesta: " + laRespuesta);
		JsonParser jsonParser = new JsonParser();
		JsonObject nuevo = (JsonObject)jsonParser.parse(laRespuesta);
		return nuevo.get(queObtener).toString();
	}

	public void dimensionsPOST() {
		ClientResponse response = manejarCliente("http://librairy.linkeddata.es/jgalan-topics/dimensions",
				"{ \"parameters\": { "
						+ "\"language\": \"en\", "
						+ "\"email\": \"jorgejotahoyo@gmail.com\", "
						+ "\"stopwords\": \""+ stopWords +"\" "
						+ "}}");
		int status = response.getStatus();
		if (status == 400 || status == 401 || status == 402 || status == 403 || status == 404 || status == 503) {
			try {
				Thread.sleep(60000);
				dimensionsPOST();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String shapePOST(String texto) {
		ClientResponse response = manejarCliente("http://librairy.linkeddata.es/jgalan-topics/shape",
				"{ \"text\": \""+texto+"\"}");
		int status = response.getStatus();
		if (status == 400 || status == 401 || status == 402 || status == 403 || status == 404 || status == 503) {
			System.out.println("No deberia de volver a llamar a esto ...");
			System.out.println("Status: " + status
					+ "Message: " + response.getEntity(String.class));
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.shapePOST(texto);
		}
		else {
			String laRespuesta = obtenerElJson(response.getEntity(String.class),"vector");
			if(laRespuesta.equals("[]")) {
				System.out.println("Esto NUNCA deberia de ser vacio");
				System.exit(0);
			}
			System.out.println(laRespuesta);
			return laRespuesta;
		}
	}

	public void pointsPOST(String id, String name, String vector) {
		ClientResponse response = manejarCliente("http://librairy.linkeddata.es/jgalan-space/points",
				"{ \"id\": \""+id+"\", "
						+"\"name\": \""+name+"\", "
						+"\"shape\": "+ vector + ", "
						+"\"type\": \""+typeTopic+"\""
						+ " }");
		int status = response.getStatus();
		if (status == 400 || status == 401 || status == 402 || status == 403 || status == 404 || status == 503) {
			System.out.println("Esto nunca deberia de haber pasado");
			System.out.println("ssssssssss" + status + response.getEntity(String.class));
		}
		System.out.println(response);
	}

	public void neighboursPOST(String number, String vector) {
		ClientResponse response = manejarCliente("http://librairy.linkeddata.es/jgalan-space/neighbours",
				"{ \"force\": true, "
						+"\"number\": "+number + " , "
						+"\"shape\": "+ vector + " , "
						+"\"types\": [\""+ this.typeTopic+"\"]}");
		int status = response.getStatus();
		if (status == 400 || status == 401 || status == 402 || status == 403 || status == 404 || status == 503) {
			System.out.println("FALLO AL REALIZAR neighboursPOST\n" + response.getEntity(String.class));
			System.exit(0);
		}
		System.out.println("Ha ido bien " + status);
		String laRespuesta = obtenerElJson(response.getEntity(String.class),"neighbours");
		Pattern pattern = Pattern.compile("score\":(.*?)}");
		Matcher matcher = pattern.matcher(laRespuesta);
		System.out.println(laRespuesta);
		if(matcher.find()) {
			double porcentaje = Double.parseDouble(matcher.group(1));
			System.out.println(porcentaje);
		}
	}
}
