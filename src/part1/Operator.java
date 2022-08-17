package part1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Operator class to store internal nodes of the
 * binary expression tree which are boolean operators
 */
public class Operator {
    protected final String symbol; // symbol of the operator
    public Boolean bool; // boolean value of the current operator node
    private final Operator left; // left child of the operator node
    private final Operator right; // right child of the operator node


    /**
     * Operator constructor
     *
     * @param symbol symbol of the operator
     */
    public Operator(String symbol) {
        this.symbol = symbol;
        this.bool = null;
        this.left = null;
        this.right = null;
    }

    /**
     * Operator constructor
     *
     * @param symbol symbol of the operator
     * @param left left child of the operator node
     * @param right right child of the operator node
     */
    public Operator(String symbol, Operator left, Operator right) {
        this.symbol = symbol;
        this.bool = null;
        this.left = left;
        this.right = right;
    }

    /**
     * Print the operator structure
     *
     * @param ws String to keep track of the indentation for
     *               printing the branch predicate structure
     */
    public void print(String ws) {
        System.out.println(ws + this.symbol); // printing symbol
        ws = ws + " ".repeat(this.symbol.length()); // adding indentation
        if (this.left != null) { // print subtrees
            this.left.print(ws);
        }
        if (this.right != null) {
            this.right.print(ws);
        }
    }

    /**
     * Evaluate the operator with child nodes and the operator symbol,
     * change the bool value of the operator
     */
    public void evaluate() {

        // Evaluate subtrees
        if (this.left != null) {
            this.left.evaluate();
        }
        if (this.right != null) {
            this.right.evaluate();
        }
        if (!(this instanceof Condition))
            // Binary operators
            if (this.left != null && this.right != null) {
                switch (this.symbol) { // get the boolean value by evaluating operator with the subtrees
                    case "==":
                        this.bool = this.left.bool == this.right.bool;
                        break;
                    case "!=":
                        this.bool = this.left.bool != this.right.bool;
                        break;
                    case "||":
                        this.bool = this.left.bool || this.right.bool;
                        break;
                    default:
                        this.bool = this.left.bool && this.right.bool;
                }
            }
            // Unary operators
            else if (this.left == null && this.right != null) {
                this.bool = !this.right.bool;
            } else if (this.left != null) {
                this.bool = !this.left.bool;
            } else {
                this.bool = null;
            }
    }


    /**
     * Get Restricted MCDC test requirements for the current branch predicate
     *
     * @return Set of Restricted MCDC test requirements
     */
    public HashSet<boolean[]> getRestrictedMCDC() {
        HashSet<boolean[]> result = new HashSet<>(); // result set
        ArrayList<Condition> conditions = getConditions(new ArrayList<>()); // list of conditions in the branch predicate
        ArrayList<boolean[]> requirements = getRequirements(); // list of all possible requirements

        for (int i = 0; i < conditions.size(); i++) {
            ArrayList<boolean[][]> reqPairs = new ArrayList<>(); // comparing every requirement
            for (boolean[] req1 : requirements) {
                for (boolean[] req2 : requirements) {
                    if (req1 != req2) {
                        boolean valid = true;
                        for (int j = 0; j < req1.length - 1; j++) { // searching for valid requirement pairs for every condition
                            if (j != i && req1[j] != req2[j]) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid && req1[i] != req2[i] && req1[req1.length - 1] != req2[req2.length - 1]) {
                            boolean[][] pair = new boolean[][]{req1, req2}; // if valid pair found add them to the pair list
                            reqPairs.add(pair);
                        }
                    }
                }
            }
            if (reqPairs.size() > 0) { // add the first pair to the result
                result.add(reqPairs.get(0)[0]);
                result.add(reqPairs.get(0)[1]);
            }
        }
        return result;
    }

    /**
     * Get Correlated MCDC test requirements for the current branch predicate
     *
     * @return Set of Correlated MCDC test requirements
     */
    public HashSet<boolean[]> getCorrelatedMCDC() {
        HashSet<boolean[]> result = new HashSet<>(); // result set
        ArrayList<Condition> conditions = getConditions(new ArrayList<>()); // list of conditions in the branch predicate
        ArrayList<HashSet<boolean[]>> conditionReqs = new ArrayList<>(); // list of sets of valid requirements

        ArrayList<boolean[]> requirements = getRequirements(); // list of all possible requirements
        for (int i = 0; i < conditions.size(); i++) {
            HashSet<boolean[]> reqSet = new HashSet<>(); // comparing every requirement
            for (boolean[] req1 : requirements) {
                for (boolean[] req2 : requirements) {
                    if (req1 != req2) {
                        boolean valid = false;
                        for (int j = 0; j < req1.length - 1; j++) { // searching for valid requirement pairs for every condition
                            if (j == i && req1[j] != req2[j] && req1[req1.length - 1] != req2[req2.length - 1]) {
                                valid = true;
                                break;
                            }
                        }
                        if (valid) { // if valid add requirements to the set of valid requirements
                            reqSet.add(req1);
                            reqSet.add(req2);
                        }
                    }
                }
            }
            // for each condition add set of valid requirements to the list of sets of valid requirements
            conditionReqs.add(reqSet);
        }
        // get the common requirements for all the conditions
        HashSet<boolean[]> intersectSet = new HashSet<>(conditionReqs.get(0));

        for (int i = 1; i < conditionReqs.size(); i++) {
            intersectSet.retainAll(conditionReqs.get(i));
        }
        // try to find the smallest set of requirements that can flip boolean values of the all conditions and result
        for (boolean[] res1 : intersectSet) {
            for (boolean[] res2 : intersectSet) {
                boolean flag = true;
                if (res1 != res2) {
                    for (int i = 0; i < res1.length; i++) {
                        if (res1[i] == res2[i]) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        result.add(res1);
                        result.add(res2);
                    }
                }
            }
            if (result.size() > 0) {
                break;
            }
        }
        return result;
    }

