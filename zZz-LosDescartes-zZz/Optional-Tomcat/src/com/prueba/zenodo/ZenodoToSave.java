package com.prueba.zenodo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class ZenodoToSave {
	public String titulo;
	public String abstractText;
	public String date;
	public List<String> authors;
	public List<String> keywords;
	public ZenodoToSave() {
		this.abstractText = "";
		this.authors = new ArrayList<String>();
		this.keywords = new ArrayList<String>();
	}
}
