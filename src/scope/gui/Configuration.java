package scope.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	//Properties properties = new Properties();
	
	public Configuration(Properties properties){

		try {
			
			FileInputStream fileInStream = new FileInputStream("resources/config.properties"); //Standardkonfiguration
			properties.load(fileInStream);
			fileInStream.close();
			
			String str = properties.getProperty("dataset");
			System.out.println(str);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readFile(File configFile, Properties properties){
		try {
			FileInputStream fileInStream = new FileInputStream(configFile.getAbsolutePath());
			properties.load(fileInStream);
			fileInStream.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
}
