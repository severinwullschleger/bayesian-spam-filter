import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: verman
 * Date: 5/4/2014
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class BayesianClassifier {


    public File[] filterFiles(File[] initialFiles) {

        Vector listOfFiles = new Vector();
        for(int i = 0; i<initialFiles.length; i++){
            if (initialFiles[i].getName().endsWith(".txt")) {
                listOfFiles.addElement(initialFiles[i]);
            }
        }

        File[] fileArray = new File[listOfFiles.size()];
        listOfFiles.toArray(fileArray);
        return fileArray;

   }

    public void train(String spamTrainingFolder, String hamTrainingFolder) {

        File spamTrainingDirectory = new File(spamTrainingFolder);
                if (!spamTrainingDirectory.exists()){
            System.out.println("ERR: The Spam Training Directory does not exist");
            return;
        }

        File hamTrainingDirectory = new File(hamTrainingFolder);
        if (!hamTrainingDirectory.exists()){
            System.out.println("ERR: The Ham Training Directory does not exist");
            return;
        }


        File spamFiles[] = filterFiles(spamTrainingDirectory.listFiles());

        
        int numberOfFiles = 0;

        
        for (File f : spamFiles) {
            /*
                TODO
             */
            numberOfFiles++;

        }
        System.out.println(numberOfFiles+" files found in spam training folder");

        numberOfFiles = 0;
        File hamFiles[] = filterFiles(hamTrainingDirectory.listFiles());
        for (File f : hamFiles) {
            /*
                TODO
             */

            numberOfFiles++;
        }
        System.out.println(numberOfFiles+" files found in ham training folder");

    }

    public void test(String spamTestingFolder, String hamTestingFolder) {

        File spamTestingDirectory = new File(spamTestingFolder);
        if (!spamTestingDirectory.exists()){
            System.out.println("ERR: The Spam Testing Directory does not exist");
            return;
        }

        File hamTestingDirectory = new File(hamTestingFolder);
        if (!hamTestingDirectory.exists()){
            System.out.println("ERR: The Ham Testing Directory does not exist");
            return;
        }

        System.out.println("Testing phase:");
        
        int allSpam = 0;
        int SpamClassifiedAsHam = 0; //Spams incorrectly classified as Hams

        File spamFiles[] = filterFiles(spamTestingDirectory.listFiles());
        for (File f : spamFiles) {
            allSpam++;
            if (!isSpam(f))
                SpamClassifiedAsHam++;

        }

        int allHam = 0;
        int HamClassifiedAsSpam = 0; //Hams incorrectly classified as Spams
        
        File hamFiles[] = filterFiles(hamTestingDirectory.listFiles());
        for (File f : hamFiles) {
            allHam++;
            if (isSpam(f))
                HamClassifiedAsSpam++;

        }

        System.out.println("###_DO_NOT_USE_THIS_###Spam = "+allSpam);
        System.out.println("###_DO_NOT_USE_THIS_###Ham = "+allHam);
        System.out.println("###_DO_NOT_USE_THIS_###SpamClassifAsHam = "+SpamClassifiedAsHam);
        System.out.println("###_DO_NOT_USE_THIS_###HamClassifAsSpam = "+HamClassifiedAsSpam);


    }



    public boolean isSpam(File f){
        /*
        TODO
        implement method
        erase the following "return true" statement
         */
       return true;
    }
    
}
