import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Main {

    public static void main(String[] args) {

        ArrayList<User> userList = new ArrayList<User>();
        Tweet tweet = new Tweet(2,3,1,"bonjour",true);
        User test = new User(1,"babar",100,200,new GregorianCalendar(2017, Calendar.MAY, 31).getTime(),tweet);
        userList.add(test);
        tweet = new Tweet(8,0,4,"bonjour",true);
        test.tweetsList.add(tweet);
        System.out.println(test.getAgeInDay());
        System.out.println(test.getMoyUrlPerTweet());
        System.out.println(test.getMoyHashtagPerTweet());
        System.out.println(test.getMoyMentionPerTweet());


    }

}


