import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by morgane on 02/06/17.
 */
public class User {

    //User identity
    long idUser;
    String nameUser;
    Date creationDate;
    ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();

    //SPOT featured
    private int numberOfTweet;
    private int numberOfFriend;


    private  long ageInDay;
    private  int numberOfFollowers;
    private  double moyUrlPerTweet;
    private  double moyMentionPerTweet;
    private  double moyHashtagPerTweet;
    private  double moyTweetRetweete;
    private  double freqTweetperDay;
    private  double distBetwTweet;
    private double reputation;

    public User(long id, String name, int followers_count, int friends_count, Date created_at, Tweet tweet) {
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
    public double getMoyUrlPerTweet(){
        int totalUrl =0;
        for(int i=0;i<tweetsList.size();i++){
            totalUrl += tweetsList.get(i).numberOfURL;
        }
        moyUrlPerTweet = totalUrl/tweetsList.size();
        return moyUrlPerTweet;
    }

    public double getMoyMentionPerTweet() {
        int totalMentionNumber = 0;
        for(int i=0;i<tweetsList.size();i++){
            totalMentionNumber += tweetsList.get(i).numberOfMention;
        }
        moyMentionPerTweet= totalMentionNumber/tweetsList.size();
        return moyMentionPerTweet;
    }

    public double getMoyHashtagPerTweet() {
        int totalHashtagNumber = 0;
        for(int i=0;i<tweetsList.size();i++){
            totalHashtagNumber += tweetsList.get(i).numberOfHashTags;
        }
        moyHashtagPerTweet= totalHashtagNumber/tweetsList.size();
        return moyHashtagPerTweet;

    }

    public String getFreqTweetperDay() {
        ArrayList<String> listeDate = new ArrayList<String>();
ArrayList<ArrayList<Integer>> listDate;
        // A remplacer par un stream
        for(Tweet tweet:tweetsList){
            Calendar cal = Calendar.getInstance();
            cal.setTime(tweet.getTweetDate());
            String dateConcat =cal.get(Calendar.DAY_OF_MONTH)+""+cal.get(Calendar.DAY_OF_WEEK)+""+cal.get(Calendar.YEAR);

            listeDate.add(dateConcat);
        }

        //PAS ENCORE FAIT
      //  return freqTweetperDay;
        return listeDate.toString();
    }


}
