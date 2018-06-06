package pruebas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Execute {
	public String myURL; 
	public WebDriver driver;
	
	public Execute(){
		this.myURL = "https://zenodo.org/search?page=1&size=20&access_right=open";
		this.driver = new FirefoxDriver();
	}
	
	public void chooseTopic(String search) {
		if(search!=null && !search.isEmpty())
			myURL+="&q="+search;
	}
	
	public void chooseType(String[] topicList) {
		for(String e:topicList){
			myURL+="&file_type="+e;
		}
	}
	
	public void run() {
		
	}
}
