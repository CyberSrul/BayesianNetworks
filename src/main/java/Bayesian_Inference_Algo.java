import java.security.KeyException;
import java.util.InputMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import java.text.DecimalFormat;

/**
 * An abstract class for implementing different Bayesian Inference algorithms on top a Bayesian Network.
 * It facilitates a basic interface for all algorithms by defining a method for parsing common strict format
 * of text based queries into variables a bayesian network can work with
 * <p>
 * It implements the FlyWeight design pattern by simply using a Singleton Bayesian network class
 * **/

public abstract class Bayesian_Inference_Algo {

    protected final BayesiaNetwork network;
    static final DecimalFormat out_format = new DecimalFormat("0.00000"); // output format

    public Bayesian_Inference_Algo(BayesiaNetwork network) { this.network = network; }

    /**
     * Expected query format P(Q=q|E1=e1, E2=e2,..., Ek=ek)
     * Expected output format #.#####,number of additions,number of multiplications
     **/
    public String Query(String query) {

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
                // arranging the values array correctly
                sortByParents(query_variable, evidence_variables, evidence_values);
                return query_variable.fetch(IntStream.concat(IntStream.of(query_value), Arrays.stream(evidence_values)).toArray()) + ",0,0";
            }

            return this.compute(query_variable, query_value, evidence_variables, evidence_values);
        }
        catch (IndexOutOfBoundsException | KeyException e){
            throw new InputMismatchException("wrong query format");
        }
    }

    private void sortByParents(BayesNode query_variable, BayesNode[] evidence_variables, int[] values){

        List<BayesNode> parents_order = query_variable.getCPT().getVariables().subList(1, evidence_variables.length +1);

        for (int ind = 0; ind < evidence_variables.length; ++ind){

           int proper_loc = parents_order.indexOf(evidence_variables[ind]);

           // swapping

           BayesNode tmp_var = evidence_variables[ind];
           evidence_variables[ind] = evidence_variables[proper_loc];
           evidence_variables[proper_loc] = tmp_var;

           int tmp_val = values[ind];
           values[ind] = values[proper_loc];
           values[proper_loc] = tmp_val;
        }
    }

    protected abstract String compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) throws KeyException;

    // method for evaluating the cost of a join operation between 2 given CPTs
    // that is, how many multiplications
    public int EvaluateJoin(CPT cpt1, CPT cpt2){

        if (cpt1 == null || cpt2 == null) return  0;

        int cpt1_ranges = cpt1.getVariables().stream().mapToInt(BayesNode::getRangeSize).reduce(1, (x, y) -> x * y);
        // value-ranges of the variables of cpt2 without those of cpt1
        int cpt2_diff_ranges = cpt2.getVariables().stream().filter((var) -> ! cpt1.getVariables().contains(var)).mapToInt(BayesNode::getRangeSize).reduce(1, (x, y) -> x * y);

        return cpt1_ranges * cpt2_diff_ranges;
    }

    // method for evaluating the cost of a sum operation on a given CPT
    // that is, how many additions
    public int EvaluateSum(CPT cpt, BayesNode eliminated){

        if (! cpt.refersTo(eliminated)) throw new InputMismatchException("can't eliminate this variable the CPT does not refer to it");

        return cpt.getVariables().stream().filter((var) -> var != eliminated).mapToInt(BayesNode::getRangeSize).reduce(1, (x, y) -> x * y)  *  (eliminated.getRangeSize() -1);
    }
}