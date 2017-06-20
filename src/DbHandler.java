import java.io.IOException;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jdk.nashorn.internal.parser.JSONParser;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbClientBase;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;

public class DbHandler {

    public DbHandler() {
    }

    public void testing() {

        CouchDbClientBase db = new CouchDbClient("if25_tweets", true, "http", "localhost", 5984, "root", "root");
        //CouchDbClient dbClient = new CouchDbClient("if25_tweets", true, "http", "localhost", 5984, "anta", "password");

/*		System.out.print(db.view("_all_docs")
                .reduce(false)
				.limit(10)
				.includeDocs(true)
				.query(Object.class));*/

       HashMap<Long, User> userList = new HashMap<>();
        List<JsonObject> jsonUserList = db.view("userview/users-view").query(JsonObject.class);

        // Ci-dessous on supprime les doublons
        Set<JsonObject> hs = new HashSet<>();
        hs.addAll(jsonUserList);
        jsonUserList.clear();
        jsonUserList.addAll(hs);
        for (int i = 0; i < jsonUserList.size() ; i++) {
            JsonObject jsonObject = jsonUserList.get(i).get("key").getAsJsonObject();
           // System.out.println(jsonObject.get("created_at").toString());
            User user = new User(
                    jsonObject.get("id").getAsLong(),
                    jsonObject.get("screen_name").getAsString(),
                    jsonObject.get("followers_count").getAsInt(),
                    jsonObject.get("friends_count").getAsInt(),
                    new GregorianCalendar(2017, Calendar.MAY, 31).getTime(),
                    jsonObject.get("description").getAsString()
            );
            userList.put(jsonObject.get("id").getAsLong(),user);
            //User(long id, String name, int followers_count, int friends_count, Date created_at,String descr)
            //System.out.println(jsonObject.get("id"));

        }

        List<JsonObject> jsonTweetList = db.view("_all_docs")
                .reduce(false)
                .includeDocs(true)
                .query(JsonObject.class);

        for (int i = 0; i < jsonTweetList.size() ; i++) {
           // System.out.println(jsonTweetList.get(i).get("docs"));
            if(jsonTweetList.get(i).get("docs") != null){
                JsonObject jsonObject = jsonTweetList.get(i).get("docs").getAsJsonObject();
                boolean isRetweet = true;
                if(jsonObject.get("in_reply_to_status_id") == null){
                    isRetweet = false;
                }
                Tweet tweet = new Tweet(
                        jsonObject.get("entities").getAsJsonObject().get("urls").getAsJsonArray().size(),
                        jsonObject.get("entities").getAsJsonObject().get("hashtags").getAsJsonArray().size(),
                        jsonObject.get("entities").getAsJsonObject().get("user_mentions").getAsJsonArray().size(),
                        jsonObject.get("text").getAsString(),
                        isRetweet,
                        new GregorianCalendar(2017, Calendar.MAY, 31).getTime(),
                        jsonObject.get("favorite_count").getAsInt()
                );
                Long idUSer = jsonObject.get("user").getAsJsonObject().get("id").getAsLong();
                if( userList.get(idUSer) != null){
                    User user  = userList.get(idUSer);
                    userList.get(idUSer).getTweetsList().add(tweet);
                }
            }
        }




creerBdAttributs(userList);

  /*      FichierCSV csv = new FichierCSV(userList);
        try {
            csv.creerFichierCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public void creerBdAttributs(HashMap<Long, User> userList){

        CouchDbClientBase db = new CouchDbClient("if25_attributs", true, "http", "localhost", 5984, "root", "root");
        boolean isAtypique = false;
        for (Map.Entry<Long,User> e : userList.entrySet()){
            System.out.println(e.getKey() + " : " + e.getValue());

            User user = e.getValue();
            if(user.getTweetsList().size() >10){
                Map<String, Object> map = new HashMap<>();
                map.put("idUser", user.getIdUser());
                map.put("sizeProfilName", user.getSizeName());
                map.put("sizeDescr",user.getSizeDescr());
                map.put("tweetsNumber",user.getTweetsList().size());
                map.put("userAgeInDay",user.getAgeInDay());
                map.put("ratioFollowingOnFollower",user.getRatioFollow());
                map.put("followingNumber",user.getNumberOfFriend());
                map.put("followersNumber",user.getNumberOfFollowers());
                map.put("freqTweetPerDay",user.getFreqTweetperDay());
                map.put("hachagsAvg", user.getMoyHashtagPerTweet());
                map.put("mentionAvg", user.getMoyMentionPerTweet());
                map.put("urlAvg",user.getMoyUrlPerTweet());
                map.put("isAtypique", isAtypique);
                db.save(map);
            }
        }

    }

}
