import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

Date datecreation = convertStringToDate(jsonObject.get("created_at").getAsString());
            User user = new User(
                    jsonObject.get("id").getAsLong(),
                    jsonObject.get("screen_name").getAsString(),
                    jsonObject.get("followers_count").getAsInt(),
                    jsonObject.get("friends_count").getAsInt(),
                    datecreation,
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
                Date datecreation = convertStringToDate(jsonObject.get("created_at").getAsString());
               // System.out.println(datecreation.getTime());
                Tweet tweet = new Tweet(
                        jsonObject.get("entities").getAsJsonObject().get("urls").getAsJsonArray().size(),
                        jsonObject.get("entities").getAsJsonObject().get("hashtags").getAsJsonArray().size(),
                        jsonObject.get("entities").getAsJsonObject().get("user_mentions").getAsJsonArray().size(),
                        jsonObject.get("text").getAsString(),
                        isRetweet,
                        datecreation,
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
        Long idUser = getRandomIdUser(userList);
      boolean bool = estAtypique(idUser,userList);
        int a = 1;

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
            //System.out.println(e.getKey() + " : " + e.getValue());

            User user = e.getValue();

            if(estJeune(user.getIdUser(),userList) ){
                    //&& bigRatioFollowers(user.getIdUser(),userList) && bigTweetHashtag(user.getIdUser(),userList )&& bigTweetLink(user.getIdUser(),userList) && bigTweetMention(user.getIdUser(),userList)){
                isAtypique = true;
            }else{
                isAtypique = false;
            }
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
            this.estJeune(user.getIdUser(),userList);
        }



    }


    public boolean estJeune(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Long> listAge = new ArrayList<>();
        userList.putAll(usList);
        listAge.addAll(userList.entrySet().stream().map(e -> e.getValue().getAgeInDay()).collect(Collectors.toList()));
      Collections.sort(listAge);
       // System.out.println(listAge);
        ArrayList<Long> listAge2 = new ArrayList<>();
        listAge2.addAll(listAge.stream().limit(30).collect(Collectors.toList()));
        if(listAge2.contains(userList.get(idUser).getAgeInDay())){
            return true;
        }
        return false;
    }

    public boolean bigRatioFollowers(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Double> listRatio = new ArrayList<>();
        userList.putAll(usList);
        listRatio.addAll(userList.entrySet().stream().map(e -> e.getValue().getRatioFollow()).collect(Collectors.toList()));
        Collections.sort(listRatio, Collections.reverseOrder());
       // System.out.println(listRatio);
        ArrayList<Double> listRatio2 = new ArrayList<>();
        listRatio2.addAll(listRatio.stream().limit(30).collect(Collectors.toList()));
        if(listRatio2.contains(userList.get(idUser).getRatioFollow())){
            return true;
        }
        return false;
    }

    public boolean noFollower(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        userList.putAll(usList);
        if(userList.get(idUser).getNumberOfFollowers() == 0){
            return true;
        }
        return false;
    }

    public boolean bigTweetLink(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Double> listLink = new ArrayList<>();
        userList.putAll(usList);
        listLink.addAll(userList.entrySet().stream().map(e -> e.getValue().getMoyUrlPerTweet()).collect(Collectors.toList()));
        Collections.sort(listLink, Collections.reverseOrder());
        ArrayList<Double> listLink2 = new ArrayList<>();
        listLink2.addAll(listLink.stream().limit(30).collect(Collectors.toList()));
        if(listLink2.contains(userList.get(idUser).getRatioFollow())){
            return true;
        }
        return false;
    }

    public boolean bigTweetHashtag(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Double> listHashtag = new ArrayList<>();
        userList.putAll(usList);
        listHashtag.addAll(userList.entrySet().stream().map(e -> e.getValue().getMoyHashtagPerTweet()).collect(Collectors.toList()));
        Collections.sort(listHashtag, Collections.reverseOrder());
        ArrayList<Double> listHashtag2 = new ArrayList<>();
        listHashtag2.addAll(listHashtag.stream().limit(30).collect(Collectors.toList()));
        if(listHashtag2.contains(userList.get(idUser).getRatioFollow())){
            return true;
        }
        return false;
    }

    public boolean bigTweetMention(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Double> listMention = new ArrayList<>();
        userList.putAll(usList);
        listMention.addAll(userList.entrySet().stream().map(e -> e.getValue().getMoyMentionPerTweet()).collect(Collectors.toList()));
        Collections.sort(listMention, Collections.reverseOrder());
        ArrayList<Double> listMention2 = new ArrayList<>();
        listMention2.addAll(listMention.stream().limit(30).collect(Collectors.toList()));
        if(listMention2.contains(userList.get(idUser).getRatioFollow())){
            return true;
        }
        return false;
    }

    public Date convertStringToDate(String dateString){
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String dateInString = new java.text.SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy",Locale.ENGLISH).format(cal.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy",Locale.ENGLISH);
        Date parsedDate = null;
        try {
            parsedDate = formatter.parse(dateString.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }


    public boolean estAtypique(Long idUser, HashMap<Long, User> usList ){
        boolean estAtypique = false;
        int k = 5;
        HashMap<Long, User> listVoisin = getVoisionProche(k,idUser,usList);
        int nombreAtypique = 0;
        CouchDbClientBase db = new CouchDbClient("if25_attributs", true, "http", "localhost", 5984, "root", "root");
        List<JsonObject> jsonAttributsList = db.view("_all_docs")
                .reduce(false)
                .includeDocs(true)
                .query(JsonObject.class);
        for(int i = 0 ; i< jsonAttributsList.size() ; i++){
            JsonObject jsonObject = jsonAttributsList.get(i);
           if(  listVoisin.containsKey(jsonObject.get("idUser").getAsLong()) ){
               if(jsonObject.get("isAtypique").getAsBoolean()){
                   nombreAtypique++;
               }
           }
        }

        if(nombreAtypique >= (k/2)){
            estAtypique =true;
        }else{
            estAtypique = false;
        }

        return  estAtypique;
    }

    public HashMap<Long, User> getVoisionProche(int k,Long idUser, HashMap<Long, User> usList){
        HashMap<Long,User> userList = new HashMap<>();
        HashMap<Long,User> userListVoisin = new HashMap<>();
        userList.putAll(usList);
        User monUser = userList.get(idUser);
        userList.remove(monUser.getIdUser(),monUser);
        if(k == userList.size()){

            return userList;
        }else{
                HashMap<Long,Double> userVoisin = getProcheUser(monUser,k);
                for(Map.Entry<Long,Double> e : userVoisin.entrySet()){
                    userListVoisin.put(e.getKey(),userList.get(e.getKey()));
                }
        }
        return userListVoisin;

    }

    public  HashMap<Long,Double> getProcheUser(User userATrouv, int k ){
        CouchDbClientBase db = new CouchDbClient("if25_attributs", true, "http", "localhost", 5984, "root", "root");
        List<JsonObject> jsonAttributsList = db.view("_all_docs")
                .reduce(false)
                .includeDocs(true)
                .query(JsonObject.class);
        User userATrouver = userATrouv;
        double xNouvelEntre = userATrouv.getMoyMentionPerTweet();
        double yNouvelEntre = userATrouv.getMoyHashtagPerTweet();
        double zNouvelEntre = userATrouv.getMoyUrlPerTweet();
        HashMap<Long,Double> listIdUserEtDistance = new HashMap<>();

        for(int i = 0 ; i< jsonAttributsList.size() ; i++){
            JsonObject jsonObject = jsonAttributsList.get(i);
            double x = jsonObject.get("mentionAvg").getAsDouble();
            double y = jsonObject.get("hachagsAvg").getAsDouble();
            double z = jsonObject.get("urlAvg").getAsDouble();


            Double distance = Math.sqrt( Math.abs((Math.pow(xNouvelEntre-x,2))) +
                    Math.abs((Math.pow(yNouvelEntre-y,2))) +  Math.abs((Math.pow(zNouvelEntre-z,2)))
            );

            listIdUserEtDistance.put(jsonObject.get("idUser").getAsLong(),distance);

        }

listIdUserEtDistance.remove(userATrouv.getIdUser());
        return listIdUserEtDistance.entrySet().stream()
                 .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                 .limit(k)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }


    public long getRandomIdUser(HashMap<Long, User> usList){
        HashMap<Long,User> userList = new HashMap<>();
        userList.putAll(usList);
        ArrayList<Long> listID = new ArrayList<>();
        listID.addAll(userList.keySet());
        CouchDbClientBase db = new CouchDbClient("if25_attributs", true, "http", "localhost", 5984, "root", "root");
        List<JsonObject> jsonAttributsList = db.view("_all_docs")
                .reduce(false)
                .includeDocs(true)
                .query(JsonObject.class);

        Random r = new Random();
        int valeur = 0 + r.nextInt(jsonAttributsList.size() - 0);
        JsonObject randomObject = jsonAttributsList.get(valeur);
        System.out.print(randomObject.get("tweetsNumber").getAsString());
        return randomObject.get("idUser").getAsLong();
    }

}
