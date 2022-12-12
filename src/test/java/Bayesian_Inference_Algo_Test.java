import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyException;

import static org.junit.jupiter.api.Assertions.*;

class Bayesian_Inference_Algo_Test {

    static Bayesian_Inference_Algo naive, VE, best;
    static final BayesiaNetwork network = new BayesiaNetwork();

    @BeforeEach
    void setUp() {

        naive = new Naive_Bayesian_Inference(network);
        VE = new VariableElimination(network);
        best = new OptimisedVE(network);
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