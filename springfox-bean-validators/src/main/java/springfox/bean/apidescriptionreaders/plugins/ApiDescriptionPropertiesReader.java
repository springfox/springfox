package springfox.bean.apidescriptionreaders.plugins;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;


@Component
public class ApiDescriptionPropertiesReader {

	String propertyFilePath = "/api_description.properties";
	
	Properties props = new Properties();
	
	@PostConstruct
	public void init() throws IOException {
		props.load(this.getClass().getResourceAsStream(propertyFilePath));
		
		
		
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public String getPropertyFilePath() {
		return propertyFilePath;
	}

	public void setPropertyFilePath(String propertyFilePath) {
		this.propertyFilePath = propertyFilePath;
	}
	
	
}
