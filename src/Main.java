import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {

    public static void main(String[] args) {
    	
    	/*** Modifier la ligne ci-dessous pour indiquer vos propre paramètres de connexion ****/
    	//Param 1 : URL accès à CouchDB
    	//Param 2 : Port accès à CouchDB
    	//Param 3 : Identifiant de l'admin pour acces à CouchDB
    	//Param 4 : Mot de passe de l'admin pour acces à CouchDB
    	
    	KNN test = new KNN("localhost", 5984, "root", "root");
    	test.mainMethod();
    }

}


