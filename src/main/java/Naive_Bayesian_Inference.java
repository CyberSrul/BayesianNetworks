import java.security.KeyException;

public class Naive_Bayesian_Inference extends Bayesian_Inference_Algo{

    public Naive_Bayesian_Inference(String dir) throws KeyException { super(dir); }

    @Override
    protected String compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values) {
        return null;
    }
}