    /**
     * Get conditions of the branch predicate
     * with eliminating equivalent conditions
     *
     * @param res current list of conditions
     * @return list of valid conditions
     */
    public ArrayList<Condition> getConditions(ArrayList<Condition> res) {

        if (this instanceof Condition) { // check the list of conditions for equivalences
            boolean eqAndInv = ((Condition) this).checkEquivalences(res);
            // add to the result if there is no equivalent conditions in the current list of conditions
            if (!(eqAndInv) || res.size() == 0) {
                res.add((Condition) this);
            }
            return res;
        } else {
            // get conditions for subtrees
            if (this.left != null) {
                this.left.getConditions(res);
            }
            if (this.right != null) {
                this.right.getConditions(res);
            }
        }
        // eliminate identical conditions and return list of valid conditions
        HashSet<Condition> conSet = new HashSet<>(res);
        res = new ArrayList<>(conSet);
        return res;
    }

    /**
     * Get all possible test requirements with evaluating each one
     *
     * @return list of all possible test requirements
     */
    public ArrayList<boolean[]> getRequirements() {
        ArrayList<boolean[]> result = new ArrayList<>(); // list of requirements
        ArrayList<Condition> conList = this.getConditions(new ArrayList<>()); // list of conditions
        boolean[][] truthTable = getTruthTable(conList.size()); // get truth table with the number of conditions

        for (boolean[] row : truthTable) { // evaluating each requirement and getting results from them
            boolean[] req = new boolean[row.length + 1];
            // set the boolean value of each condition the given boolean value in the requirement
            for (int i = 0; i < row.length; i++) {
                Condition con = conList.get(i);
                con.bool = row[i];
                for (Condition eq : con.equivalences) { // set the boolean value of equivalent and opposite conditions
                    eq.bool = row[i];
                }
                for (Condition inv : con.inverses) {
                    inv.bool = !row[i];
                }
                req[i] = row[i];
            }
            this.evaluate(); // evaluate branch predicate with values for the conditions which is given in the requirement
            req[req.length - 1] = this.bool; // storing result of the branch predicate in the last index of the array
            result.add(req); // add requirement and its result to the list of requirements
        }
        return result;
    }

    /**
     *  Get truth table for the given number of conditions
     *
     * @param numberOfConditions number of conditions
     * @return 2D list of truth values
     */
    protected boolean[][] getTruthTable(int numberOfConditions) {
        int numberOfCombinations = (int) Math.pow(2, numberOfConditions); // number of rows in the table
        boolean[][] table = new boolean[numberOfCombinations][numberOfConditions]; // create empty table

        for (int i = 0; i < numberOfCombinations; i++) { // fill the table
            for (int j = 0; j < numberOfConditions; j++) {
                table[i][j] = 1 != (i / (int) Math.pow(2, j)) % 2;
            }
        }
        return table;
    }

    /**
     * Print test requirements for the Restricted MCDC or
     * Correlated MCDC to the console, depending on the parameter
     *
     * @param restricted boolean value to choose which MCDC
     *                   requirements will be printed to the console
     */
    public void printTestRequirements(boolean restricted) {
        HashSet<boolean[]> requirements;
        if (restricted) {
            requirements = this.getRestrictedMCDC(); // getting restricted MCDC requirements
            System.out.println();
            System.out.println("Restricted MCDC test requirements: ");
        }
        else {
            requirements = this.getCorrelatedMCDC(); // getting correlated MCDC requirements
            System.out.println();
            System.out.println("Correlated MCDC test requirements: ");
        }
        for (boolean[] req : requirements) { // print requirements
            System.out.println(Arrays.toString(req));
        }
    }

    /**
     * Print valid conditions for the branch predicate
     */
    public void printConditions() {
        ArrayList<Condition> conditions = this.getConditions(new ArrayList<>()); // list of conditions

        int numberOfCons = 1;
        for (Condition con : conditions) { // printing conditions
            System.out.println("============ Condition " + numberOfCons + " =============");
            con.print("");
            numberOfCons++;
        }
    }
}
