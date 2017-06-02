import java.util.ArrayList;
import java.util.Date;

/**
 * Created by morgane on 02/06/17.
 */
public class User {

    //User identity
    int idUser;
    String nameUser;
    Date creationDate;
    ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();

    //SPOT featured
    private int numberOfTweet;
    private int numberOfFriend;


    private  long ageInDay;
    private  int numberOfFollowers;
    private  int urlPerTweet;
    private  int refPerTweet;
    private  int hashtagPerTweet;
    private  int retweetPerTweet;
    private  int freqTweetperDay;
    private  int distBetwTweet;
    private int reputation;

    public User(int id, String name, int followers_count, int friends_count, Date created_at, Tweet tweet) {
        this.idUser = id;
        this.nameUser = name;
        this.numberOfFollowers = followers_count;
        this.numberOfFriend =friends_count;
        this.creationDate = created_at;
        if(!tweetsList.contains(tweet)){
            this.tweetsList.add(tweet);
        }
    }


    public int getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public int getNumberOfFriend() {
        return numberOfFriend;
    }
    public long getAgeInDay() {
        Date todayDate = new Date();
        final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;
        long delta =  todayDate.getTime()- creationDate.getTime();
        ageInDay = delta / (MILLISECONDS_PER_DAY);
        return ageInDay;
    }


}
