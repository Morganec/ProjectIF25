import com.mkyong.utils.CSVUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by morgane on 16/06/17.
 */
public class FichierCSV {
    private final static String FILE_NAME = "src/fichierSauvegarde/sauvegarde.csv";
    HashMap<Long,User> userList = new HashMap<>();
    public FichierCSV(HashMap<Long,User> listeUser) {
        this.userList.putAll(listeUser);
    }

    public boolean creerFichierCsv() throws IOException {

        String csvFile = "src/fichierSauvegarde/save.csv";
        FileWriter writer = new FileWriter(csvFile);





        CSVUtils.writeLine(writer, Arrays.asList("ID user ","Length of proﬁle name", "Length of proﬁle description", "Number of tweets posted","Age of the user account, in days","Ratio of number of followings and followers","Number of following","Number of followers","Frequence of tweets posted per day","hashtags average","mentions average","links average", "Likes average per tweet"), ';', '"');

        for(User user:userList.values()){

            CSVUtils.writeLine(writer, Arrays.asList(user.getIdUser()+"",
                    user.getSizeName()+"",
                    user.getSizeDescr()+"",
                    user.getTweetsList().size()+"",
                    user.getAgeInDay()+"",
                    Double.toString(user.getRatioFollow()).replace(".", ","),
                    user.getNumberOfFriend()+"",
                    user.getNumberOfFollowers()+"",
                    Double.toString(user.getFreqTweetperDay()).replace(".", ","),
                    Double.toString(user.getMoyHashtagPerTweet()).replace(".", ","),
                    Double.toString(user.getMoyMentionPerTweet()).replace(".", ","),
                    Double.toString(user.getMoyUrlPerTweet()).replace(".", ","),
                    Double.toString(user.getMoyLikesPerTweet()).replace(".", ",")), ';', '"');

        }




        writer.flush();
        writer.close();

        return true;
    }



}
