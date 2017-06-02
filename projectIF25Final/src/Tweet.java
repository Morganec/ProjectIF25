/**
 * Created by morgane on 02/06/17.
 */
public class Tweet {
    int numberOfURL;
    int numberOfHashTags;
    int numberOfMention;
    int numberOfRetweet;
    boolean isARetweet;


    int charactersNumber;
    String tweetText;
    public Tweet(int urlsSize, int hastagsSize, int mentionSize, String text, boolean retweeted_status) {
        this.numberOfURL = urlsSize;
        this.numberOfHashTags= hastagsSize;
        this.numberOfMention = mentionSize;
        this.tweetText = text;
        this.isARetweet = retweeted_status;
    }

    public int getCharactersNumber() {
        charactersNumber = tweetText.length();
        return charactersNumber;
    }
}

