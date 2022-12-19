import java.util.*;

import java.util.stream.Collectors;

/* A data oriented class representing a node in a Bayesian Network (a random variable) */

public class BayesNode {

    private final int name;
    private final int[] range;                      // possible values of variable
    private final List<BayesNode> parents, kids;
    private CPT cpt;

    public BayesNode(int name, int[] values){

        this.name = name;
        this.range = Arrays.copyOf(values, values.length);
        this.parents = new LinkedList<>();
        this.kids = new LinkedList<>();
        this.cpt = null;
    }

    public int getName() { return this.name; }
    public int getValue(int ind){ return this.range[ind]; }
    public int getRangeSize(){ return this.range.length; }

    public void addKid(BayesNode kid){

        if (this.kids.contains(kid)){
            throw new InputMismatchException("bayes node " + this.getName() + "already has a kid named " + kid.getName());
        }
        if (this.parents.contains(kid)){
            throw new RuntimeException("Bayesian Networks are Acyclic");
        }
        this.kids.add(kid);
    }
    public void addParent(BayesNode parent){

        if (this.parents.contains(parent)){
            throw new InputMismatchException("bayes node " + this.getName() + "already has a parent named " + parent.getName());
        }
        if (this.kids.contains(parent)){
            throw new RuntimeException("Bayesian Networks are Acyclic");
        }
        this.parents.add(parent);
    }

    public BayesNode getKid(int name){ return this.kids.stream().filter((var) -> var.getName() == name).findAny().orElse(null); }
    public BayesNode getParent(int name){ return this.parents.stream().filter((var) -> var.getName() == name).findAny().orElse(null); }
    public CPT getCPT(){ return this.cpt; }

    // these are the parents of this BayesNode no one is missing, all of them belong.
    public boolean AreParents(List<BayesNode> variables){
        return variables.stream().map(BayesNode::getName).sorted().collect(Collectors.toList())
               .equals(List.copyOf(this.parents).stream().map(BayesNode::getName).sorted().collect(Collectors.toList()));
    }

    public void setCPT(double[] probabilities){

        List<BayesNode> variables = new ArrayList<>(List.copyOf(this.parents));
        variables.add(0, this);
        this.cpt = new CPT(variables, probabilities);
    }

    // get value of specific row in cpt
    public double fetch(int[] values){

        List<BayesNode> variables = new ArrayList<>(List.copyOf(this.parents));
        variables.add(0, this);
        return this.cpt.fetch(variables, values);
    }

    public String toString(){ return "BayesNode " + this.name + "\n" + this.cpt.toString() + "\n"; }
}