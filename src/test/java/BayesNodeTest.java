import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class BayesNodeTest {

    static BayesNode A, B, C;

    @BeforeEach
    void setUp() {

        A = new BayesNode(0, new int[]{0, 1});
        B = new BayesNode(1, new int[]{0, 1});
        C = new BayesNode(2, new int[]{0, 1, 2});
        A.addKid(B); A.addKid(C);
        B.addParent(A); C.addParent(A);
    }

    @Test
    void getName() {

        Assertions.assertEquals(0, A.getName());
        Assertions.assertEquals(1, B.getName());
        Assertions.assertEquals(2, C.getName());
    }

    @Test
    void getRangeSize() {

        Assertions.assertEquals(2, A.getRangeSize());
        Assertions.assertEquals(2, B.getRangeSize());
        Assertions.assertEquals(3, C.getRangeSize());
    }

    @Test
    void getValue() {

        Assertions.assertEquals(0, A.getValue(0));
        Assertions.assertEquals(1, A.getValue(1));
        Assertions.assertEquals(0, B.getValue(0));
        Assertions.assertEquals(1, B.getValue(1));
        Assertions.assertEquals(0, C.getValue(0));
        Assertions.assertEquals(1, C.getValue(1));
        Assertions.assertEquals(2, C.getValue(2));
    }

    @Test
    void getKid() {

        Assertions.assertEquals(B, A.getKid(1));
        Assertions.assertEquals(C, A.getKid(2));
        // no such kid
        Assertions.assertNull(A.getKid(3));

        Assertions.assertEquals(List.of(B, C), A.getKids());
    }

    @Test
    void getParent() {

        Assertions.assertEquals(A, B.getParent(0));
        Assertions.assertEquals(A, C.getParent(0));
        // no such parent
        Assertions.assertNull(B.getParent(2));

        Assertions.assertEquals(List.of(A), B.getParents());
        Assertions.assertEquals(List.of(A), C.getParents());
    }

    @Test
    void AreParents(){

        Assertions.assertTrue(B.AreParents(List.of(A)));
        Assertions.assertTrue(C.AreParents(List.of(A)));
    }

    @Test
    void addKid() {

        BayesNode D = new BayesNode(3, new int[]{2, 3});
        Assertions.assertDoesNotThrow(() -> B.addKid(D));
        Assertions.assertEquals(D, B.getKid(3));
        Assertions.assertThrows(RuntimeException.class, () -> B.addKid(D) );
        Assertions.assertThrows(RuntimeException.class, () -> B.addParent(D) );
    }

    @Test
    void addParent() {

        BayesNode D = new BayesNode(3, new int[]{2, 3});
        Assertions.assertDoesNotThrow(() -> B.addParent(D));
        Assertions.assertEquals(D, B.getParent(3));
        Assertions.assertThrows(RuntimeException.class, () -> B.addKid(D) );
        Assertions.assertThrows(RuntimeException.class, () -> B.addParent(D) );
    }

    @Test
    void mark(){

        Assertions.assertFalse(A.getMark());
        Assertions.assertFalse(B.getMark());
        Assertions.assertFalse(C.getMark());
        Assertions.assertDoesNotThrow(() -> A.setMark(true));
        Assertions.assertTrue(A.getMark());
        Assertions.assertFalse(B.getMark());
        Assertions.assertFalse(C.getMark());
        Assertions.assertDoesNotThrow(() -> B.setMark(true));
        Assertions.assertTrue(B.getMark());
        Assertions.assertFalse(C.getMark());
        Assertions.assertDoesNotThrow(() -> C.setMark(true));
        Assertions.assertTrue(C.getMark());

    }

    @Test
    void CPT_methods() {

        Assertions.assertDoesNotThrow(() -> A.setCPT(new double[]{0.1, 0.9}));
        System.out.println(A);
        Assertions.assertDoesNotThrow(() -> B.setCPT(new double[]{0.2, 0.8, 0.3, 0.7}));
        System.out.println(B);
        Assertions.assertDoesNotThrow(() -> C.setCPT(new double[]{0.33, 0.33, 0.33, 0.2, 0.6, 0.2}));
        System.out.println(C);

        // fetch values from CPT
        Assertions.assertEquals(0.2, C.fetch(new int[]{2, 1}));
        Assertions.assertEquals(0.6, C.fetch(new int[]{1, 1}));
        Assertions.assertEquals(0.2, B.fetch(new int[]{0, 0}));
        Assertions.assertEquals(0.9, A.fetch(new int[]{1}));
    }
}