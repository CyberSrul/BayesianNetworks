import java.security.KeyException;
import java.util.InputMismatchException;

public abstract class Bayesian_Inference_Algo {

    private BayesiaNetwork network;

    public Bayesian_Inference_Algo(BayesiaNetwork network) throws KeyException { this.network = network; }

    /** Expected query format P(Q=q|E1=e1, E2=e2,..., Ek=ek) **/
    public String Query(String query) {

        // parsing question
        String[] details = query.replaceAll("[=|]", ",").replace(" ", "").split(",");

        try {

            BayesNode query_variable = this.network.getVariable(details[0]);
            int query_value = this.network.translate(details[1]);
            BayesNode[] evidence_variables = new  BayesNode[(details.length -2) / 2];
            int[] evidence_values = new int[(details.length -2) / 2];

            int val_ind = 0, var_ind = 0, detail_ind;
            for (detail_ind = 2; detail_ind < details.length; ++detail_ind){

                if (detail_ind %2 == 0) {

                    evidence_variables[var_ind] = this.network.getVariable(details[var_ind]);
                    ++var_ind;
                }
                else {

                    evidence_values[val_ind] = this.network.translate(details[val_ind]);
                    ++val_ind;
                }
            }

            return this.compute(query_variable, query_value, evidence_variables, evidence_values);
        }
        catch (IndexOutOfBoundsException | KeyException e){
            throw new InputMismatchException("wrong query format");
        }
    }

    protected abstract String compute(BayesNode query_variable, int query_value, BayesNode[] evidence_variables, int[] evidence_values);
}

//TODO: if answer already appears ...

