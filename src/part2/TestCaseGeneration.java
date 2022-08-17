package part2;

import part1.Condition;
import part1.Operator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * The TestCaseGeneration class to automatically generate
 * test cases for the branch predicate
 */
public class TestCaseGeneration {
    /**
     * Parameters for Automatic Test Case Generation
     */
    public static double max = 10; // max value for an input
    public static double min = -10; // min value for an input
    public static int iterations = 1000; // number of iterations for random input search
    public static int numberOfInputs = 3; // number of inputs required for the branch predicate
    public static boolean restricted = true; // if true find test requirements for Restricted MCDC else Correlated MCDC
    public static boolean inputTypeInt = true; // if true generate Integer inputs else generate doubles
    public static Operator branchPredicate; // root of the current branch predicate
    public static String methodNameToTest = null; // method name which will be tested
    public static String packageName = null; // package name which the test suite will be executed
    public static String fileName = null; // name of the test file to be generated

    private static String currentAssertionValue; // current expected output for the current input list
    private static ArrayList<Number> inputList; // list of current inputs to be tested

    /**
     * Branch interface to initialize a branch predicate
     */
    public interface Branch {
        void initBranch(ArrayList<Number> inputs);
    }

    /**
     * Find inputs and generate a test suite from the test requirements
     *
     * @param  branch   the branch predicate to be tested
     */
    public static void generateTests(Branch branch) {
        createTestFile(); // creating test .java file with the filename
        int testCaseCount = 0; // counting the number of test cases created
        inputList = generateRandomInputs(numberOfInputs); // getting random inputs
        branch.initBranch(inputList); // simulating given branch with random inputs
        HashSet<boolean[]> testRequirements;
        if (restricted) { // getting test requirements
            testRequirements = branchPredicate.getRestrictedMCDC();
        } else {
            testRequirements = branchPredicate.getCorrelatedMCDC();
        }
        // comparing simulated branch and condition results with test requirements
        for (boolean[] req : testRequirements) {
            for (int x = 0; x < iterations - 1; x++) {
                inputList = generateRandomInputs(numberOfInputs);
                branch.initBranch(inputList); // simulating given branch with new inputs every time
                ArrayList<Condition> conditions = branchPredicate.getConditions(new ArrayList<>());
                boolean valid = true;
                for (int i = 0; i < conditions.size(); i++) {
                    Condition con = conditions.get(i);
                    con.evaluate();
                    if (con.bool != req[i]) {
                        valid = false;
                        break;
                    }
                }
                if (valid) { // valid inputs found
                    testCaseCount++; // adding to the test case count
                    getAssertionValue(); // getting the expected output for the inputs from the user
                    writeToTestFile(printTestCase(testCaseCount)); // writing the test case to the .java file
                    System.out.println("Test case generated successfully.");
                    break;
                }
            }
        }
        writeToTestFile("\n}");
        // warning the user if the number of test cases generated do not match the number of test requirements
        if (testCaseCount != testRequirements.size()) {
            System.out.println("Could not find inputs for some of the test requirements. Increase the number of " +
                    "iterations or check your branch predicate for infeasibilities.");
            System.out.println("Clearing contents of the test file...");
            clearTestFile();
        } else {
            System.out.println("The inputs for all the requirements were found" +
                    " and test suite generated successfully!");
        }
    }

    /**
     * Get the expected output from the user for the generated inputs
     */
    private static void getAssertionValue() {
        Scanner scanner = new Scanner(System.in);  // Creating a Scanner object
        StringBuilder s = new StringBuilder("Enter the expected output for the inputs ("); // Prompt the user
        addInputsAsString(s);
        s.append(": ");
        System.out.print(s);
        currentAssertionValue = scanner.nextLine();  // Reading user input
    }

    /**
     * Create a test case String to write to the file
     *
     * @param  count   number of test cases generated
     * @return         test case String to write to test file
     */
    private static String printTestCase(int count) {
        StringBuilder result = new StringBuilder("    @Test\n    public void testCase" + count +
                "() { assertEquals(" + currentAssertionValue + ", " + methodNameToTest + "(");
        addInputsAsString(result); // adding inputs to the test case String
        result.append("); }\n\n");
        return result.toString();
    }

    /**
     * Add inputs to the test case string.
     *
     * @param  s   string to add inputs
     */
    private static void addInputsAsString(StringBuilder s) {
        for (int i = 0; i < inputList.size(); i++) {
            if (inputTypeInt) { // add integer values if method to test accepts integer inputs
                if (i != inputList.size() - 1)
                    s.append(inputList.get(i).intValue()).append(", ");
                else
                    s.append(inputList.get(i).intValue()).append(")");
            } else {
                if (i != inputList.size() - 1)
                    s.append(inputList.get(i)).append(", ");
                else
                    s.append(inputList.get(i)).append(")");
            }
        }
    }

    /**
     * Generate random numbers
     *
     * @param  r   a random Object
     * @return     a random Number
     */
    private static Number randomNumber(Random r) {
        if (min == Double.MIN_VALUE && max == Double.MAX_VALUE) {
            if (inputTypeInt) {
                return r.nextInt();
            } else {
                return r.nextDouble();
            }

        } else {
            if (inputTypeInt) {
                return r.nextInt((int) (max - min + 1)) + min;
            } else {
                return min + (r.nextDouble() * (max - min));
            }
        }
    }

    /**
     * Generate random inputs and create an input list
     *
     * @param  n   number of inputs
     * @return     array of random number inputs
     */
    private static ArrayList<Number> generateRandomInputs(int n) {
        Random r = new Random();
        ArrayList<Number> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(randomNumber(r));
        }
        return result;
    }

    /**
     * Create a test file with package name, file name, class name and needed imports
     */
    public static void createTestFile() {
        try {
            File myObj = new File(fileName + ".java"); // creating file
            myObj.createNewFile();
            if (packageName != null) { // add package if the file will be used in a package
                writeToTestFile("package " + packageName + ";\n");
            }
            writeToTestFile("import org.junit.jupiter.api.Test;\n"); // writing needed imports
            writeToTestFile("import static org.junit.jupiter.api.Assertions.*;\n\n");
            writeToTestFile("public class " + fileName + " {\n\n"); // writing class name
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Write a String to the current test file
     *
     * @param  s   String to write to test file
     */
    public static void writeToTestFile(String s) {
        try {
            FileWriter myWriter = new FileWriter(fileName + ".java", true);
            myWriter.write(s);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Clear current test file
     */
    public static void clearTestFile() {
        try {
            FileWriter myWriter = new FileWriter(fileName + ".java");
            myWriter.write("");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
