package com.tfg.Extraccion;

import java.util.ArrayList;

public class PdfBookMarks {
	private String titulo;
	private String texto;
	private ArrayList<PdfBookMarks> hijos;
	public PdfBookMarks() {
		hijos = new ArrayList<PdfBookMarks>();
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public void setHijo(PdfBookMarks e) {
		hijos.add(e);
	}
	public void getAllHijos() {
		for (PdfBookMarks pdfBookMarks : hijos) {
			pdfBookMarks.getTitulo();
		}
	}
}
