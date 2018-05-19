import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;

public class PreProcessor {

    /**
     *  distributes the emails from the specifed folder path to the training and test folder with a ratio: 1/3 test and
     *  2/3 train
     *
     * @param dataSetPath which contains the data, the folder needs to have a "ham" and a "spam" directory
     */
    public void distributeDataset(String dataSetPath) {
        File hamTestingFolder = new File("HamTestingFolder");
        deleteFolder(hamTestingFolder);
        File hamTrainingFolder = new File("HamTrainingFolder");
        deleteFolder(hamTrainingFolder);
        File spamTestingFolder = new File("SpamTestingFolder");
        deleteFolder(spamTestingFolder);
        File spamTrainingFolder = new File("SpamTrainingFolder");
        deleteFolder(spamTrainingFolder);

        //C:\Users\sever\GitHub\PAI_A3\DataSets\enron1
        File hamDirectory = new File(dataSetPath + "\\ham");
        File spamDirectory = new File(dataSetPath + "\\spam");

        File[] hamFiles = hamDirectory.listFiles();
        File[] spamFiles = spamDirectory.listFiles();

        // would be used for balancing the negative and positive classification
        double spamHamRatio = spamFiles.length / (double) hamFiles.length;   // e.g 1001 / 2898 = 1/3

        for (File f : hamFiles) {
            Random rand = new Random();
            float fl = rand.nextFloat();

            if (fl < 0.3333)
                copyFileToPath(f, "HamTestingFolder", 1);
            else {
                copyFileToPath(f, "HamTrainingFolder", 1);

                /**
                Trying to balance the negative and positive example for getting a better learning
                -> did not improve the learning
                 **/
//                // balance training data
//                if (spamHamRatio > 1) {
//                    Long multiplikator = Math.round(spamHamRatio);
//                    for (int i = 2; i <= multiplikator; i++)
//                        copyFileToPath(f, "HamTrainingFolder", i);
//                }
            }
        }

        for (File f : spamFiles) {
            Random rand = new Random();
            float fl = rand.nextFloat();

            if (fl < 0.3333)
                copyFileToPath(f, "SpamTestingFolder", 1);
            else {
                copyFileToPath(f, "SpamTrainingFolder", 1);

                /**
                Trying to balance the negative and positive example for getting a better learning
                -> did not improve the learning
                 **/
//                // balance training data
//                if (spamHamRatio < 1) {
//                    Long multiplikator = Math.round((1 / spamHamRatio));
//                    for (int i = 2; i <= multiplikator; i++)
//                        copyFileToPath(f, "SpamTrainingFolder", i);
//                }
            }
        }
    }

    private void balance(List<File> files, double ratio) {
        int filesLength = files.size();
        Random rand = new Random();
        for (int i = 0; i < filesLength; i++) {
            if (rand.nextFloat() > ratio)
                files.add(files.get(i));
        }
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public void copyFileToPath(File f, String path, int copy) {
        try {
            Path copied = Paths.get(path + "\\" + copy + "_" + f.getName());
            Path originalPath = Paths.get(f.getAbsolutePath());
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
