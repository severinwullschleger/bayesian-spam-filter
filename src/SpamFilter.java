/**
 * Created by IntelliJ IDEA.
 * User: verman
 * Date: 4/23/12
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpamFilter {

    /**
     * The SpamFilter program can be run in three different ways:
     *
     *  - with four arguments it just takes the data in the specified folders:
     *      "SpamTrainingFolder" "HamTrainingFolder" "SpamTestingFolder" "HamTestingFolder"
     *
     *  - with a fifth argument the folder of the data can be specified. the folder must have to sub folders "ham" and
     *      "spam" with the emails in it. this mode will automatically distribute (copy) the emails in the training (2/3)
     *      and testing (1/3)
     *
     *  - the third mode is choosen by adding the fifth argument "all": it runs the spam filter process sequentially for
     *      the 6 enron datasets at the path "DataSets\\enron[1-6]"
     *
     * @param args
     */
    public static void main(String[] args) {

        if ((args.length < 4)) {
            System.out.println("Insufficient input arguments. Input format should be:");
            System.out.println("spamTrainingFolder, hamTrainingFolder, spamTestingFolder, hamTestingFolder");

        } else if (args.length == 5 && args[4].equals("all")) {

            PreProcessor pp = new PreProcessor();
            for (int i = 1; i <= 6; i++) {
                pp.distributeDataset("DataSets\\enron" + i);

                BayesianClassifier bc = new BayesianClassifier();

                String spamTrainingFolder = args[0];
                String hamTrainingFolder = args[1];

                bc.train(spamTrainingFolder, hamTrainingFolder);

                String spamTestingFolder = args[2];
                String hamTestingFolder = args[3];

                bc.test(spamTestingFolder, hamTestingFolder);
            }

        } else if (args.length == 5) {
            /**
                the preprocessor distributes the as fifth argument declared folder
                the folder must contain a "spam" and s "ham" folder
             */
            PreProcessor pp = new PreProcessor();
            pp.distributeDataset(args[4]);

            BayesianClassifier bc = new BayesianClassifier();

            String spamTrainingFolder = args[0];
            String hamTrainingFolder = args[1];

            bc.train(spamTrainingFolder, hamTrainingFolder);

            String spamTestingFolder = args[2];
            String hamTestingFolder = args[3];

            bc.test(spamTestingFolder, hamTestingFolder);

        } else {
            /**
                default process which is run when grading
             */
            BayesianClassifier bc = new BayesianClassifier();

            String spamTrainingFolder = args[0];
            String hamTrainingFolder = args[1];

            bc.train(spamTrainingFolder, hamTrainingFolder);

            String spamTestingFolder = args[2];
            String hamTestingFolder = args[3];

            bc.test(spamTestingFolder, hamTestingFolder);
        }
    }
}
