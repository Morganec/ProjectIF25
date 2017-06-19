
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

/**
 * Created by morgane on 19/06/17.
 */




public class CouchDBSolution
{

    public CouchDBSolution() {
    }
    public void getDonnee(){
         HttpClient httpClient = new StdHttpClient.Builder()
                .host("localhost")
                .port(5984)
                .username("")
                .password("").build();


        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector tweets = dbInstance.createConnector("if25_tweets", true);

        System.out.println(tweets.getDatabaseName());
    }



}
