import java.security.KeyException;

public class Main {

    public static void main(String[] args) throws KeyException {

        String dir1 = "src/main/resources/alarm_net.xml";
        String dir2 = "src/main/resources/big_net.xml";

        BayesiaNetwork network1 = new BayesiaNetwork(), network2 = new BayesiaNetwork();
        network1.switchNetwork(dir1);
        System.out.println(network2);
        Bayesian_Inference_Algo naive = new Naive_Bayesian_Inference(network1);
        System.out.println(naive.Query("B=T|J=T,M=T"));
        System.out.println(naive.Query("J=T|B=T"));
        System.out.println(naive.Query("A=F|"));
        System.out.println(naive.Query("M=T|J=T,B=T"));
        Bayesian_Inference_Algo VE = new VariableElimination(network1);
        System.out.println(VE.Query("B=T|J=T,M=T"));
        System.out.println(VE.Query("J=T|B=T"));
        System.out.println(naive.Query("A=F|"));
        System.out.println(naive.Query("M=T|J=T,B=T"));
        network2.switchNetwork(dir2);
        System.out.println(network1);
        System.out.println(naive.Query("B0=v3|C3=T,B2=F,C2=v3"));
        System.out.println(naive.Query("A2=T|C2=v1"));
        System.out.println(naive.Query("D1=T|C2=v1,C3=F"));
        System.out.println(VE.Query("B0=v3|C3=T,B2=F,C2=v3"));
        System.out.println(VE.Query("A2=T|C2=v1"));
        System.out.println(VE.Query("D1=T|C2=v1,C3=F"));
    }
}

//TODO: parse, invoke, rounding format package, tests ( just numeric output and exceptions )