import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbClientBase;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;

public class DbHandler {
	
	public DbHandler() {}
	
	public void testing() {
		 
		CouchDbClientBase db = new CouchDbClient("if25_tweets", true, "http", "localhost", 5984, "anta", "password");
		//CouchDbClient dbClient = new CouchDbClient("if25_tweets", true, "http", "localhost", 5984, "anta", "password");
		
		System.out.print(db.view("_all_docs")
				.reduce(false)
				.limit(10)
				.includeDocs(true)
				.query(Object.class));
		
		CouchDbInfo info = new CouchDbInfo();
		System.out.println(info.getDbName());
	}

}
