import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by morgane on 02/06/17.
 */
public class User {


    //User identity
    long idUser;



    String nameUser;
    Date creationDate;


    ArrayList<Tweet> tweetsList;
String descriptionProfil;
    //SPOT featured
    private int numberOfTweet;
    private int numberOfFriend;


    private  long ageInDay;



    private  int numberOfFollowers;
    private  double moyUrlPerTweet;
    private  double moyMentionPerTweet;
    private  double moyHashtagPerTweet;
    private  double moyLikePerTweet;
    private  double moyTweetRetweete;
    private  double freqTweetperDay;
    private  double distBetwTweet;
    private double reputation;
    private boolean isAtypique = false;

    public User(long id, String name, int followers_count, int friends_count, Date created_at,String descr) {
        this.idUser = id;
        this.nameUser = name;
        this.descriptionProfil = descr;
        this.numberOfFollowers = followers_count;
        this.numberOfFriend =friends_count;
        this.creationDate = created_at;
        this.tweetsList = new ArrayList<Tweet>();

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
        if(tweetsList.size() >0 ){
            moyUrlPerTweet = totalUrl/tweetsList.size();
        }
        else{
            moyUrlPerTweet = 0;
        }

        return moyUrlPerTweet;
    }

    public double getMoyLikesPerTweet(){
        int totalLike =0;
        for(int i=0;i<tweetsList.size();i++){
            totalLike += tweetsList.get(i).numberOfLikes;
        }

        if(tweetsList.size() >0 ){
            moyLikePerTweet = totalLike/tweetsList.size();
        }
        else{
            moyLikePerTweet = 0;
        }

        return moyLikePerTweet;
    }

    public double getMoyMentionPerTweet() {
        int totalMentionNumber = 0;
        for(int i=0;i<tweetsList.size();i++){
            totalMentionNumber += tweetsList.get(i).numberOfMention;
        }
        if(tweetsList.size() >0 ){
            moyMentionPerTweet= totalMentionNumber/tweetsList.size();
        }
        else {
            moyMentionPerTweet = 0;
        }
        return moyMentionPerTweet;
    }

    public double getMoyHashtagPerTweet() {
        int totalHashtagNumber = 0;
        for(int i=0;i<tweetsList.size();i++){
            totalHashtagNumber += tweetsList.get(i).numberOfHashTags;
        }
        if(tweetsList.size() >0 ){
            moyHashtagPerTweet= totalHashtagNumber/tweetsList.size();
        }
        else{
            moyHashtagPerTweet = 0;
        }
        return moyHashtagPerTweet;

    }
   
    public double getFreqTweetperDay() {
    	HashMap<String, List<Tweet>> hashMap = new HashMap<String, List<Tweet>>();
    	
    	for(Tweet tweet:tweetsList){
            Calendar cal = Calendar.getInstance();
            cal.setTime(tweet.getTweetDate());
            String dateConcat =cal.get(Calendar.DAY_OF_MONTH)+""+cal.get(Calendar.DAY_OF_WEEK)+""+cal.get(Calendar.YEAR);
            
            if (!hashMap.containsKey(dateConcat)) {
                List<Tweet> list = new ArrayList<Tweet>();
                list.add(tweet);

                hashMap.put(dateConcat, list);
            } else {
                hashMap.get(dateConcat).add(tweet);
            }            
        }
    	
    	int total = 0; 
    	for (Map.Entry<String, List<Tweet>> entry : hashMap.entrySet()) { 
    		total += entry.getValue().size(); 
    	}
        
        freqTweetperDay = total/hashMap.size();

        return freqTweetperDay;
    }


    public long getIdUser() {
        return idUser;
    }
    public String getNameUser() {
        return nameUser;
    }

    public int getSizeName(){
        return this.nameUser.length();
    }

    public int getSizeDescr(){
        return this.descriptionProfil.length();
    }

    public ArrayList<Tweet> getTweetsList() {
        return tweetsList;
    }

    public int getNumberOfTweet() {
    	numberOfTweet = tweetsList.size();
        return numberOfTweet;
    }

    public double getReputation() {
        return reputation;
    }

    public double getRatioFollow(){
        if(numberOfFriend > 0){
            return numberOfFollowers/numberOfFriend;
        }else{
            return 0;
        }

    }


	public boolean isAtypique() {
		return isAtypique;
	}


	public void setAtypique(boolean isAtypique) {
		this.isAtypique = isAtypique;
	}

}
