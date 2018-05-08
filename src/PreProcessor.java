import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class PreProcessor {

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

        for (File f : hamFiles) {
            Random rand = new Random();
            float fl = rand.nextFloat();

            if (fl < 0.3333)
                copyFileToPath(f, "HamTestingFolder");
            else
                copyFileToPath(f, "HamTrainingFolder");
        }

        for (File f : spamFiles) {
            Random rand = new Random();
            float fl = rand.nextFloat();

            if (fl < 0.3333)
                copyFileToPath(f, "SpamTestingFolder");
            else
                copyFileToPath(f, "SpamTrainingFolder");
        }
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public void copyFileToPath(File f, String path) {
        try {
            Path copied = Paths.get(path + "\\" + f.getName());
            Path originalPath = Paths.get(f.getAbsolutePath());
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
