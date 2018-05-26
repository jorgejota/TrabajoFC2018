import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class UnaPequeñaPrueba {
	public static void main(String[] args) {
		String stopWords = leoDeArchivo();
		System.out.println(stopWords);
	}
	
	private static String leoDeArchivo() {
		String caracteresFixear = "";
		File file = new File("src/main/resources/commonWords");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				caracteresFixear += line + " ";
			}
		}catch(FileNotFoundException e1) {
			e1.printStackTrace();
		}catch(IOException e2) {
			e2.printStackTrace();
		}
		return caracteresFixear;
	}
}
