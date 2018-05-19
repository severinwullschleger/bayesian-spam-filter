import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    /**
     * the spamicity threshold defines how many words are kept in the spamicity hashtable with which
     * the spam probability is calculated
     */
    private final double SPAMICITY_THRESHOLD = 0.2;

    /**
     * the threshold which decides if a message is spam or not
     * it is determined from the train data. it is calculated at the end of the train method.
     */
    private double THRESHOLD;

    /**
     * entries in the spamicity hashtable have the word as a key and as value the probability of an email containing
     * this word beeing spam
     * the spamicity hashtable shortened by the reduceAmountOfWords method and the SPAMICITY_THRESHOLD
     */
    private Hashtable<String, Double> spamicity;

    public BayesianClassifier() {
        this.THRESHOLD = 1.0;
    }

    /**
     * training the spamfilter with the files in the training folders
     *
     *      for every file in the training folders it goes through the words and adds it to the hashtable
     *      for every word it counts in how many emails it appeared at least once
     *
     *      after counting the appearance the spamicity for each word is calculated
     *      the spamicity hashtable is reduced to a few tousand words
     *
     *      as a last step the threshold is computed
     *
     * @param spamTrainingFolder
     * @param hamTrainingFolder
     */
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

        // another posibility is to consider the word frequency:
//        Hashtable<String, Integer> spamWordsCount = new Hashtable();

        // counts the appearance of a word in all the spam files
        Hashtable<String, Integer> spamFileCount = new Hashtable();
        // represents the probability of a word beeing in a spam email
        Hashtable<String, Double> spamRatio_W_S;

        for (File f : spamFiles) {
            addFileCountToHashtable(f, spamFileCount);
            numberOfFiles++;
        }
        spamRatio_W_S = calculateRatio(spamFileCount, numberOfFiles);
        System.out.println(numberOfFiles + " files found in spam training folder");

        numberOfFiles = 0;
        File hamFiles[] = filterFiles(hamTrainingDirectory.listFiles());

        // another posibility is to consider the word frequency:
//        Hashtable<String, Integer> hamWordsCount = new Hashtable();

        // counts the appearance of a word in all the ham files
        Hashtable<String, Integer> hamFileCount = new Hashtable();
        // represents the probability of a word beeing in a ham email
        Hashtable<String, Double> hamRatio_W_H;

        for (File f : hamFiles) {
            addFileCountToHashtable(f, hamFileCount);
            numberOfFiles++;
        }
        hamRatio_W_H = calculateRatio(hamFileCount, numberOfFiles);
        System.out.println(numberOfFiles + " files found in ham training folder");

        // calculating the spamicity of each word with the two probabilites spam- and hamRatio
        Hashtable<String, Double> fullSpamicity = calculateSpamicity(spamRatio_W_S, hamRatio_W_H);

        // reducing the Amount of words of the spamicity hashtable, removing the words with a spamicity arround 0.5
        spamicity = reduceAmountOfWords(fullSpamicity);

