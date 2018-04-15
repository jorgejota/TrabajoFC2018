package com.tfg.Extraccion;

import java.util.Calendar;

public class PdfMetaData {
	public Integer numberOfPages; 
	public String title; 
	public String author; 
	public String subject; 
	public String keywords; 
	public String creator; 
	public String producer; 
	public Calendar creationDate; 
	public Calendar modificationDate;
	public String trapped;
	public PdfMetaData() {
		this.title = ""; 
		this.author = ""; 
		this.subject = ""; 
		this.keywords = ""; 
		this.creator = ""; 
		this.producer = ""; 
		this.trapped = "";
	}
}
