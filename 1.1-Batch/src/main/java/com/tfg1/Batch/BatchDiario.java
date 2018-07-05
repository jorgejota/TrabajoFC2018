package com.tfg1.Batch;

import java.net.HttpURLConnection;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import alandb.Application;
import zenododb.ApplicationZenodo;

@Component
public class BatchDiario {

	@Scheduled(cron = "0 0 12 * * ?")
	public void ejecutarAhora() {
		String owner_org = "librAly";
		String api_key = "8192c248-e5f6-4403-8443-20252234487b";
		//TODO cambiar URL
		String urlSubida = "http://demo.ckan.org/api/3/action/dashboard_activity_list";
		//TODO cambiar carpeta
		List<JsonObject> jsonZenodo = new ApplicationZenodo("//", "light pollution", true, true).extractAllJson();
		List<JsonObject> jsonAlan = new Application("//", "", true, true).extractAllJson();
		for (JsonObject jsonObject : jsonAlan) {
			String titulo = jsonObject.get("titulo").toString();
			String notas = jsonObject.toString();
			//TODO manejar el input	
			String input = createInput(titulo,notas,owner_org);
			ClientResponse response = manejarCliente(urlSubida);
		}
		for (JsonObject jsonObject : jsonAlan) {
			String titulo = jsonObject.get("titulo").toString();
			String notas = jsonObject.toString();
			String input = createInput(titulo,notas,owner_org);
			ClientResponse response = manejarCliente(urlSubida);
		}

	}
	private ClientResponse manejarCliente(String url) {
		//TODO poner la Api Key
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("jgalan", "librairy"));
		WebResource webResource = client.resource(url);
		return webResource.type("application/json").post(ClientResponse.class);
	}
	private String createInput(String name, String notes, String owner_org) {
		return "{  \"name\": \""+name+"\",  "
				+ "\"notes\": "+notes+",  "
				+ "\"owner_org\": \""+owner_org+"\"}";
	}

}
