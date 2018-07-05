package com.tfg.CKAN;

import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class CKANConnection {

	private Client client;
	private String user;
	private String password;
	private List<String> textos;
	
	public CKANConnection(List<String> textos, String user, String password) {
		this.client = Client.create();
		this.user = user;
		this.password = password;
		this.textos = textos;
	}
	
	public ClientResponse manejarCliente(String url) {
		try {
			this.client.destroy();
			this.client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(user, password));
			WebResource webResource = this.client.resource(url);
			return webResource.type("application/json").post(ClientResponse.class);
		}catch(com.sun.jersey.api.client.ClientHandlerException ex) {
			System.out.println("Conexion caducada en " + url);
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
			}
			return this.manejarCliente(url);
		}
	}
	
	private void datasetPOST(String ssID,String ssNAME,String ssTEXT, int tryCounter) {
		String ssLABELS = "[  ]";
		String input = "{  \"id\": \""+ssID+"\",  "
				+ "\"labels\": "+ssLABELS+",  "
				+ "\"name\": \""+ssNAME+"\",  "
				+ "\"text\": \""+ssTEXT+"\"}";
		ClientResponse response = manejarCliente(urlTopics + "documents", input);
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

//	public static void main(String[] args) throws IOException {
//		try{                         
//			String api_key = "8192c248-e5f6-4403-8443-20252234487b";
//			//String data = "{\"Inputs\": {\"input1\": {\"ColumnNames\": [\"id\", \"regex\"], \"Values\": [[\"0\", \"the regex value\"]]}}, \"GlobalParameters\": {\"Database query\": \"select * from expone\"}}";
//			URL url = new URL("http://demo.ckan.org/api/3/action/dashboard_activity_list");
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			con.setDoInput(true);
//			con.setDoOutput (true);             
//			con.setRequestMethod("GET");
//			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Authorization", api_key);
//			//make the request
//			//	        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());                
//			//	        writer.write(data);                
//			//	        writer.flush(); 
//			//read the request
//			BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String response;
//			while ((response=reader.readLine())!=null) 
//				System.out.println(response);
//		} catch(Exception e) {
//			System.out.println("Exception in MachineLearning.main " + e);
//		}
//	}
}
