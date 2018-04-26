package pruebas;

import java.util.Arrays;

public class ObjectDownload {
	public String titulo;
	public String autor;
	public String uploadTime;
	public String[] downloaded;
	
	public ObjectDownload(String titulo, String autor, String uploadTime, String[] downloaded) {
		this.titulo = titulo;
		this.autor = autor;
		this.uploadTime = uploadTime;
		this.downloaded = downloaded;
	}
	
	@Override
	public String toString() {
		return "ObjectDownload [titulo=" + titulo + ", autor=" + autor + ", uploadTime=" + uploadTime + ", downloaded="
				+ Arrays.toString(downloaded) + "]";
	}
}
