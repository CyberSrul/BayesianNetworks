import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class BayesiaNetworkTest {

    static final BayesiaNetwork network = new BayesiaNetwork();

    @BeforeAll
    static void setUp() {

        Assertions.assertDoesNotThrow(() -> network.switchNetwork("src/main/resources/big_net.xml"));
        System.out.println(network);
        Assertions.assertEquals(11, network.size());
        Assertions.assertDoesNotThrow(() -> network.switchNetwork("src/main/resources/alarm_net.xml"));
        Assertions.assertEquals(5, network.size());
        System.out.println(network);
    }

    @Test
    void translate() throws KeyException {

        Assertions.assertEquals(0, network.translate("E"));
        Assertions.assertEquals(1, network.translate("T"));
        Assertions.assertEquals(2, network.translate("F"));
        Assertions.assertEquals(3, network.translate("B"));
        Assertions.assertEquals(4, network.translate("A"));
        Assertions.assertEquals(5, network.translate("J"));
        Assertions.assertEquals(6, network.translate("M"));
    }

    @Test
    void search_operations() throws KeyException {

        // working through an example scenario

        CPT J = network.getVariable("J").getCPT().factor(List.of(network.getVariable("J")), new int[]{1});
        CPT M = network.getVariable("M").getCPT().factor(List.of(network.getVariable("M")), new int[]{1});
        CPT A = network.getVariable("A").getCPT();
        CPT E = network.getVariable("E").getCPT();
        CPT B = network.getVariable("B").getCPT();

        CPT joined = J.join(A).join(M).sum(network.getVariable("A"));
        System.out.println(joined);
        joined = joined.join(B);
        System.out.println(joined);
        joined = joined.join(E);
        System.out.println(joined);
        joined = joined.sum(network.getVariable("E"));
        System.out.println(joined);
        System.out.println(joined.fetch(List.of(network.getVariable("B")), new int[]{1}) / joined.totalSum());
    }

    @Test
    void marks(){

        Assertions.assertDoesNotThrow(network::clearMarks);
        for (BayesNode var : network) Assertions.assertFalse(var.getMark());
        for (BayesNode var : network) var.setMark(true);
        for (BayesNode var : network) Assertions.assertTrue(var.getMark());
        Assertions.assertDoesNotThrow(network::clearMarks);
        for (BayesNode var : network) Assertions.assertFalse(var.getMark());
    }

    @Test
    void getAncestors() throws KeyException {

        Assertions.assertEquals(List.of(network.getVariable("E"), network.getVariable("B"), network.getVariable("A")),
                                network.getAncestors(new LinkedList<>(Collections.singletonList(network.getVariable("A")))));

        Assertions.assertEquals(List.of(network.getVariable("E"), network.getVariable("B"), network.getVariable("A"), network.getVariable("J")),
                network.getAncestors(new LinkedList<>(Collections.singletonList(network.getVariable("J")))));

        Assertions.assertEquals(List.of(network.getVariable("E"), network.getVariable("B"), network.getVariable("A"), network.getVariable("J")),
                network.getAncestors(new LinkedList<>(Arrays.asList(network.getVariable("J"), network.getVariable("A")))));
    }
}