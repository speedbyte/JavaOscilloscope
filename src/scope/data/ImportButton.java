package scope.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ImportButton {
	static JFrame frame;
	public JButton button;
	public static String databaseSelected;
	InitialFrame iniFrame = new InitialFrame();

	public ImportButton(String name){
		button = new JButton(name);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				iniFrame.frame.setVisible(true);
			}
		});
	}
	public static void ImportCommand(){
		JFileChooser chooser = new JFileChooser("C:\\Users");
		chooser.showOpenDialog(null);
		File selFile = chooser.getSelectedFile();
		Scanner sc = null;
		//date Fri Sep 24 03:21:53 pm 2010 => first line to import
		SimpleDateFormat formatLongDate = new SimpleDateFormat(
				"EEE MMM dd kk:mm:ss a yyyy", 
				Locale.ENGLISH
		);
		String date_SQL = "",time_SQL = "",testDate = "";
		MatchResult result;
		int id_datetime = 0, j=0, k=0, numDeletedData=-1,hour;
		Object id_datetime_MongoDB=null;
		Calendar calDatetime = Calendar.getInstance();
		Date date_MongoDB = new Date(), time_MongoDB= new Date();
		if ( selFile != null )	{
			try {
				sc = new Scanner(selFile);//("\\d{4}-[01]\\d-[0-3]\\d")
				sc.useLocale(Locale.US);
				sc.findInLine("date ([A-Z][a-z][a-z]) ([A-Z][a-z][a-z]) (\\d+) ([0-2]\\d):([0-5]\\d):([0-5]\\d) (\\w+) (\\d{4})");
				result = sc.match();
				testDate = result.group(1)+" "+result.group(2)+" "+result.group(3)+" "+result.group(4)+
					":"+result.group(5)+":"+result.group(6)+" "+result.group(7)+" "+result.group(8);
				int monthNum = getMonthNum(result.group(2));
				hour=Integer.parseInt(result.group(4));
				System.out.println(result.group(7));
				if(result.group(7).contains("PM")){hour=hour+12;}
							
				if (databaseSelected=="MongoDB"){
					calDatetime.clear();
					monthNum--;
					calDatetime.set (Integer.parseInt(result.group(8)),monthNum,Integer.parseInt(result.group(3)),
							hour,Integer.parseInt(result.group(5)),Integer.parseInt(result.group(6)));
					date_MongoDB = calDatetime.getTime();
					calDatetime.set(1901, 0, 1);
					time_MongoDB = calDatetime.getTime();
					System.out.println("date_MongoDB="+date_MongoDB+" time_MongoDB="+time_MongoDB);
								
				}
				else if (databaseSelected=="MySQL"){
					date_SQL = result.group(8)+"-"+monthNum+"-"+result.group(3);
					time_SQL = hour+":"+result.group(5)+":"+result.group(6);
				}
								
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			finally{
				if(sc!=null){
					System.out.println(testDate);
					boolean test = isValidDate(testDate, formatLongDate);
					System.out.println("isValidDate="+test);
					if (test==false){
						System.out.println("Not valid date, import failed");
						JOptionPane.showMessageDialog(null, "Not valid date", "Canceled import", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else {
						try {
							if (databaseSelected=="MongoDB"){
								id_datetime_MongoDB = MongoDB.checkDatetimeExists(date_MongoDB,time_MongoDB);
							}
							else if (databaseSelected=="MySQL"){
								id_datetime = SQL.checkDatetimeExists(date_SQL,time_SQL);
							}
						} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}	
						if (((id_datetime!=-1)&&(databaseSelected=="MySQL"))||((id_datetime_MongoDB!=null)&&(databaseSelected=="MongoDB"))){
							int choice = JOptionPane.showConfirmDialog(
								frame,
								"Data already existing for this date,\ndo you want to overwrite them?",
								"Data already existing",
								 JOptionPane.YES_NO_OPTION);
							System.out.println("choice="+choice);
							if(choice==0){
								try {
									if (databaseSelected=="MongoDB"){
										numDeletedData = MongoDB.deleteData(id_datetime_MongoDB);
									}
									else if (databaseSelected=="MySQL"){
										numDeletedData = SQL.deleteData(id_datetime);
									}
								} catch (SQLException e) {
								// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							else{return;}
						}
						else{
							try {
									if (databaseSelected=="MongoDB"){
										id_datetime_MongoDB = MongoDB.importIntoDatetime(date_MongoDB,time_MongoDB);
									}
									else if (databaseSelected=="MySQL"){
										id_datetime = SQL.importIntoDatetime(date_SQL,time_SQL);
									}							
							} catch (SQLException e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			if ((databaseSelected=="MySQL"&&id_datetime > 0)||(databaseSelected=="MongoDB"&&!id_datetime_MongoDB.toString().equals(null))){
				sc.nextLine(); //base hex  timestamps absolute => line skipped
				sc.nextLine();//internal events logged => line skipped
				//  0.007000 1  100             Rx   d 8 11 22 33 44 55 66 77 88 -48.741163 9.336416 => import data template
				float timestamp = 0, GPS_lat = 0, GPS_lon = 0;
				int can_id = 0,dataLength;
				int[] data = new int[8];
				String tempData;
				while (sc.hasNext()){
					sc.nextLine();
					timestamp = sc.nextFloat();
					sc.findInLine(" (\\d+)  (\\p{XDigit}+)             (\\w+)   d (\\d+)");
					result = sc.match();
					can_id = Integer.parseInt(result.group(2),16);
					dataLength = Integer.parseInt(result.group(4));
					tempData = "";
					for (int i=0;i<dataLength;i++){
						tempData = tempData+" (\\p{XDigit}+)";
					}
					sc.findInLine(tempData);
					result = sc.match();
					for (int i=0;i<8;i++){
						if(i<dataLength){data[i]=Integer.parseInt(result.group(i+1),16);}
						else{data[i]=-1;}
					}
					GPS_lat = sc.nextFloat();
					GPS_lon = sc.nextFloat();
					try {
						if (databaseSelected=="MongoDB"){
							k=MongoDB.importValues(k,id_datetime_MongoDB,timestamp,can_id,data,GPS_lat,GPS_lon);
						}
						else if (databaseSelected=="MySQL"){
							k=SQL.importValues(k,id_datetime,timestamp,can_id,data,GPS_lat,GPS_lon);
						}	
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					j++;
					//System.out.println("k="+k);
				}
				String dialog = "";
				if (numDeletedData!=-1){dialog=numDeletedData+" rows deleted in the database\n"+j+" rows found in the file\n"+ k+" rows added successfully in the database";}
				else{dialog=j+" rows found in the file\n"+ k+" rows added successfully in the database";}
				JOptionPane.showMessageDialog(null, dialog, "Import result", JOptionPane.INFORMATION_MESSAGE);
				System.out.println(dialog);		
			}
			sc.close();
		}
	}
	static boolean isValidDate(String input, SimpleDateFormat f) {
		try {
		f.parse(input);
		return true;
		}
		catch(ParseException e){
		return false;
		}
	}
	public static int getMonthNum(String month){
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
					"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		for (int i=0;i<months.length;i++){
			if (month.equals(months[i])==true){return i+1;}
		}
	return -1;
	}
}
