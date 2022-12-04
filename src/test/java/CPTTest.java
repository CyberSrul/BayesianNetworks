import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;

class CPTTest {

    static BayesNode A, B, C, D;
    static double[] probabilities1, probabilities2;
    static CPT cpt1, factor, cpt2;


    @BeforeEach
    void setUp() {

        A = new BayesNode(0, new int[]{0, 1});
        B = new BayesNode(1, new int[]{0, 1});
        C = new BayesNode(2, new int[]{0, 1, 2});
        D = new BayesNode(3, new int[]{1, 2});
        probabilities1 = new double[12];    Arrays.fill(probabilities1, 0.05);
        probabilities2 = new double[8];     Arrays.fill(probabilities2, 0.05);

        // no data
        Assertions.assertThrows(InputMismatchException.class, () -> new CPT(new LinkedList<>(), new double[]{}));
        // cpt1
        Assertions.assertDoesNotThrow(() -> { cpt1 = new CPT(List.of(A, B, C), probabilities1); });
        // cpt2
        Assertions.assertDoesNotThrow(() -> { cpt2 = new CPT(List.of(B, A, D), probabilities2); });
    }

    @Test
    void factor() {

        Assertions.assertDoesNotThrow(() -> { factor = cpt1.factor(List.of(B), new int[]{1}); });
        System.out.println(factor);
        Assertions.assertDoesNotThrow(() -> { factor = cpt1.factor(List.of(B, C), new int[]{1, 2}); });
        System.out.println(factor);
        Assertions.assertDoesNotThrow(() -> { factor = cpt2.factor(List.of(B), new int[]{1}); });
        System.out.println(factor);
        Assertions.assertDoesNotThrow(() -> { factor = cpt2.factor(List.of(B, A), new int[]{1, 1}); });
        System.out.println(factor);
    }

    @Test
    void join(){

        Assertions.assertDoesNotThrow(() -> { factor = cpt1.join(cpt2); });
        System.out.println(factor);
        Assertions.assertDoesNotThrow(() -> { factor = cpt1.factor(List.of(A, B), new int[]{0, 0}).join(cpt1); });
        System.out.println(factor);
    }

    @Test
    void sum(){

        Assertions.assertDoesNotThrow(() -> { factor = cpt1.sum(A); });
        System.out.println(factor);
        Assertions.assertDoesNotThrow(() -> { factor = cpt1.sum(A).sum(C); });
        System.out.println(factor);
        Assertions.assertDoesNotThrow(() -> { factor = cpt2.sum(D); });
        System.out.println(factor);
        Assertions.assertDoesNotThrow(() -> { factor = cpt2.sum(A).sum(B); });
        System.out.println(factor);
    }
}