//        System.out.println(spamicity);
//        System.out.println(Collections.max(spamicity.values()));
//        System.out.println(Collections.min(spamicity.values()));
//        System.out.println(spamicity.values().size());

        computeThreshold(spamFiles, hamFiles);
    }

    /**
     * filters the file directory such that only files with the ending .txt are cosidered
     *
     * @param initialFiles all files from the dictory
     * @return an array with only text files
     */
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



    /**
     *  reads the file and adds every word to the a temporary hashtable.
     *  the methods counts every word only once per file
     *  in a second steps the values from the temp hashtable are added to the already existing hashtable, if the word
     *  already exists the value is increased by 1
     *
     * @param file
     * @param words
     */
    private void addFileCountToHashtable(File file, Hashtable<String, Integer> words) {
        try {
            Hashtable<String, Integer> tempHashtable = new Hashtable<>();
            for (String s : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
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
            //e.printStackTrace();
        }
    }

    /**
     * a method to add the frequency of each word to the hashtable
     *
     * @param f
     * @param words
     */
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

    /**
     *  saves for every word the probability of appearing in the file
     *
     * @param hashtable
     * @param numberOfFiles
     * @return
     */
    private Hashtable<String, Double> calculateRatio(Hashtable<String, Integer> hashtable, int numberOfFiles) {
        Hashtable<String, Double> ratio = new Hashtable<>();
        for (String word : hashtable.keySet())
            ratio.put(word, (double) hashtable.get(word) / (double) numberOfFiles);
        return ratio;
    }

    /**
     *  calculates the spamicity of each word.
     *  spamicity = the probability of a message beeing spam if the word appeares in it
     *
     *  if the word only appeared in only on of both, ham or spam messages, then the spamicity is set to 0.5
     *  (these words will be removed in a later step)
     *
     * @param spamRatio_w_s = the probability of a word appearing in a spam message
     * @param hamRatio_w_h = the probability of a word appearing in a ham message
     * @return
     */
    private Hashtable<String, Double> calculateSpamicity(Hashtable<String, Double> spamRatio_w_s, Hashtable<String, Double> hamRatio_w_h) {
        double p_s = 0.5;
        double p_h = 0.5;

        Hashtable<String, Double> spamicity_s_w = new Hashtable<>();

        for (String word : spamRatio_w_s.keySet()) {
            if (hamRatio_w_h.get(word) != null) {
                double spamicity = (spamRatio_w_s.get(word) * p_s) / (spamRatio_w_s.get(word) * p_s + hamRatio_w_h.get(word) * p_h);
                spamicity_s_w.put(word, spamicity);
            } else
                spamicity_s_w.put(word, 0.5);
        }

        for (String word : hamRatio_w_h.keySet()) {
            if (spamRatio_w_s.get(word) == null)
                spamicity_s_w.put(word, 0.5);
        }

        return spamicity_s_w;
    }

    /**
     *  reducing all the words arround a spamicity of 0.5
     * @param fullSpamicity
     * @return  new spamicity hashtable with less words
     */
    private Hashtable<String, Double> reduceAmountOfWords(Hashtable<String, Double> fullSpamicity) {
        Hashtable<String, Double> spamicity = new Hashtable<>();
        for (String word : fullSpamicity.keySet()) {
            Double value = fullSpamicity.get(word);
            if (Math.abs(value - 0.5) > SPAMICITY_THRESHOLD)
                spamicity.put(word, value);
        }
        return spamicity;
    }

    /**
     * computing the best threshold for the train data itself.
     * the threshold with the smallest errorRate is saved in THRESHOLD which the isSpam method uses for its decision
     *
     * @param spamFiles
     * @param hamFiles
     */
    private void computeThreshold(File[] spamFiles, File[] hamFiles) {

        /*
            testing 20 thresholds and saves the best
         */
        double bestErrorRate = 1.0;
        double thres = 1.0;
        while (thres >= 0.0) {
            double errorRate = getErrorRate(spamFiles, hamFiles, thres);
            if (errorRate < bestErrorRate) {
                THRESHOLD = thres;
                bestErrorRate = errorRate;
            }
            thres = thres - 0.05;
        }

        /*
            if the best threshold is very small or very big, it is tried to improve by testing
            even smaller/higher thresholds closer to 0.0 resp. 1.0
         */
        if (THRESHOLD <= 0.1) {
            thres = 0.1;
            while (thres >= 0.00000000001) {
                double errorRate = getErrorRate(spamFiles, hamFiles, thres);
                if (errorRate < bestErrorRate) {
                    THRESHOLD = thres;
                    bestErrorRate = errorRate;
                }
                thres = thres / 5;
            }
        }
        if (THRESHOLD >= 0.9) {
            thres = 0.1;
            while (thres >= 0.00000000001) {
                double errorRate = getErrorRate(spamFiles, hamFiles, 1 - thres);
                if (errorRate < bestErrorRate) {
                    THRESHOLD = 1 - thres;
                    bestErrorRate = errorRate;
                }
                thres = thres / 5;
            }
        }
    }

    /**
     *  gets the errorRate of the spamfilter when running it with the specified threshold
     *
     * @param spamFiles
     * @param hamFiles
     * @param threshold
     * @return
     */
    private double getErrorRate(File[] spamFiles, File[] hamFiles, double threshold) {
        int allSpam = 0;
        int SpamClassifiedAsHam = 0; //Spams incorrectly classified as Hams

        for (File f : spamFiles) {
            allSpam++;
            if (!internalIsSpam(f, threshold))
                SpamClassifiedAsHam++;
        }

        int allHam = 0;
        int HamClassifiedAsSpam = 0; //Hams incorrectly classified as Spams

        for (File f : hamFiles) {
            allHam++;
            if (internalIsSpam(f, threshold))
                HamClassifiedAsSpam++;
        }

        double errorRate = (Math.round((SpamClassifiedAsHam + HamClassifiedAsSpam) / (double) (allHam + allSpam) * 10000)) / 10000.0;
//        System.out.println("with threshold " + threshold +" the training data returns error rate " + errorRate * 100.0 + "%" );

        return errorRate;
    }

    /**
     * another internal isSpam method, because the isSpam method should not be renamed. So I interpreted that it is also
     * not allowed to change the requested parameters. Thats why its almost the same method as isSpam
     * <p>
     * it calculates the numerator and denominator of the probability of beeing spam seperately and then decides if it
     * is considered spam: if the probability is bigger equal the threshold
     *
     * @param file
     * @param threshold
     * @return
     */
    private boolean internalIsSpam(File file, double threshold) {
        double numerator = getNumerator(file);
        double denominator = getDenominator(file);

        double probability = numerator / denominator;

        if (probability >= threshold)
            return true;
        else
            return false;
    }

    /**
     * @param file the email which needs to be classified
     * @return  the numerator of the probability of a message being spam
     */
    private double getNumerator(File file) {
        double zaehler = 1.0;
        boolean touched = false;

        try {
            for (String s : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
                for (String word : s.split(" ")) {
                    Double value = spamicity.get(word);
                    if (value != null) {
                        zaehler = zaehler * value;
                        touched = true;
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

        if (touched)
            return zaehler;
        else
            return 0;
    }

    /**
     *
     * @param file
     * @return the denominator of the probability of a message being spam
     */
    private double getDenominator(File file) {
        double zaehler = 1.0;
        double secondPart = 1.0;
        boolean touched = false;

        try {
            for (String s : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
                for (String word : s.split(" ")) {
                    Double value = spamicity.get(word);
                    if (value != null) {
                        zaehler = zaehler * value;
                        secondPart = secondPart * (1 - value);
                        touched = true;
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

        if (touched)
            return zaehler + secondPart;
        else
            return 1;
    }

    /**
     *  Test the accuracy of the spamfilter trained in the "train" method
     *
     * @param spamTestingFolder
     * @param hamTestingFolder
     */
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

//        System.out.println(THRESHOLD);
        System.out.println("###_DO_NOT_USE_THIS_###Spam = " + allSpam);
        System.out.println("###_DO_NOT_USE_THIS_###Ham = " + allHam);
        System.out.println("###_DO_NOT_USE_THIS_###SpamClassifAsHam = " + SpamClassifiedAsHam);
        System.out.println("###_DO_NOT_USE_THIS_###HamClassifAsSpam = " + HamClassifiedAsSpam);
//        System.out.println("###_DO_NOT_USE_THIS_###SpamClassification ErrorRate: " + (Math.round(SpamClassifiedAsHam / (double) allSpam * 10000)) / 100.0 + "%");
//        System.out.println("###_DO_NOT_USE_THIS_###HamClassification ErrorRate: " + (Math.round(HamClassifiedAsSpam / (double) allHam * 10000)) / 100.0 + "%");
//        System.out.println("###_DO_NOT_USE_THIS_###Total ErrorRate: " + (Math.round((SpamClassifiedAsHam + HamClassifiedAsSpam) / (double) (allHam + allSpam) * 10000)) / 100.0 + "%");


    }

    /**
     *  determines if the message is classified as spam or ham
     *  considers the THRESHOLD which is saved in the spamfilter
     *
     * @param file
     * @return the decision if spam or not as boolean
     */
    public boolean isSpam(File file) {

        double zaehler = getNumerator(file);
        double nenner = getDenominator(file);

        double probability = zaehler / nenner;

        if (probability >= THRESHOLD)
            return true;
        else
            return false;
    }

}
