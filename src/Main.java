import part1.Condition;
import part1.Operator;
import part1.Var;
import part2.TestCaseGeneration;

/**
 * The Main class to initialize branch predicates, generating
 * a test suite and test file for the branch predicates
 */
public class Main {

    public static void main(String[] args) {
        /* ********************************** EXAMPLE 1 ****************************** */
        // Initializing the branch predicate
        TestCaseGeneration.Branch basicExample = inputs -> {
            // Initialize inputs
            double a = inputs.get(0).doubleValue();
            double b = inputs.get(1).doubleValue();
            // Initialize condition variables
            Var var1 = new Var("a", a);
            Var var2 = new Var("b", b);
            // Initialize conditions (external nodes)
            Condition equals = new Condition("==", var1, var2);
            Condition greaterEqual = new Condition(">=", var2, var1);
            // Initialize root
            TestCaseGeneration.branchPredicate = new Operator("||", equals, greaterEqual);

        };

        /* ********************************** EXAMPLE 2 ****************************** */
        // Initializing the branch predicate
        TestCaseGeneration.Branch triangleExample = inputs -> {
            // Initialize inputs
            int side1 = inputs.get(0).intValue();
            int side2 = inputs.get(1).intValue();
            int side3 = inputs.get(2).intValue();
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
            TestCaseGeneration.branchPredicate = new Operator("&&", greater, and);
        };

        /* ********************************** EXAMPLE 3 ****************************** */
        // Initializing the branch predicate
        TestCaseGeneration.Branch moreComplexExample = inputs -> {
            // Initialize inputs
            int a = inputs.get(0).intValue();
            int b = inputs.get(1).intValue();
            int c = inputs.get(2).intValue();
            int d = inputs.get(3).intValue();
            int e = inputs.get(4).intValue();
            // Initialize condition variables
            Var var1 = new Var("a", a);
            Var var2 = new Var("b", b);
            Var var3 = new Var("c", c);
            Var var4 = new Var("d", d);
            Var var5 = new Var("e", e);
            Var var6 = new Var("c + e + d", c + e + d);
            Var var7 = new Var("a - b", a - b);
            // Initialize conditions (external nodes)
            Condition greater = new Condition(">", var3, var4);
            Condition eq1 = new Condition("==", var3, var4);
            Condition eq2 = new Condition("==", var2, var1);
            Condition noteq1 = new Condition("!=", var7, var2);
            Condition noteq2 = new Condition("!=", var1, var2);
            Condition lessEq = new Condition("<=", var1, var6);
            Condition less = new Condition("<", var5, var4);
            Condition greaterEq = new Condition(">=", var1, var2);
            // Initialize operators (internal nodes)
            Operator or3 = new Operator("||", greater, less);
            Operator and4 = new Operator("&&", eq1, lessEq);
            Operator or2 = new Operator("||", eq2, noteq1);
            Operator not2 = new Operator("!", null, or2);
            Operator and3 = new Operator("&&", not2, noteq2);
            Operator or1 = new Operator("||", and4, or3);
            Operator and2 = new Operator("&&", and3, greaterEq);
            Operator and1 = new Operator("&&", or1, and2);
            // Initialize root
            TestCaseGeneration.branchPredicate = new Operator("!", null, and1);
        };
        /* ****************************************************************************** */

        /* Parameters for test case generation */
        TestCaseGeneration.max = 20; // max value for an input
        TestCaseGeneration.min = 10; // min value for an input
        TestCaseGeneration.iterations = 100000; // number of iterations for random input search
        TestCaseGeneration.numberOfInputs = 3; // number of inputs required for the branch predicate
        TestCaseGeneration.restricted = false; // if true find test requirements for Restricted MCDC else Correlated MCDC
        TestCaseGeneration.packageName = "unittest"; // package name which the test suite will be executed(comment out if test suite will not be in a package)
        TestCaseGeneration.methodNameToTest = "checkTriangle"; // method name which will be tested
        TestCaseGeneration.fileName = "TriangleTest"; // name of the test file to be generated
        TestCaseGeneration.inputTypeInt = true; // if true generate Integer inputs else generate doubles

        /* Generate tests for the branch predicate */
        //TestCaseGeneration.generateTests(basicExample);
        TestCaseGeneration.generateTests(triangleExample);
        //TestCaseGeneration.generateTests(moreComplexExample);

        /* Uncomment below to see the branch predicate structure */
        //TestCaseGeneration.branchPredicate.print("");

        /* Uncomment below to see Correlated MCDC test cases (the last boolean on each list is the result of the predicate) */
        TestCaseGeneration.branchPredicate.printTestRequirements(false);

        /* Uncomment below to see Restricted MCDC test cases (the last boolean on each list is the result of the predicate) */
        //TestCaseGeneration.branchPredicate.printTestRequirements(true);
    }
}
