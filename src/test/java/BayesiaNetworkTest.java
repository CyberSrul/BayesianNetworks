import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyException;
import java.util.List;

class BayesiaNetworkTest {

    static final BayesiaNetwork network = new BayesiaNetwork();
    static final String dir1 = "src/main/resources/alarm_net.xml";
    static final String dir2 = "src/main/resources/big_net.xml";

    @BeforeAll
    static void setUp() {

        Assertions.assertDoesNotThrow(() -> network.switchNetwork(dir2));
        System.out.println(network);
        Assertions.assertDoesNotThrow(() -> network.switchNetwork(dir1));
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
}