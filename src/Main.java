import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {

    public static void main(String[] args) {

        //ParseurJSON parseurJSON = new ParseurJSON("src/JSON/tweet.json");
        //parseurJSON.getDonnee();

        CouchDBSolution couchDBSolution = new CouchDBSolution();
        couchDBSolution.getDonnee();
        ArrayList<User> userList = new ArrayList<User>();
        Tweet tweet = new Tweet(2,3,1,"bonjour",true,new GregorianCalendar(2013, Calendar.APRIL, 10).getTime(),3);
        User test = new User(1,"babar",100,200,new GregorianCalendar(2017, Calendar.MAY, 31).getTime(),"blavlablabla");
        test.getTweetsList().add(tweet);
        userList.add(test);
        tweet = new Tweet(8,0,4,"bonjour",true,new GregorianCalendar(2015, Calendar.APRIL, 11).getTime(),3);
        test.getTweetsList().add(tweet);
        tweet = new Tweet(2,1,4,"bor",true,new GregorianCalendar(2015, Calendar.APRIL, 11).getTime(),5);
        test.getTweetsList().add(tweet);
        tweet = new Tweet(2,1,4,"bor",true,new GregorianCalendar(2015, Calendar.APRIL, 11).getTime(),7);
        test.getTweetsList().add(tweet);
        tweet =new Tweet(2,3,1,"bonjour",true,new GregorianCalendar(2013, Calendar.APRIL, 10).getTime(),8);
        test.getTweetsList().add(tweet);

        tweet =new Tweet(2,3,1,"bonjour",true,new GregorianCalendar(2014, Calendar.APRIL, 10).getTime(),2);
        User test2 = new User(2,"Francois",300,20,new GregorianCalendar(2015, Calendar.MAY, 20).getTime(),"blablablaBonjourmoic francois");
        test2.getTweetsList().add(tweet);
        tweet =new Tweet(2,3,1,"bonjour",true,new GregorianCalendar(2013, Calendar.APRIL, 9).getTime(),5);
        test2.getTweetsList().add(tweet);
        userList.add(test2);
        System.out.println(test.getAgeInDay());
        System.out.println(test.getMoyUrlPerTweet());
        System.out.println(test.getMoyHashtagPerTweet());
        System.out.println(test.getMoyMentionPerTweet());
        System.out.println(test2.getFreqTweetperDay());

        FichierCSV csv = new FichierCSV(userList);
        try {
            csv.creerFichierCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


