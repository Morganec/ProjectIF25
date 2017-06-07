import java.util.Date;

/**
 * Created by morgane on 02/06/17.
 */
public class Tweet {
    int numberOfURL;
    int numberOfHashTags;
    int numberOfMention;
    int numberOfRetweet;
    boolean isARetweet;



    Date tweetDate;


    int charactersNumber;
    String tweetText;
    public Tweet(int urlsSize, int hastagsSize, int mentionSize, String text, boolean retweeted_status, Date created_at) {
        this.numberOfURL = urlsSize;
        this.numberOfHashTags= hastagsSize;
        this.numberOfMention = mentionSize;
        this.tweetText = text;
        this.isARetweet = retweeted_status;
        this.tweetDate = created_at;
    }

    public int getCharactersNumber() {
        charactersNumber = tweetText.length();
        return charactersNumber;
    }

    public Date getTweetDate() {
        return tweetDate;
    }
}

