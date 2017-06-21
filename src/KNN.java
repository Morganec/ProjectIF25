import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbClientBase;


public class KNN {
	
	public KNN() {}
	
	public void mainMethod() {

        /** Accessing the database with the raw tweets **/
		CouchDbClientBase db = new CouchDbClient("if25_tweets", true, "http", "localhost", 5984, "root", "root");
		
		//Getting all the users (from the view)
		HashMap<Long, User> userList = new HashMap<>();
        List<JsonObject> jsonUserList = db.view("userview/users-view").query(JsonObject.class);
        
        // Deleting the duplicates 
        Set<JsonObject> hs = new HashSet<>();
        hs.addAll(jsonUserList);
        jsonUserList.clear();
        jsonUserList.addAll(hs);
        
        // Creating the User objects
        for (int i = 0; i < jsonUserList.size() ; i++) {
            JsonObject jsonObject = jsonUserList.get(i).get("key").getAsJsonObject();

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
        }
        
        // Getting all the tweets from the DB
        List<JsonObject> jsonTweetList = db.view("_all_docs")
                .reduce(false)
                .includeDocs(true)
                .query(JsonObject.class);
        
        // Creating the Tweet objects and linking them with corresponding users
        for (int i = 0; i < jsonTweetList.size() ; i++) {
            if(jsonTweetList.get(i).get("docs") != null){
                JsonObject jsonObject = jsonTweetList.get(i).get("docs").getAsJsonObject();
                boolean isRetweet = true;
                if(jsonObject.get("in_reply_to_status_id") == null){
                    isRetweet = false;
                }
                Date datecreation = convertStringToDate(jsonObject.get("created_at").getAsString());
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

        HashMap<Long, User>  newUserList = createAttributDB(userList); //do below on the method and return the list straight        
        
        //Long idUser = getRandomIdUser(userList);
        User myUser = getRandomUser(newUserList);
        isAtypic(myUser,newUserList, 5);
    }
	
	// Creating the database with the users we kept
	public HashMap<Long, User> createAttributDB(HashMap<Long, User> userList)
	{
        CouchDbClientBase db = new CouchDbClient("if25_attributs", true, "http", "localhost", 5984, "root", "root");
        
        for (Map.Entry<Long,User> e : userList.entrySet())
        {
            User user = e.getValue();
            if(isYoung(user.getIdUser(),userList) ){                    
            	user.setAtypique(true);
            }else{
            	user.setAtypique(false);
            }
            if(user.getTweetsList().size() >10){
                db.save(user);
            }
            
        }
        return userList;
    }
	
	/** Check users age in days**/
	public boolean isYoung(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Long> listAge = new ArrayList<>();
        userList.putAll(usList);
        listAge.addAll(userList.entrySet().stream().map(e -> e.getValue().getAgeInDay()).collect(Collectors.toList()));
        Collections.sort(listAge);
        ArrayList<Long> listAge2 = new ArrayList<>();
        listAge2.addAll(listAge.stream().limit(30).collect(Collectors.toList()));
        if(listAge2.contains(userList.get(idUser).getAgeInDay())){
            return true;
        }
        return false;
    }
	
	/** Get users following/follower ratio **/
	public boolean followRation(long idUser, HashMap<Long, User> usList){
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
	
	/** Get users followers number **/
	public boolean nbFollowers(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        userList.putAll(usList);
        if(userList.get(idUser).getNumberOfFollowers() == 0){
            return true;
        }
        return false;
    }
	
	/** Get users average number of links in tweets **/
	public boolean avgTweetLinks(long idUser, HashMap<Long, User> usList){
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
	
	/** Get users average number of hashtags in tweets **/
	public boolean avgTweetHashtags(long idUser, HashMap<Long, User> usList){
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
	
	/** Get users average number of mentions in tweets **/
	public boolean avgTweetMentions(long idUser, HashMap<Long, User> usList){
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
	
	/** Get entry user closest k neighbors **/
	public  HashMap<Long,User> kNearestNeighbors(User newUser, HashMap<Long, User> userDataset, int k ){
                HashMap<Long,User> nearestNeighbors = new HashMap<>();
        
        //Getting the attributes we will focus on
        double xNouvelEntre = newUser.getMoyMentionPerTweet();
        double yNouvelEntre = newUser.getMoyHashtagPerTweet();
        double zNouvelEntre = newUser.getMoyUrlPerTweet();
        
        //Getting the closest users Ids 
        HashMap<Long,Double> closestUserIds = new HashMap<>(); 
        int datasetLength = userDataset.size() ;
        
        for(int i = 0 ; i< datasetLength ; i++){
            User user = userDataset.get(i);
            double x = user.getMoyMentionPerTweet();
            double y = user.getMoyHashtagPerTweet();
            double z = user.getMoyUrlPerTweet();
            
            //Pythagore Theorem to get the distance between two point in space
            Double distance = Math.sqrt( Math.abs((Math.pow(xNouvelEntre-x,2))) +
                    Math.abs((Math.pow(yNouvelEntre-y,2))) +  Math.abs((Math.pow(zNouvelEntre-z,2)))
            );

            closestUserIds.put(user.getIdUser(),distance);

        }

        closestUserIds.remove(newUser.getIdUser());
        closestUserIds.entrySet().stream()
                 .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                 .limit(k)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
        
        //Getting the corresponding users
        for(Map.Entry<Long,Double> e : closestUserIds.entrySet()){
        	nearestNeighbors.put(e.getKey(),userDataset.get(e.getKey()));
        }
        
        return nearestNeighbors;
        
    }
	
	/** Check if a users as atypical **/
	public boolean isAtypic(User newUser, HashMap<Long, User> dataset, int k )
	{
        boolean isAtypic = false;
        int nombreAtypique = 0;
        
        //Get the k closest neighbors
        HashMap<Long, User> nearestNeighbors = kNearestNeighbors(newUser, dataset, k);
        
        for(int i = 0 ; i< dataset.size() ; i++){
            User user = dataset.get(i);
            if(  nearestNeighbors.containsKey(user.getIdUser()) ){
               if(user.isAtypique()){
                   nombreAtypique++;
               }
           }
        }

        //Check the majority type
        if(nombreAtypique >= (k/2))
        	isAtypic = true;
        
        return  isAtypic;
    }
	
	public User getRandomUser(HashMap<Long, User> usList){
        Random generator = new Random();
        Object[] values = usList.values().toArray();
        User randomUser = (User) values[generator.nextInt(values.length)];
        return randomUser;
    }
	
	/** method to convert string into a date object **/
	public Date convertStringToDate(String dateString){
        java.util.Calendar cal = java.util.Calendar.getInstance();
        
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy",Locale.ENGLISH);
        Date parsedDate = null;
        try {
            parsedDate = formatter.parse(dateString.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }
	
	/****/
	public int getOptimalK(HashMap<Long, User> userList)
	{
		int k = 1;
		int optimalK = 1;
		int listLength = userList.size();

		int min_errors = listLength;
		int iterator = 0;
		while(k < listLength)
		{
			int nb_errors = 0;
			for(int i = 0; i < listLength; i++){
				User user = userList.get(iterator);
				boolean test = isAtypic(user, userList, k);
				if(test != user.isAtypique())
					nb_errors++;
			}
			if(nb_errors < min_errors){
				min_errors = nb_errors;
				optimalK = k;
			}
			k++;
		}
		return k;
	}

}
