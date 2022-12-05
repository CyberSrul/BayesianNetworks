public class VariableElimination extends Bayesian_Inference_Algo {

    public VariableElimination(BayesiaNetwork network) { super(network); }

    @Override
    protected double compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) {
        return 0;
    }
}
