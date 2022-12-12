import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Bayesian_Inference_Algo_Test {

    static Bayesian_Inference_Algo naive, VE, best;
    static final BayesiaNetwork network = new BayesiaNetwork();
    static CPT cpt1, cpt2, cpt3;
    static BayesNode A, B, C, D;

    @BeforeEach
    void setUp() {

        naive = new Naive_Bayesian_Inference(network);
        VE = new VariableElimination(network);
        best = new OptimisedVE(network);

        A = new BayesNode(0, new int[]{0, 1});
        B = new BayesNode(1, new int[]{0, 1});
        C = new BayesNode(2, new int[]{0, 1, 2});
        D = new BayesNode(3, new int[]{1, 2});
        double[] probabilities1 = new double[12];    Arrays.fill(probabilities1, 0.05);
        double[] probabilities2 = new double[8];     Arrays.fill(probabilities2, 0.05);
        double[] probabilities3 = new double[4];    Arrays.fill(probabilities3, 0.05);
        cpt1 = new CPT(List.of(A, B, C), probabilities1);
        cpt2 = new CPT(List.of(B, A, D), probabilities2);
        cpt3 = new CPT(List.of(B, A), probabilities3);
    }

    @Test
    void CostEvaluation(){

        Assertions.assertEquals(24, naive.EvaluateJoin(cpt1, cpt2));
        Assertions.assertEquals(24, VE.EvaluateJoin(cpt1, cpt2));
        Assertions.assertEquals(24, best.EvaluateJoin(cpt1, cpt2));
        // flipping the order should not change the result
        Assertions.assertEquals(24, naive.EvaluateJoin(cpt2, cpt1));
        Assertions.assertEquals(24, VE.EvaluateJoin(cpt2, cpt1));
        Assertions.assertEquals(24, best.EvaluateJoin(cpt2, cpt1));

        Assertions.assertEquals(12, naive.EvaluateJoin(cpt1, cpt3));
        Assertions.assertEquals(12, VE.EvaluateJoin(cpt1, cpt3));
        Assertions.assertEquals(12, best.EvaluateJoin(cpt1, cpt3));
        // flipping the order should not change the result
        Assertions.assertEquals(12, naive.EvaluateJoin(cpt3, cpt1));
        Assertions.assertEquals(12, VE.EvaluateJoin(cpt3, cpt1));
        Assertions.assertEquals(12, best.EvaluateJoin(cpt3, cpt1));

        Assertions.assertEquals(8, naive.EvaluateJoin(cpt2, cpt3));
        Assertions.assertEquals(8, VE.EvaluateJoin(cpt2, cpt3));
        Assertions.assertEquals(8, best.EvaluateJoin(cpt2, cpt3));
        // flipping the order should not change the result
        Assertions.assertEquals(8, naive.EvaluateJoin(cpt3, cpt2));
        Assertions.assertEquals(8, VE.EvaluateJoin(cpt3, cpt2));
        Assertions.assertEquals(8, best.EvaluateJoin(cpt3, cpt2));
    }

    @Test
    void EvaluateSum(){

        Assertions.assertEquals(6, naive.EvaluateSum(cpt1, A));
        Assertions.assertEquals(6, VE.EvaluateSum(cpt1, A));
        Assertions.assertEquals(6, best.EvaluateSum(cpt1, A));
        Assertions.assertEquals(4, naive.EvaluateSum(cpt2, A));
        Assertions.assertEquals(4, VE.EvaluateSum(cpt2, A));
        Assertions.assertEquals(4, best.EvaluateSum(cpt2, A));
        Assertions.assertEquals(2, naive.EvaluateSum(cpt3, A));
        Assertions.assertEquals(2, VE.EvaluateSum(cpt3, A));
        Assertions.assertEquals(2, best.EvaluateSum(cpt3, A));
        Assertions.assertEquals(6, naive.EvaluateSum(cpt1, B));
        Assertions.assertEquals(6, VE.EvaluateSum(cpt1, B));
        Assertions.assertEquals(6, best.EvaluateSum(cpt1, B));
        Assertions.assertEquals(4, naive.EvaluateSum(cpt2, B));
        Assertions.assertEquals(4, VE.EvaluateSum(cpt2, B));
        Assertions.assertEquals(4, best.EvaluateSum(cpt2, B));
        Assertions.assertEquals(2, naive.EvaluateSum(cpt3, B));
        Assertions.assertEquals(2, VE.EvaluateSum(cpt3, B));
        Assertions.assertEquals(2, best.EvaluateSum(cpt3, B));
        Assertions.assertEquals(8, naive.EvaluateSum(cpt1, C));
        Assertions.assertEquals(8, VE.EvaluateSum(cpt1, C));
        Assertions.assertEquals(8, best.EvaluateSum(cpt1, C));
        Assertions.assertEquals(4, naive.EvaluateSum(cpt2, D));
        Assertions.assertEquals(4, VE.EvaluateSum(cpt2, D));
        Assertions.assertEquals(4, best.EvaluateSum(cpt2, D));

        Assertions.assertThrows(InputMismatchException.class, () -> naive.EvaluateSum(cpt2, C));
        Assertions.assertThrows(InputMismatchException.class, () -> naive.EvaluateSum(cpt1, D));
        Assertions.assertThrows(InputMismatchException.class, () -> naive.EvaluateSum(cpt3, C));
        Assertions.assertThrows(InputMismatchException.class, () -> naive.EvaluateSum(cpt3, D));
    }

    @Test
    void query() throws KeyException {

        network.switchNetwork("alarm_net.xml");

        String example_query = "P(Q=q|E1=e1, E2=e2, E3=e3)";
        assertArrayEquals(new String[]{"Q", "q", "E1", "e1", "E2", "e2", "E3", "e3"}, example_query.substring(2, example_query.length() -1).replaceAll("[=|]", ",").replace(" ", "").split(","));

        assertEquals(0.94, naive.Query("P(A=T|B=T, E=F)"));
        assertEquals(0.94, VE.Query("P(A=T|B=T, E=F)"));
        assertEquals(0.94, best.Query("P(A=T|B=T, E=F)"));
        assertEquals(0.06, naive.Query("P(A=F|B=T, E=F)"));
        assertEquals(0.06, VE.Query("P(A=F|B=T, E=F)"));
        assertEquals(0.06, best.Query("P(A=F|B=T, E=F)"));
        assertEquals(0.3, best.Query("P(M=F|A=T)"));
    }

    @Test
    void Heavy_searches() throws KeyException {

        network.switchNetwork("alarm_net.xml");

        assertEquals(0.28417, naive.Query("P(B=T|J=T,M=T)"), 5);
        assertEquals(0.84902, naive.Query("P(J=T|B=T)"), 5);
        assertEquals(0.99, naive.Query("P(A=F|)"), 2);
        assertEquals(0.6975, naive.Query("P(M=T|J=T,B=T)"), 4);
        assertEquals(0.28417, VE.Query("P(B=T|J=T,M=T)"), 5);
        assertEquals(0.84902, VE.Query("P(J=T|B=T)"), 5);
        assertEquals(0.99, VE.Query("P(A=F|)"), 2);
        assertEquals(0.6975, VE.Query("P(M=T|J=T,B=T)"), 4);

        network.switchNetwork("big_net.xml");

        assertEquals(0.423, naive.Query("P(B0=v3|C3=T,B2=F,C2=v3)"), 3);
        assertEquals(0.0936, naive.Query("P(A2=T|C2=v1)"), 4);
        assertEquals(0.37687, naive.Query("P(D1=T|C2=v1,C3=F)"), 5);
        assertEquals(0.423, VE.Query("P(B0=v3|C3=T,B2=F,C2=v3)"), 3);
        assertEquals(0.0936, VE.Query("P(A2=T|C2=v1)"), 4);
        assertEquals(0.37687, VE.Query("P(D1=T|C2=v1,C3=F)"), 5);
    }
}