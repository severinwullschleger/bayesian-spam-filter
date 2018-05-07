import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
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
        for (int i = 0; i < initialFiles.length; i++) {
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
        if (!spamTrainingDirectory.exists()) {
            System.out.println("ERR: The Spam Training Directory does not exist");
            return;
        }

        File hamTrainingDirectory = new File(hamTrainingFolder);
        if (!hamTrainingDirectory.exists()) {
            System.out.println("ERR: The Ham Training Directory does not exist");
            return;
        }

        int numberOfFiles = 0;
        File spamFiles[] = filterFiles(spamTrainingDirectory.listFiles());
        Hashtable<String, Integer> spamWordsCount = new Hashtable();
        Hashtable<String, Integer> spamFileCount = new Hashtable();
        Hashtable<String, Double> spamRatio_W_S;


        for (File f : spamFiles) {
            /*
                TODO
             */

            addTotalWordCountToHashtable(f, spamWordsCount);
            addFileCountToHashtable(f, spamFileCount);

            numberOfFiles++;

        }
        spamRatio_W_S = calculateRatio(spamFileCount, numberOfFiles);

        System.out.println(numberOfFiles + " files found in spam training folder");

        numberOfFiles = 0;
        File hamFiles[] = filterFiles(hamTrainingDirectory.listFiles());
        Hashtable<String, Integer> hamWordsCount = new Hashtable();
        Hashtable<String, Integer> hamFileCount = new Hashtable();
        Hashtable<String, Double> hamRatio_W_H;
        for (File f : hamFiles) {
            /*
                TODO
             */

            addTotalWordCountToHashtable(f, hamWordsCount);
            addFileCountToHashtable(f, hamFileCount);

            numberOfFiles++;
        }
        hamRatio_W_H = calculateRatio(hamFileCount, numberOfFiles);
        System.out.println(numberOfFiles + " files found in ham training folder");

        Hashtable<String, Double> spamicity = calculateSpamicity(spamRatio_W_S, hamRatio_W_H);
        System.out.println(spamicity);

        System.out.println(Collections.max(spamicity.values()));
        System.out.println(Collections.min(spamicity.values()));
        System.out.println(spamicity.values().size());

    }

    private void addTotalWordCountToHashtable(File f, Hashtable<String, Integer> words) {
        try {
            for (String s : Files.readAllLines(Paths.get(f.getAbsolutePath()))) {
                for (String word : s.split(" ")) {
                    Integer value = words.get(word);
                    if (value == null)
                        words.put(word, 1);
                    else
                        words.put(word, value + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFileCountToHashtable(File f, Hashtable<String, Integer> words) {
        try {
            Hashtable<String, Integer> tempHashtable = new Hashtable<>();
            for (String s : Files.readAllLines(Paths.get(f.getAbsolutePath()))) {
                for (String word : s.split(" ")) {
                    tempHashtable.put(word, 1);
                }
            }

            for (String word : tempHashtable.keySet()) {
                Integer value = words.get(word);
                if (value == null)
                    words.put(word, 1);
                else
                    words.put(word, value + 1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Hashtable<String, Double> calculateRatio(Hashtable<String, Integer> hashtable, int numberOfFiles) {
        Hashtable<String, Double> spamRatio_W_S = new Hashtable<>();
        for (String word : hashtable.keySet())
            spamRatio_W_S.put(word, (double) hashtable.get(word) / (double) numberOfFiles);
        return spamRatio_W_S;
    }

    private Hashtable<String, Double> calculateSpamicity(Hashtable<String, Double> spamRatio_w_s, Hashtable<String, Double> hamRatio_w_h) {
        double p_s = 0.5;
        double p_h = 0.5;

        Hashtable<String, Double> spamicity_s_w = new Hashtable<>();

        for (String word : spamRatio_w_s.keySet()) {
            if (hamRatio_w_h.get(word) != null) {
                double spamicity = (spamRatio_w_s.get(word) * p_s) / (spamRatio_w_s.get(word) * p_s + hamRatio_w_h.get(word) * p_h);
                spamicity_s_w.put(word, spamicity);
            } else
                spamicity_s_w.put(word, 1.0);
        }

        for (String word : hamRatio_w_h.keySet()) {
            if (spamRatio_w_s.get(word) == null)
                spamicity_s_w.put(word, 1.0);
        }

        return spamicity_s_w;
    }

    public void test(String spamTestingFolder, String hamTestingFolder) {

        File spamTestingDirectory = new File(spamTestingFolder);
        if (!spamTestingDirectory.exists()) {
            System.out.println("ERR: The Spam Testing Directory does not exist");
            return;
        }

        File hamTestingDirectory = new File(hamTestingFolder);
        if (!hamTestingDirectory.exists()) {
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

        System.out.println("###_DO_NOT_USE_THIS_###Spam = " + allSpam);
        System.out.println("###_DO_NOT_USE_THIS_###Ham = " + allHam);
        System.out.println("###_DO_NOT_USE_THIS_###SpamClassifAsHam = " + SpamClassifiedAsHam);
        System.out.println("###_DO_NOT_USE_THIS_###HamClassifAsSpam = " + HamClassifiedAsSpam);


    }


    public boolean isSpam(File f) {
        /*
        TODO
        implement method
        erase the following "return true" statement
         */
        return true;
    }

}
