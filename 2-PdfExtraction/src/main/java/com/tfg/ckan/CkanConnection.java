package com.tfg.ckan;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class CkanConnection {
	
	private Client client;
	
	//Extraer informacion de esta pagina web
	//http://docs.ckan.org/en/ckan-2.7.3/api/
	public static void main(String[] args) {
		
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
}
