import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
   Conditional Probability table, a corner stone of Bayesian Networks.
   The odds of a Variable to poses some value given its parents state.
*/

public class CPT {

    final List<BayesNode> variables;
    final int[][] values;
    final double[] probabilities;


    public CPT(List<BayesNode> variables, double[] probabilities){

        if (variables.size() == 0){ throw new InputMismatchException("variables list can not be empty"); }

        this.variables = variables;
        this.probabilities = Arrays.copyOf(probabilities, probabilities.length);

        // sizes of variables possible values ranges
        int[] sizes = variables.stream().mapToInt(BayesNode::getRangeSize).toArray();
        // allocating memory
        int size = variables.stream().mapToInt(BayesNode::getRangeSize).reduce(1, (x, y) -> x * y);
        if (size != probabilities.length){ throw new InputMismatchException("incompatibility between size of table and the variables"); }
        this.values = new int[variables.size()][size];


        // the CPT needs to specify the probability for any combination of values,
        // so we need to iterate through the table in a specific manner
        int loop = 1;   // frequency for changing value
        for (int var_ind = 0; var_ind < variables.size(); ++var_ind) {

            int val_ind = 0;
            for (int table_ind = 0; table_ind < size; ++table_ind){

                if ((table_ind % loop == 0 || loop == 1) && table_ind >= loop){
                    ++val_ind;
                    val_ind %= variables.get(var_ind).getRangeSize();
                }
                this.values[var_ind][table_ind] = variables.get(var_ind).getValue(val_ind);
            }
            loop *= sizes[var_ind]; // add next variables values for every previous combination
        }
    }

    public CPT join (CPT other){ return new CPT(this, other); }

    private CPT(CPT cpt1, CPT cpt2){

        List<BayesNode> intersect = cpt1.variables.stream().filter(cpt2.variables::contains).collect(Collectors.toList());
        List<BayesNode> diff = cpt1.variables.stream().filter((var) -> ! intersect.contains(var)).collect(Collectors.toList());
        // inclusion exclusion
        this.variables = Stream.concat(cpt2.variables.stream(), diff.stream()).collect(Collectors.toList());
        // size of table
        int size = variables.stream().mapToInt(BayesNode::getRangeSize).reduce(1, (x, y) -> x * y);
        this.probabilities = new double[size];
        this.values = new int[this.variables.size()][size];

        // the join operation
        int[] cpt1_intersect_indexes = intersect.stream().mapToInt(cpt1.variables::indexOf).toArray();
        int[] cpt2_intersect_indexes = intersect.stream().mapToInt(cpt2.variables::indexOf).toArray();
        int[] cpt2_vars_indexes = cpt2.variables.stream().mapToInt(this.variables::indexOf).toArray();
        int[] diff_indexes = diff.stream().mapToInt(this.variables::indexOf).toArray();
        int prob_ind = 0, val1_ind, val2_ind, var_ind;

        for (val1_ind = 0; val1_ind < cpt1.probabilities.length; ++val1_ind){
            for (val2_ind = 0; val2_ind < cpt2.probabilities.length; ++val2_ind){

                boolean match = true;
                for (var_ind = 0; var_ind < intersect.size(); ++var_ind){
                    if (cpt1.values[cpt1_intersect_indexes[var_ind]][val1_ind] != cpt2.values[cpt2_intersect_indexes[var_ind]][val2_ind]){
                        match = false; break;
                    }
                }
                if (match){
                    // probability
                    this.probabilities[prob_ind] = cpt1.probabilities[val1_ind] * cpt2.probabilities[val2_ind];
                    // the variables state
                    for (var_ind = 0; var_ind < cpt2_vars_indexes.length; ++var_ind){
                        this.values[cpt2_vars_indexes[var_ind]][prob_ind] = cpt2.values[var_ind][val2_ind];
                    }
                    for (var_ind = 0; var_ind < diff_indexes.length; ++var_ind){
                        this.values[diff_indexes[var_ind]][prob_ind] = cpt1.values[var_ind][val1_ind];
                    }
                    ++prob_ind;
                }
            }
        }
    }

    public CPT factor(List<BayesNode> evidence_nodes, int[] evidence_values){

        List<Integer> evidence_indexes = evidence_nodes.stream().map(this.variables::indexOf).collect(Collectors.toList());
        // a factor of how much space is saved by the evidence
        int evidence_size = evidence_nodes.stream().mapToInt(BayesNode::getRangeSize).reduce(1, (x, y) -> x * y);
        // allocating memory
        double[] probabilities = new double[this.probabilities.length / evidence_size];

        // searching the CPT for the right values
        int val_ind, prob_ind = 0;
        for (val_ind = 0; val_ind < this.probabilities.length; ++val_ind){

            // checking if current entry is relevant to the factor
            boolean match = true;
            for (int evd_ind = 0; evd_ind < evidence_values.length; ++evd_ind){
                if (this.values[evidence_indexes.get(evd_ind)][val_ind] != evidence_values[evd_ind]){
                    match = false; break;
                }
            }
            if (match){ probabilities[prob_ind] = this.probabilities[val_ind]; ++prob_ind; }
        }
        // finally returning a table of all relevant data (what's not evident)
        return new CPT(this.variables.stream().filter((var) -> ! evidence_nodes.contains(var)).collect(Collectors.toList()), probabilities);
    }

    public CPT sum(BayesNode target){

        if (! this.variables.contains(target)){ throw new InputMismatchException("variable does not appear in the table"); }

        List<BayesNode> res_variables = List.copyOf(this.variables);
        res_variables = res_variables.stream().filter((var) -> var != target).collect(Collectors.toList());

        double[] res_probabilities = new double[this.probabilities.length / target.getRangeSize()];
        Arrays.fill(res_probabilities, 0);

        CPT res = new CPT(res_variables, res_probabilities);

        // indexes of the remaining variables
        int[] indexes = res.variables.stream().mapToInt(this.variables::indexOf).toArray();

        int this_val_ind, res_val_ind, var_ind;
        for (this_val_ind = 0; this_val_ind < this.probabilities.length; ++this_val_ind){
            for (res_val_ind = 0; res_val_ind < res.probabilities.length; ++res_val_ind){

                boolean match = true;
                for (var_ind = 0; var_ind < res.variables.size(); ++var_ind){
                    if (this.values[indexes[var_ind]][this_val_ind] != res.values[var_ind][res_val_ind]){
                        match = false; break;
                    }
                }
                if (match){
                    res.probabilities[res_val_ind] += this.probabilities[this_val_ind];
                    break;
                }
            }
        }
        return res;
    }

    public String toString(){

        StringBuilder view = new StringBuilder(Arrays.toString(this.variables.stream().mapToInt(BayesNode::getName).toArray()) + "\n\n");

        for (int val_ind = 0; val_ind < this.values[0].length; ++val_ind){
            for (int[] value : this.values) {

                view.append(value[val_ind]).append(" ");
            }
            view.append(this.probabilities[val_ind]).append("\n");
        }
        return view.toString();
    }
}