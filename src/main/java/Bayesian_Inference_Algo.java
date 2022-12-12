import java.security.KeyException;
import java.util.InputMismatchException;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * An abstract class for implementing different Bayesian Inference algorithms on top a Bayesian Network.
 * It facilitates a basic interface for all algorithms by defining a method for parsing common strict format
 * of text based queries into variables a bayesian network can work with
 * <p>
 * It implements the FlyWeight design pattern by simply using a Singleton Bayesian network class
 * **/

public abstract class Bayesian_Inference_Algo {

    protected final BayesiaNetwork network;

    public Bayesian_Inference_Algo(BayesiaNetwork network) { this.network = network; }

    /**
     * Expected query format P(Q=q|E1=e1, E2=e2,..., Ek=ek)
     **/
    public double Query(String query) {

        // parsing question
        String[] details = query.substring(2, query.length() -1).replaceAll("[=|]", ",").replace(" ", "").split(",");

        try {

            BayesNode query_variable = this.network.getVariable(details[0]);
            int query_value = this.network.translate(details[1]);
            BayesNode[] evidence_variables = new  BayesNode[(details.length -2) / 2];
            int[] evidence_values = new int[(details.length -2) / 2];

            int val_ind = 0, var_ind = 0, detail_ind;
            for (detail_ind = 2; detail_ind < details.length; ++detail_ind){

                if (detail_ind %2 == 0) {

                    evidence_variables[var_ind] = this.network.getVariable(details[detail_ind]);
                    ++var_ind;
                }
                else {

                    evidence_values[val_ind] = this.network.translate(details[detail_ind]);
                    ++val_ind;
                }
            }
            // if the evidence are the query's parents
            if (query_variable.AreParents(Arrays.asList(evidence_variables))){
                // then I don't need to call any algorithm
                return query_variable.fetch(IntStream.concat(IntStream.of(query_value), Arrays.stream(evidence_values)).toArray());
            }

            return this.compute(query_variable, query_value, evidence_variables, evidence_values);
        }
        catch (IndexOutOfBoundsException | KeyException e){
            throw new InputMismatchException("wrong query format");
        }
    }

    protected abstract double compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) throws KeyException;
}