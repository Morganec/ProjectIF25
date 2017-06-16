import com.mkyong.utils.CSVUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by morgane on 16/06/17.
 */
public class FichierCSV {
    private final static String FILE_NAME = "src/fichierSauvegarde/sauvegarde.csv";
    ArrayList<User> userList = new ArrayList<>();
    public FichierCSV(ArrayList<User> listeUser) {
        this.userList.addAll(listeUser);
    }

    public boolean creerFichierCsv() throws IOException {

        String csvFile = "src/fichierSauvegarde/save.csv";
        FileWriter writer = new FileWriter(csvFile);

        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bb,b", "cc,c"), ';', '"');

        for(User user:userList){

            //custom separator + quote
            CSVUtils.writeLine(writer, Arrays.asList("aaa", "bb,b", "cc,c"), ';', '"');
        }




        writer.flush();
        writer.close();

        return true;
    }



}
