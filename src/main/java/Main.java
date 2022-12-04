import java.security.KeyException;

public class Main {

    public static void main(String[] args) throws KeyException {

        BayesiaNetwork network = new BayesiaNetwork("src/main/resources/alarm_net.xml");
        System.out.println(network);
    }
}

//TODO: parse, invoke, rounding format package, tests ( just numeric output and exceptions )