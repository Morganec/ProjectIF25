import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbClientBase;
import org.lightcouch.CouchDbContext;


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
                    userList.get(idUSer).getNumberOfTweet();
                    userList.get(idUSer).getAgeInDay();
                    userList.get(idUSer).getMoyUrlPerTweet();
                    userList.get(idUSer).getMoyLikesPerTweet();
                    userList.get(idUSer).getMoyMentionPerTweet();
                    userList.get(idUSer).getFreqTweetperDay();                 
                }
            }
        }

        HashMap<Long, User>  newUserList = createAttributDB(userList); //do below on the method and return the list straight        
        
        //Check for the optimal K        
        System.out.println("Ks"+ getOptimalK(newUserList));
        
        //Testing our method
       // User myUser = getRandomUser(newUserList);
        //System.out.println(isAtypic(myUser,newUserList, 5));
    }
	
	// Creating the database with the users we kept
	public HashMap<Long, User> createAttributDB(HashMap<Long, User> userList)
	{
        CouchDbClientBase db = new CouchDbClient("if25_attributs", true, "http", "localhost", 5984, "root", "root");  
        
        HashMap<Long, User> newUserList = new HashMap<>();

        for (Map.Entry<Long,User> e : userList.entrySet())
        {
            User user = e.getValue();
            //Filtering and only keep user with more than 10 tweets
            if (user.getTweetsList().size() >10) {
                //Setting our users as atypic or not
	            if( checkAvgTweetLinks(user.getIdUser(),userList) 
	            		 && checkAvgTweetHashtags(user.getIdUser(),userList) 
	            		 && checkAvgTweetMentions(user.getIdUser(),userList) ){                    
	            	user.setAtypique(true);
	            	System.out.println("atypic !");
	            }
	            else{
	            	user.setAtypique(false);
	            }       
	            newUserList.put(user.getIdUser(), user);
	            db.save(user);
            }            
        }
        return newUserList;
    }

	/** Check if the user is "popular" **/
	public boolean isPopular(User user){        
        if(user.getRatioFollow() < 2 && user.getRatioFollow() > 0){
            return true;
        }
        return false;
    }
	
	/** Check if the user is "young" **/
	public boolean checkAgeInDays(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Long> listAge = new ArrayList<>();
        userList.putAll(usList);
        listAge.addAll(userList.entrySet().stream().map(e -> e.getValue().getAgeInDay()).collect(Collectors.toList()));
        Collections.sort(listAge);
        int limit_value = listAge.size() * 40/100;
        ArrayList<Long> listAge2 = new ArrayList<>();
        listAge2.addAll(listAge.stream().limit(30).collect(Collectors.toList()));
        if(listAge2.contains(userList.get(idUser).getAgeInDay())){
            return true;
        }
        return false;
    }

	/** Check if the user is part of serial linkers **/
	public boolean checkAvgTweetLinks(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Double> listLink = new ArrayList<>();
        userList.putAll(usList);
        listLink.addAll(userList.entrySet().stream().map(e -> e.getValue().getMoyUrlPerTweet()).collect(Collectors.toList()));
        Collections.sort(listLink, Collections.reverseOrder());
        ArrayList<Double> listLink2 = new ArrayList<>();
        int limit_value = listLink.size() * 30/100;
        listLink2.addAll(listLink.stream().limit(limit_value).collect(Collectors.toList()));
        if(listLink2.contains(userList.get(idUser).getRatioFollow())){
            return true;
        }
        return false;
    }
	
	/** Check if the user is part of serial hashtagers **/
	public boolean checkAvgTweetHashtags(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Double> listHashtag = new ArrayList<>();
        userList.putAll(usList);
        listHashtag.addAll(userList.entrySet().stream().map(e -> e.getValue().getMoyHashtagPerTweet()).collect(Collectors.toList()));
        Collections.sort(listHashtag, Collections.reverseOrder());
        ArrayList<Double> listHashtag2 = new ArrayList<>();
        int limit_value = listHashtag.size() * 30/100;
        listHashtag2.addAll(listHashtag.stream().limit(limit_value).collect(Collectors.toList()));
        if(listHashtag2.contains(userList.get(idUser).getRatioFollow())){
            return true;
        }
        return false;
    }
	
	/** Check if the user is part of serial mentioners **/
	public boolean checkAvgTweetMentions(long idUser, HashMap<Long, User> usList){
        HashMap<Long, User> userList = new HashMap<>();
        ArrayList<Double> listMention = new ArrayList<>();
        userList.putAll(usList);
        listMention.addAll(userList.entrySet().stream().map(e -> e.getValue().getMoyMentionPerTweet()).collect(Collectors.toList()));
        Collections.sort(listMention, Collections.reverseOrder());
        ArrayList<Double> listMention2 = new ArrayList<>();
        int limit_value = listMention.size() * 30/100;
        listMention2.addAll(listMention.stream().limit(limit_value).collect(Collectors.toList()));
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
        
        for (Map.Entry<Long, User> entry : userDataset.entrySet()) { 
        	User user = entry.getValue();
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
        HashMap<Long,Double> result = new HashMap<>();
        result.putAll(closestUserIds.entrySet().stream()
                 .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                 .limit(k)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new)));
        
        //Getting the corresponding users
        for(Map.Entry<Long,Double> e : result.entrySet()){
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

        for (Map.Entry<Long, User> entry : nearestNeighbors.entrySet()) { 
        	User user = entry.getValue();
        	if(user.isAtypique())
        		nombreAtypique++;
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
	
	/** Checking for the optimal value for K**/
	public ArrayList<Double> getOptimalK(HashMap<Long, User> userList)
	{
		ArrayList<Double> error_list = new ArrayList<>();
		
		int k = 1;
		int listLength = userList.size();
		
		while(k < listLength)
		{
			int nb_errors = 0;
			
			for (Map.Entry<Long, User> entry : userList.entrySet()) { 
				User user = entry.getValue();
				boolean test = isAtypic(user, userList, k);
				if(test != user.isAtypique())
					nb_errors++;
			}
			error_list.add((double)nb_errors);
			k++;
		}
		return error_list;
	}

}
