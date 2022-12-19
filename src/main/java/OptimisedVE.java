import java.util.LinkedList;
import java.util.List;

import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Adopting a heuristic for determining the order of elimination in the VE algorithm.
 * The idea: eliminate variables with bigger range first.
 * Rational: They make much bigger CPTs as their presence exhibits more convoluted joint distribution table.
 * So it's plausible to assume that it is efficient to make sure they will not persist throughout the computation for long.
 * **/

public class OptimisedVE extends VariableElimination{

    public OptimisedVE(BayesiaNetwork network) { super(network); }

    @Override
    protected String compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) {

        List<CPT> factors = generate_factors(query_variable, evidence_variables, evidence_values, get_relevant_variables(query_variable, evidence_variables));

        int multiplications = 0, additions = 0;

        List<BayesNode> elimination_order = new LinkedList<>();
        this.network.iterator().forEachRemaining(elimination_order::add);
        elimination_order.sort(Comparator.comparingInt(BayesNode::getRangeSize));


        for (BayesNode current_variable : elimination_order) {

            /* joining */

            List<CPT> relevant_variables = factors.stream().filter((fact) -> fact.refersTo(current_variable)).sorted(Comparator.comparingInt(CPT::size)).collect(Collectors.toList());
            if (relevant_variables.isEmpty()) continue;

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