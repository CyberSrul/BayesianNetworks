import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Bayesian_Inference_AlgoTest {

    static Bayesian_Inference_Algo naive, VE, best;
    static final String dir = "src/main/resources/alarm_net.xml";

    @BeforeEach
    void setUp() throws KeyException {

        naive = new Naive_Bayesian_Inference(dir);
        VE = new VariableElimination(dir);
        best = new OptimisedVE(dir);
    }

    @Test
    void switchNetwork() {
    }

    @Test
    void query() {

        assertArrayEquals(new String[]{"Q", "q", "E1", "e1", "E2", "e2", "E3", "e3"}, "Q=q|E1=e1, E2=e2, E3=e3".replaceAll("[=|]", ",").replace(" ", "").split(","));
    }
}