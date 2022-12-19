import java.security.KeyException;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    protected List<CPT> generate_factors(BayesNode query_variable, BayesNode[] evidence_variables, int[] evidence_values, List<BayesNode> reverent_variables){

        List<CPT> factors = new LinkedList<>();

        for (BayesNode current_variable : reverent_variables){

            List<BayesNode> evident_parents =  Arrays.stream(evidence_variables).filter((var) ->  current_variable.getParent(var.getName()) != null || (current_variable == var && current_variable != query_variable)).collect(Collectors.toList());
            int[] evident_parents_values = IntStream.range(0, evidence_values.length).filter((ind) -> evident_parents.contains(evidence_variables[ind])).map((ind) -> evidence_values[ind]).toArray();

            factors.add(current_variable.getCPT().factor(evident_parents, evident_parents_values));
        }
        return factors;
    }

    @Override
    protected String compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) throws KeyException {

        LinkedList<BayesNode> sources = new LinkedList<>(Arrays.asList(evidence_variables));
        sources.add(query_variable);

        List<CPT> factors = generate_factors(query_variable, evidence_variables, evidence_values, this.network.getAncestors(sources));

        int multiplications = 0, additions = 0;

        for (Iterator<String> it = this.network.getNames(); it.hasNext();) {

            BayesNode current_variable = this.network.getVariable(it.next());
            if (current_variable == null ) continue;

            /* joining */

            List<CPT> relevant_variables = factors.stream().filter((fact) -> fact.refersTo(current_variable)).sorted(Comparator.comparingInt(CPT::size)).collect(Collectors.toList());
            CPT joined_factors = null;
            for (CPT factor : relevant_variables){

                // join cost evaluation
                int current_multiplications = EvaluateJoin(joined_factors, factor);
                if (joined_factors != null && joined_factors.refersTo(query_variable)) current_multiplications /= query_variable.getRangeSize();
                multiplications += current_multiplications;

                joined_factors = factor.join(joined_factors);
            }

            if (joined_factors == null) continue;
            // summing up
            if (current_variable != query_variable) {

                int cost = EvaluateSum(joined_factors, current_variable);
                joined_factors =  joined_factors.sum(current_variable);
                if (joined_factors.size() != 1) additions += cost;
            }
            // get rid of old factors
            factors = factors.stream().filter((cpt -> ! cpt.refersTo(current_variable))).collect(Collectors.toList());
            // remember the new joined factor though
            if (joined_factors.size() != 1) factors.add(joined_factors);
        }

        CPT result = factors.get(factors.size() -1);
        // normalization
        ++additions;
        return out_format.format(result.fetch(List.of(query_variable), new int[]{query_value})  /  result.totalSum()) + "," + additions + "," + multiplications;
    }
}