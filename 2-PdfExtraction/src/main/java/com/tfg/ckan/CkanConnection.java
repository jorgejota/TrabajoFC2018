package com.tfg.ckan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CKANConnection {

	public CKANConnection() {
	}

	public static void main(String[] args) throws IOException {
		try{                         
			String api_key = "8192c248-e5f6-4403-8443-20252234487b";
			//String data = "{\"Inputs\": {\"input1\": {\"ColumnNames\": [\"id\", \"regex\"], \"Values\": [[\"0\", \"the regex value\"]]}}, \"GlobalParameters\": {\"Database query\": \"select * from expone\"}}";
			URL url = new URL("http://demo.ckan.org/api/3/action/dashboard_activity_list");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput (true);             
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", api_key);
			//make the request
			//	        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());                
			//	        writer.write(data);                
			//	        writer.flush(); 
			//read the request
			BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
			String response;
			while ((response=reader.readLine())!=null) 
				System.out.println(response);
		} catch(Exception e) {
			System.out.println("Exception in MachineLearning.main " + e);
		}
	}
}
