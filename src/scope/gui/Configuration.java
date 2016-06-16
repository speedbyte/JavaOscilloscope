package scope.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class Configuration {
	Ini defaultIni = new Ini();
	
	public Configuration(){
		//Ini ini = new Ini();
		File file = new File("resources/defconfig.ini");
        try {
			defaultIni.load(new FileReader(file));
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Ini getDefaultIni(){
		return defaultIni;
	}
	
	public int getDatasets(){
		Ini.Section general = defaultIni.get("general");
		return Integer.parseInt(general.get("datasets"));
	}
	
	public boolean loadFile(File file){
		try {
			defaultIni.load(new FileReader(file));
			return true;
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
