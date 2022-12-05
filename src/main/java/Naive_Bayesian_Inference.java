public class Naive_Bayesian_Inference extends Bayesian_Inference_Algo{

    public Naive_Bayesian_Inference(BayesiaNetwork network) { super(network); }

    @Override
    protected double compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) {
        return 0;
    }
}
