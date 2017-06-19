

import com.fourspaces.couchdb.Database;


import com.fourspaces.couchdb.Session;


/**
 * Created by morgane on 19/06/17.
 */




public class CouchDBSolution
{

    public CouchDBSolution() {
    }
    public void getDonnee(){
        Session s = new Session("localhost",5984);
        Database db = s.getDatabase("if25_tweets");


this.getTotalDocumentCount(db);


    }



    public static int getTotalDocumentCount(Database db){

        int count = db.getDocumentCount();

        System.out.println("Total Documents: " + count);

        return count;

    }
}
