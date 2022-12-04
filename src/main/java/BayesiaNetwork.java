import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Arrays;
import java.util.HashMap;

import java.security.KeyException;
import java.util.InputMismatchException;

/**
 * A Bayesian Network is a DAG that represents the joint probability distribution
 * of all the relevant random variables in a belief system s.t:
 * every variable is independent of all others GIVEN the state of its parents ( except its children ).
 * In other words: it describes well the causal relationships in the system
 * <p>
 * In this implementation, all the data for the network is stored in a xml with a specific format.
 * The builder of the class parses the data into BayesNode instances using Java's DOM parser.
 * <p>
 * The class supports a method for answering probability queries
 * First: it parses the text into arguments for another method that does most of the job,
 * It does so with a dictionary ( see Dictionary's documentation ).
 * Then invokes the method.
 * **/

public class BayesiaNetwork {

    private final Dictionary dict;                          // giving translation to each name
    private final HashMap<Integer, BayesNode> variables;    // the actual nodes

    public BayesiaNetwork(String dir) throws KeyException {

        // these lists will hold all the info
        NodeList variables = null, CPTs = null;
        // parsing xml
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(dir);
            doc.getDocumentElement().normalize();

            variables = doc.getElementsByTagName("VARIABLE");
            CPTs = doc.getElementsByTagName("DEFINITION");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (variables == null || CPTs == null) { throw new InputMismatchException("something is wrong with the xml format"); }

        // now we can get started
        this.dict = new Dictionary();
        this.variables = new HashMap<>();

        parse(variables, CPTs);
    }

    // iterative method for parsing the xml and deploying the data in the Network
    private void parse(NodeList variables, NodeList CPTs) throws KeyException {

        int var_ind, val_ind;
        String name, value;          // name and value of variable
        int[] values;                // range of values of variables
        Element element;             // intermediary steps of the parsing
        BayesNode variable, parent;
        double[] probabilities;

        for (var_ind = 0; var_ind < variables.getLength(); ++var_ind) {

            element = (Element) variables.item(var_ind);

            name = element.getElementsByTagName("NAME").item(0).getTextContent();
            this.dict.log(name);

            NodeList range = element.getElementsByTagName("OUTCOME");
            values = new int[range.getLength()];
            for (val_ind = 0; val_ind < range.getLength(); ++val_ind) {

                value = range.item(val_ind).getTextContent();
                this.dict.log(value);
                values[val_ind] = this.dict.translate(value);
            }

            this.variables.put(this.dict.translate(name), new BayesNode(this.dict.translate(name), values));
        }
        // now the CPTs and consequently the edges
        for (var_ind = 0; var_ind < variables.getLength(); ++var_ind) {

            element = (Element) CPTs.item(var_ind);
            // get relevant variable by name
            variable = this.variables.get(this.dict.translate(element.getElementsByTagName("FOR").item(0).getTextContent()));

            NodeList parents = element.getElementsByTagName("GIVEN");
            // edges
            for (val_ind = 0; val_ind < parents.getLength(); ++val_ind) {

                parent = this.variables.get(this.dict.translate(parents.item(val_ind).getTextContent()));
                variable.addParent(parent);
                parent.addKid(variable);
            }
            // probabilities
            probabilities = Arrays.stream(element.getElementsByTagName("TABLE").item(0).getTextContent().split(" ")).mapToDouble(Double::valueOf).toArray();
            variable.setCPT(probabilities);
        }
    }

    public String toString(){ return this.variables.values().stream().map(BayesNode::toString).reduce("", (s1, s2) -> s1 + s2); }

    //TODO: Algo Factory / Abstract class ( research )
}