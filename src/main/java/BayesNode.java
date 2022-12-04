import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/* A data oriented class representing a node in a Bayesian Network (a random variable) */

public class BayesNode {

    private final int name;
    private final int[] range;                                  // possible values of variable
    private final HashMap<Integer, BayesNode> parents, kids;    // name -> node
    private CPT cpt;

    public BayesNode(int name, int[] values){

        this.name = name;
        this.range = Arrays.copyOf(values, values.length);
        this.parents = new HashMap<>();
        this.kids = new HashMap<>();
        this.cpt = null;
    }

    public int getName() { return this.name; }
    public int getValue(int ind){ return this.range[ind]; }
    public int getRangeSize(){ return this.range.length; }

    public void addKid(BayesNode kid){

        if (this.kids.containsKey(kid.getName())){
            throw new RuntimeException("bayes node " + this.getName() + "already has a kid named " + kid.getName());
        }
        if (this.parents.containsKey(kid.getName())){
            throw new RuntimeException("Bayesian Networks are Acyclic");
        }
        this.kids.put(kid.getName(), kid);
    }
    public void addParent(BayesNode parent){

        if (this.parents.containsKey(parent.getName())){
            throw new RuntimeException("bayes node " + this.getName() + "already has a parent named " + parent.getName());
        }
        if (this.kids.containsKey(parent.getName())){
            throw new RuntimeException("Bayesian Networks are Acyclic");
        }
        this.parents.put(parent.getName(), parent);
    }

    public BayesNode getKid(int name){ return this.kids.get(name); }
    public BayesNode getParent(int name){ return this.parents.get(name); }

    public void setCPT(double[] probabilities){

        List<BayesNode> variables = new LinkedList<>(this.parents.values());
        variables.add(0, this);
        this.cpt = new CPT(variables, probabilities);

    }

    public String toString(){ return "BayesNode " + this.name + "\n" + this.cpt.toString() + "\n"; }

    //ToDo: get iterator for net, factor, simple queries
}