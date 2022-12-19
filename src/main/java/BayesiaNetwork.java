import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.*;

import java.security.KeyException;
import java.util.stream.Collectors;

/**
 * A Bayesian Network is a DAG that represents the joint probability distribution
 * of all the relevant random variables in a belief system s.t:
 * every variable is independent of all others GIVEN the state of its parents ( except its children ).
 * In other words: it describes well the causal relationships in the system
 * <p>
 * In this implementation, all the data for the network is stored in a xml with a specific format.
 * The builder of the class parses the data into BayesNode instances using Java's DOM parser.
 * <p>
 * The class implements the singleton design pattern because an instance can be very heavy,
 * and it's absurd to give any running thread that executes an algorithm on a bayesian network
 * its own instance because this is a static implementation, in the sense that it doesn't have setters.
 * **/

public class BayesiaNetwork implements Iterable<BayesNode>{

    private static Dictionary dict;                          // giving translation to each name
    private static HashMap<Integer, BayesNode> variables;    // the actual nodes
    static {dict = null; variables = null;}
    public BayesiaNetwork(){}

    // The only setter is a method that changes the entire network
    public void switchNetwork(String dir) throws KeyException {

        // these lists will hold all the info
        NodeList nodes = null, CPTs = null;
        // parsing xml
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(dir);
            doc.getDocumentElement().normalize();

            nodes = doc.getElementsByTagName("VARIABLE");
            CPTs = doc.getElementsByTagName("DEFINITION");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (nodes == null || CPTs == null) { throw new InputMismatchException("something is wrong with the xml format"); }

        // now we can get started
        dict = new Dictionary();
        variables = new HashMap<>();
        // the bayesian network instance can be quit heavy so prompting the garbage collector
        System.gc();

        parse(nodes, CPTs);
    }

    // iterative method for parsing the xml and deploying the data in the Network
    private void parse(NodeList nodes, NodeList CPTs) throws KeyException {

        int var_ind, val_ind;
        String name, value;          // name and value of variable
        int[] values;                // range of values of variables
        Element element;             // intermediary steps of the parsing
        BayesNode variable, parent;
        double[] probabilities;

        for (var_ind = 0; var_ind < nodes.getLength(); ++var_ind) {

            element = (Element) nodes.item(var_ind);

            name = element.getElementsByTagName("NAME").item(0).getTextContent();
            dict.log(name);

            NodeList range = element.getElementsByTagName("OUTCOME");
            values = new int[range.getLength()];
            for (val_ind = 0; val_ind < range.getLength(); ++val_ind) {

                value = range.item(val_ind).getTextContent();
                dict.log(value);
                values[val_ind] = dict.translate(value);
            }

            variables.put(dict.translate(name), new BayesNode(dict.translate(name), values));
        }
        // now the CPTs and consequently the edges
        for (var_ind = 0; var_ind < nodes.getLength(); ++var_ind) {

            element = (Element) CPTs.item(var_ind);
            // get relevant variable by name
            variable = variables.get(dict.translate(element.getElementsByTagName("FOR").item(0).getTextContent()));

            NodeList parents = element.getElementsByTagName("GIVEN");
            // edges
            for (val_ind = parents.getLength() -1; val_ind >= 0; --val_ind) {

                parent = variables.get(dict.translate(parents.item(val_ind).getTextContent()));
                variable.addParent(parent);
                parent.addKid(variable);
            }
            // probabilities
            probabilities = Arrays.stream(element.getElementsByTagName("TABLE").item(0).getTextContent().split(" ")).mapToDouble(Double::valueOf).toArray();
            variable.setCPT(probabilities);
        }
    }

    public String toString(){ return variables.values().stream().map(BayesNode::toString).reduce("", (s1, s2) -> s1 + s2); }

    /* interface methods */

    public int size(){ return variables.size(); }

    public int translate(String name) throws KeyException { return dict.translate(name); }
    public BayesNode getVariable(String name) throws KeyException { return variables.get(this.translate(name)); }

    public Iterator<String> getNames(){ return dict.iterator(); }
    // unless the input xml is formatted incorrectly, this is topologically ordered
    @Override
    public Iterator<BayesNode> iterator() {
        return variables.values().iterator();
    }


    /* Graph Search Algorithms */

    public void clearMarks(){ variables.values().forEach((var) -> var.setMark(false)); }

    public List<BayesNode> getAncestors(LinkedList<BayesNode> sources){

        this.clearMarks();

        int ind = 0;
        while (ind < sources.size()){

            BayesNode src = sources.get(ind);
            src.setMark(true);

            for (BayesNode parent : src.getParents()){

                if (! parent.getMark()) sources.add(parent);
                parent.setMark(true);
            }

            ++ind;
        }
        return variables.values().stream().filter(BayesNode::getMark).collect(Collectors.toList());
    }
}