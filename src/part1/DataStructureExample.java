package part1;

public class DataStructureExample {

    public static void main(String[] args) {
        // Initialize inputs
        int side1 = 4;
        int side2 = 4;
        int side3 = 5;
        // Initialize condition variables
        Var var1 = new Var("side1", side1);
        Var var2 = new Var("side2", side2);
        Var var3 = new Var("side3", side3);
        Var var4 = new Var("side1 + side2", side1 + side2);
        // Initialize conditions (external nodes)
        Condition greater = new Condition(">", var4, var3);
        Condition eq1 = new Condition("==", var1, var2);
        Condition eq2 = new Condition("==", var2, var3);
        Condition noteq1 = new Condition("!=", var1, var2);
        Condition noteq2 = new Condition("!=", var2, var3);
        // Initialize operators (internal nodes)
        Operator or1 = new Operator("||", eq1, eq2);
        Operator or2 = new Operator("||", noteq1, noteq2);
        Operator and = new Operator("&&", or1, or2);
        // Initialize root
        Operator r = new Operator("&&", greater, and);

        // Print branch structure
        r.print("");
        // Evaluate branch
        r.evaluate();

        // Print result of the evaluation
        System.out.println("Result of the current branch predicate is : " + r.bool);

        // Print valid branch predicate conditions
        r.printConditions();

        /* Uncomment below to see Correlated MCDC test cases (the last boolean on each list is the result of the predicate) */
        r.printTestRequirements(false);

        /* Uncomment below to see Restricted MCDC test cases (the last boolean on each list is the result of the predicate) */
        //r.printTestRequirements(true);

    }
}
