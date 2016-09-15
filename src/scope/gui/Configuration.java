package scope.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

/**
 * Manages reading ini config files and extracting information from them
 * @author Philipp
 */

public class Configuration {
	Ini defaultIni = new Ini();
	
	public Configuration(){
		//File file = new File("resources/defconfig.ini");
		File file = new File("resources/config_5.ini");
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
	
	/**
	 * @return the defaultIni
	 */
	
	public Ini getDefaultIni(){
		return defaultIni;
	}
	
	/**
	 * @return the number of datasets in the currently loaded ini
	 */
	public int getDatasets(){
		Ini.Section general = defaultIni.get("general");
		return Integer.parseInt(general.get("datasets"));
	}
	
	/**
	 * @param index the number of the dataset - 1
	 * @return the lower Range of the currently loaded ini
	 */
	public int getLowerRange(int index){
		return Integer.parseInt(defaultIni.get("data" + (index+1), "lowerRange"));
	}
	
	/**
	 * @param index the number of the dataset - 1
	 * @return the upper Range of the currently loaded ini
	 */
	public int getUpperRange(int index){
		return Integer.parseInt(defaultIni.get("data" + (index+1), "upperRange"));
	}
	
	/**
	 * @param index the number of the dataset
	 * @return the (long) name of this dataset 
	 */
	public String getLabel(int index){
		return defaultIni.get("data" + index, "label");
	}
	
	/**
	 * @param index the number of the dataset
	 * @return the short name of this dataset 
	 */
	public String getShortLabel(int index){
		return defaultIni.get("data" + index, "shortlabel");
	}
	
	/**
	 * Loads the passed file and overwrites the currently loaded ini file
	 * @param file The new ini file
	 * @return true on success 
	 */
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
