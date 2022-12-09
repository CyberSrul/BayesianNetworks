import java.security.KeyException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.Iterator;

/**
 * This algorithm builds on the naive one.
 * However, it takes advantage of the distributivity of real numbers arithmetic
 * And uses the fact that we utilise a lot of memory to apply some dynamic programming.
 * <p>
 * wherever a variable 'bifurcates' the calculation, we end up repeating operations.
 * but the join operation does all the multiplication we need.
 * So, at each bifurcation we do the relevant joins than sum up over the bifurcating variable before preceding
 * <p>
 * this algorithm also opens the door for a lot of preprocessing optimizations.
 * some very basic one is implemented in this class and more certificated ones in inheriting classes.
**/

public class VariableElimination extends Bayesian_Inference_Algo {

    public VariableElimination(BayesiaNetwork network) { super(network); }

    protected List<CPT> generate_factors(BayesNode query_variable, BayesNode[] evidence_variables, int[] evidence_values){

        List<CPT> factors = new LinkedList<>();

        for (BayesNode current_variable : this.network){

            List<BayesNode> evident_parents =  Arrays.stream(evidence_variables).filter((var) ->  current_variable.getParent(var.getName()) != null || (current_variable == var && current_variable != query_variable)).collect(Collectors.toList());
            int[] evident_parents_values = IntStream.range(0, evidence_values.length).filter((ind) -> evident_parents.contains(evidence_variables[ind])).map((ind) -> evidence_values[ind]).toArray();

            factors.add(current_variable.getCPT().factor(evident_parents, evident_parents_values));
        }
        return factors;
    }

    @Override
    protected double compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) throws KeyException {

        List<CPT> factors = generate_factors(query_variable, evidence_variables, evidence_values);

        for (Iterator<String> it = this.network.getNames(); it.hasNext();) {

            BayesNode current_variable = this.network.getVariable(it.next());
            if (current_variable == null ) continue;

            // joining
            CPT joined_factors = factors.stream().filter((fact) -> fact.refersTo(current_variable)).reduce(CPT::join).orElse(null);
            if (joined_factors == null) continue;
            // summing up
            if (current_variable != query_variable) { joined_factors =  joined_factors.sum(current_variable); }
            // get rid of old factors
            factors = factors.stream().filter((cpt -> ! cpt.refersTo(current_variable))).collect(Collectors.toList());
            // remember the new joined factor though
            factors.add(joined_factors);
        }

        CPT result = factors.get(factors.size() -1);
        // normalization
        return result.fetch(List.of(query_variable), new int[]{query_value})  /  result.totalSum();
    }
}