package cl.intelidata.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesTools {
	final static Logger logger = Logger.getLogger(PropertiesTools.class);
	
	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public InputStream getInput() {
		return input;
	}
	
	public void setInput(InputStream input) {
		this.input = input;
	}
	
	private Properties properties;
	private InputStream input;
	
	public PropertiesTools() {
		this.properties = new Properties();
		this.input = null;
	}
	
	public PropertiesTools(String pathProperties) {
		this.properties = new Properties();
		this.input = null;
		this.cargarProperties(pathProperties);
	}
	
	/**
	 * 
	 * @param pathProperties
	 */
	public void cargarProperties(String pathProperties) {
		try {
			this.getProperties().load(new FileInputStream(pathProperties));
		} catch (FileNotFoundException e) {
			logger.error("ERROR ARCHIVO NO ENCONTRADO: " + pathProperties);
		} catch (IOException e) {
			logger.error("ERROR AL ACCESAR: " + pathProperties);
		} catch (Exception e) {
			logger.error("ERROR: " + e.getMessage());
		}
	}
}
