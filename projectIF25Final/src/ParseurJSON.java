import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Iterator;

/**
 * Created by morgane on 17/06/17.
 */
public class ParseurJSON {
String pathFileName;
    public ParseurJSON(String fileName) {
        this.pathFileName = fileName;

        //"/Users/<username>/Documents/file1.txt"
    }


    public void getDonnee(){
        JSONParser parser = new JSONParser();
        try {



            Object obj = parser.parse(new FileReader(this.pathFileName));

            JSONObject jsonObject = (JSONObject) obj;

            String textTweet = (String) jsonObject.get("text");
            String id = (String) jsonObject.get("id_str");
            JSONObject jsonData =(JSONObject) jsonObject.get("entities");
            JSONArray hashtagsArray = (JSONArray) jsonData.get("hashtags");

            System.out.println("Text tweet : " + textTweet);
            System.out.println("ID tweet : " + id);
            System.out.println("\nHashtags :");
            Iterator<String> iterator = hashtagsArray.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
