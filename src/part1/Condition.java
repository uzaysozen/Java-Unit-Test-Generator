package part1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 * Condition class to store conditions which are
 * leafs of the binary expression tree structure
 */
public class Condition extends Operator {
    protected Var var1; // First variable
    protected Var var2; // Second variable
    protected HashSet<Condition> equivalences; // Conditions that will get the same boolean value
    protected HashSet<Condition> inverses; // Conditions that will get the opposite boolean value

    /**
     * Condition constructor
     *
     * @param symbol Symbol of the condition operator
     * @param var1 First variable
     * @param var2 Second variable
     */
    public Condition(String symbol, Var var1, Var var2) {
        super(symbol);
        this.var1 = var1;
        this.var2 = var2;
        this.equivalences = new HashSet<>();
        this.inverses = new HashSet<>();
    }

    /**
     * Print the condition structure
     *
     * @param ws String to keep track of the indentation for
     *           printing the branch predicate structure
     */
    @Override
    public void print(String ws) {
        System.out.println(ws + this.symbol); // printing symbol
        ws = ws + " ".repeat(this.symbol.length()); // adding indentation
        System.out.println(ws + this.var1.name); // printing name of the first variable
        System.out.println(ws + this.var2.name); // printing name of the second variable
    }

    /**
     * Evaluate the condition using variables and the operator symbol,
     * change the bool value of the condition
     */
    @Override
    public void evaluate() {
        if (this.bool == null) { // only evaluate if the boolean value has not been found
            switch (this.symbol) { // get the boolean value by evaluating operator with the variables
                case "<":
                    this.bool = this.var1.value < this.var2.value;
                    break;
                case ">":
                    this.bool = this.var1.value > this.var2.value;
                    break;
                case ">=":
                    this.bool = this.var1.value >= this.var2.value;
                    break;
                case "<=":
                    this.bool = this.var1.value <= this.var2.value;
                    break;
                case "!=":
                    this.bool = this.var1.value != this.var2.value;
                    break;
                default:
                    this.bool = this.var1.value == this.var2.value;
            }
        }
    }

    /**
     * Check equivalent and inverse conditions
     * to eliminate from the list of major conditions
     *
     * @param conlist Current list of the conditions
     * @return true if equivalence is found else false
     */
    protected boolean checkEquivalences(ArrayList<Condition> conlist) {
        for (Condition c : conlist) {
            if (c.equals(this)) {
                c.equivalences.add(this);
                return true;
            }
            if (((c.symbol.equals("==") && this.symbol.equals("==")) ||
                    (c.symbol.equals("!=") && this.symbol.equals("!="))) &&
                    ((c.var1 == this.var2 && c.var2 == this.var1) ||
                            (this.var1 == c.var1 && this.var2 == c.var2))) {
                c.equivalences.add(this);
                return true;
            }
            if (((c.symbol.equals(">") && this.symbol.equals("<")) ||
                    (c.symbol.equals("<") && this.symbol.equals(">"))) &&
                    ((c.var1 == this.var2 && c.var2 == this.var1))) {
                c.equivalences.add(this);
                return true;
            } else if (((c.symbol.equals("<") && this.symbol.equals(">=")) ||
                    (c.symbol.equals(">") && this.symbol.equals("<=") ||
                            (c.symbol.equals(">=") && this.symbol.equals("<")) ||
                            (c.symbol.equals("<=") && this.symbol.equals(">"))) &&
                            ((c.var1 == this.var1 && this.var2 == c.var2)))) {
                c.inverses.add(this);
                return true;
            } else if (((c.symbol.equals("!=") && this.symbol.equals("==")) ||
                    (c.symbol.equals("==") && this.symbol.equals("!=")) &&
                            ((c.var1 == this.var1 && this.var2 == c.var2) ||
                                    (c.var2 == this.var1 && this.var2 == c.var1)))) {
                c.inverses.add(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return var1.equals(condition.var1) && var2.equals(condition.var2) && equivalences.equals(condition.equivalences) && inverses.equals(condition.inverses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(var1, var2, equivalences, inverses);
    }
}
