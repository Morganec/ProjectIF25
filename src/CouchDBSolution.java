
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

/**
 * Created by morgane on 19/06/17.
 */


public class CouchDBSolution
{
	
	private int i;
    public CouchDBSolution() { this.i = 0;}
    
    
    public void getDonnee() throws IOException {
    	HttpClient httpClient = new StdHttpClient.Builder()
    			.url("http://localhost:5984")
    			.build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector db = new StdCouchDbConnector("mydatabase", dbInstance);

        System.out.println(db.getDatabaseName());
    }



}
