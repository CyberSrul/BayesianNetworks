import java.io.*;
import java.security.KeyException;


/**
 * This is the Main class from which the program will start.
 * The program expects a txt file directory of the format:
 * Proper Bayesian Network format XML directory
 * than line by line
 * Query, type of algorithm (int, 1 : naive, 2 : VE, 3 : VE with some join-order heuristic)
 * **/

public class Main {

    static final BayesiaNetwork network = new BayesiaNetwork();
    static Bayesian_Inference_Algo algo;

    public static void main(String[] args) throws KeyException {

        try {

            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

            // The first line should specify the xml encoding of the Bayesian Network
            String line = reader.readLine();
            network.switchNetwork(line);

            line = reader.readLine();
            while (line != null) {

                // last character specifies the algorithm by which to answer the query
                switch (line.charAt(line.length() -1)){

                    case '1': algo = new Naive_Bayesian_Inference(network); break;
                    case '2': algo = new VariableElimination(network); break;
                    case '3': algo = new OptimisedVE(network); break;
                }

                writer.write((algo.Query(line.substring(0, line.length() - 2))) + "\n");

                line = reader.readLine();
            }
            reader.close();
            writer.close();
        }
        catch (IOException e) {

            e.printStackTrace();
        }
    }
}

//TODO: join order heuristic, preprocessing