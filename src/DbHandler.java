import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbClientBase;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;

public class DbHandler {
	
	public DbHandler() {}
	
	public void testing() {
		 
		CouchDbClientBase db = new CouchDbClient("if25_tweets", true, "http", "localhost", 5984, "root", "root");
		//CouchDbClient dbClient = new CouchDbClient("if25_tweets", true, "http", "localhost", 5984, "anta", "password");

	/*	System.out.print(db.view("_all_docs")
				.reduce(false)
				.limit(10)
				.includeDocs(true)
				.query(Object.class));*/
		
		//CouchDbInfo info = new CouchDbInfo();
		//System.out.println(info.getDbName());

		List<JsonObject> jsonList = db.view("userview/users-view").query(JsonObject.class);
		Set<JsonObject> hs = new HashSet<>();
		hs.addAll(jsonList);
		jsonList.clear();
		jsonList.addAll(hs);
		for (int i=0; i< 50; i++){
			JsonObject jsonObject = jsonList.get(i).get("key").getAsJsonObject();
			System.out.println(jsonObject.get("screen_name"));
		}
	}

}
