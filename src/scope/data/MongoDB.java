package scope.data;

import java.net.UnknownHostException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
/**
 * Class that handles communication with a MongoDB database 
 */
public class MongoDB {
	private static DB db;
	protected static MongoClient mongoClient;
	
	public static void main() {
		try {
			mongoClient = new MongoClient( InitialFrame.textField_ServerAddress.getText() , Integer.parseInt(InitialFrame.textField_Port.getText()) );
			db = mongoClient.getDB( InitialFrame.textField_DatabaseName.getText() );
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.out.print("no database found");
			e1.printStackTrace();
		}
	  }
	public static Object checkDatetimeExists(Date date ,Date time) {
		BasicDBObject query = new BasicDBObject();
		query.append("date", date);
		query.append("time", time);
		DBCursor cursor = db.getCollection("datetime").find(query);
		if (cursor.hasNext()){
			Object id_datetime = cursor.next().get("_id");
			System.out.println("checkDatetimeExists: Date found!");
			return id_datetime;}
		else{
			System.out.println("checkDatetimeExists: Date not found!");
			return null;}
	}

		static Object importIntoDatetime(Date date ,Date time){
			System.out.println("date = "+date+" time = "+time);
			BasicDBObject doc = new BasicDBObject();
		    doc.append("date", date);
		    doc.append("time", time);
		    System.out.println("doc="+doc);
		    db.getCollection("datetime").insert(doc);
			Object id_datetime = checkDatetimeExists(date, time);
			System.out.println("importIntoDatetime: Id_datetime ="+id_datetime.toString());
			return id_datetime;
		}
		
		static int importValues(int k, Object id_datetime_MongoDB, float timestamp, int can_id, int[] data, float GPS_lat, float GPS_lon){

	        BasicDBObject doc = new BasicDBObject();
			String s = "";
			for (int i = 0; i < data.length; i++){
				s = "data"+(i+1);
				doc.append(s, data[i]);
			}
			doc.append("can_id", can_id);
			doc.append("timestamp", timestamp);
			doc.append("id_datetime", id_datetime_MongoDB);
			doc.append("GPS_lat", GPS_lat);
			doc.append("GPS_lon", GPS_lon);
			try{db.getCollection("can_data").insert(doc);k++;}
			finally{}
			return k;
			}

	
		public static int deleteData(Object id_datetime_value) {
			int numDeletedData=0;
			System.out.println("id_datetime="+id_datetime_value);
			BasicDBObject query = new BasicDBObject();
			DBObject objToRemove = null;
			query.append("id_datetime", id_datetime_value);
			DBCursor cursor = db.getCollection("can_data").find(query);
			try {
			     while(cursor.hasNext()) {
			    	 	 objToRemove = cursor.next();
			    	 	 db.getCollection("can_data").remove(objToRemove);
			             numDeletedData++;
			     }
			  } finally {
			     cursor.close();
			  } 
			return numDeletedData;
		}

		public static boolean testConnection(){
			boolean result = true;
			try {
				mongoClient = new MongoClient( InitialFrame.textField_ServerAddress.getText() , Integer.parseInt(InitialFrame.textField_Port.getText()) );
				db = mongoClient.getDB( InitialFrame.textField_DatabaseName.getText() );
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				System.out.print("no database found");
				result = false;
				e1.printStackTrace();
			}	
			finally
			{if(db.collectionExists("datetime")==false){
				System.out.print("no database with datetime collection found");
				result = false;
			}
				System.out.print("database connection established\n");
			}
			return result;
		}
	}