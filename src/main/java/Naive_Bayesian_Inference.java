import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// simplest least efficient algorithm, just use the chain rule and law of total probability
// and just do all the arithmetic

public class Naive_Bayesian_Inference extends Bayesian_Inference_Algo{

    public Naive_Bayesian_Inference(BayesiaNetwork network) { super(network); }

    @Override
    protected String compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) {

        CPT cpt = null;

        for (BayesNode current_variable : this.network){

            List<BayesNode> evident_parents =  Arrays.stream(evidence_variables).filter((var) ->  current_variable.getParent(var.getName()) != null || (current_variable == var && current_variable != query_variable)).collect(Collectors.toList());
            int[] evident_parents_values = IntStream.range(0, evidence_values.length).filter((ind) -> evident_parents.contains(evidence_variables[ind])).map((ind) -> evidence_values[ind]).toArray();
            cpt = current_variable.getCPT().factor(evident_parents, evident_parents_values).join(cpt);
        }
        if (cpt == null) throw new RuntimeException("network has not been initialised yet");
        // normalization and we are good to go
        return out_format.format(cpt.factor(List.of(query_variable), new int[]{query_value}).totalSum()  /  cpt.totalSum()) + "," + (cpt.size() -1) + "," + (cpt.size() * (this.network.size() -1));
    }
}