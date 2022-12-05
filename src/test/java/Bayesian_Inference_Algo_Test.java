import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyException;

import static org.junit.jupiter.api.Assertions.*;

class Bayesian_Inference_Algo_Test {

    static Bayesian_Inference_Algo naive, VE, best;
    static final BayesiaNetwork network = new BayesiaNetwork();
    static {
        try {
            network.switchNetwork("src/main/resources/alarm_net.xml");
        } catch (KeyException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {

        naive = new Naive_Bayesian_Inference(network);
        VE = new VariableElimination(network);
        best = new OptimisedVE(network);
    }

    @Test
    void query() {

        assertArrayEquals(new String[]{"Q", "q", "E1", "e1", "E2", "e2", "E3", "e3"}, "Q=q|E1=e1, E2=e2, E3=e3".replaceAll("[=|]", ",").replace(" ", "").split(","));

        assertEquals(0.94, naive.Query("A=T|B=T, E=F"));
        assertEquals(0.94, VE.Query("A=T|B=T, E=F"));
        assertEquals(0.94, best.Query("A=T|B=T, E=F"));
        assertEquals(0.06, naive.Query("A=F|B=T, E=F"));
        assertEquals(0.06, VE.Query("A=F|B=T, E=F"));
        assertEquals(0.06, best.Query("A=F|B=T, E=F"));
        assertEquals(0.3, best.Query("M=F|A=T"));

    }
}