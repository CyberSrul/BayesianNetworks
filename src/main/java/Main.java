import java.security.KeyException;

public class Main {

    public static void main(String[] args) throws KeyException {

        String dir1 = "src/main/resources/alarm_net.xml";
        String dir2 = "src/main/resources/big_net.xml";

        BayesiaNetwork network1 = new BayesiaNetwork(), network2 = new BayesiaNetwork();
        network1.switchNetwork(dir1);
        System.out.println(network2);
        network2.switchNetwork(dir2);
        System.out.println(network1);

    }
}

//TODO: parse, invoke, rounding format package, tests ( just numeric output and exceptions )