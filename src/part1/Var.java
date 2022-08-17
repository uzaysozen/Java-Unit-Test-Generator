package part1;

import java.util.Objects;

/**
 * Var class to store the condition variables
 */
public class Var {

    protected double value; // number value of the variable
    protected String name; // name of the variable

    /**
     * Var constructor
     *
     * @param name Name of the variable
     * @param val Value of the variable
     */
    public Var(String name, Number val) {
        this.value = val.doubleValue();
        this.name = name;
    }

    /**
     * Var constructor
     *
     * @param name Name of the variable
     */
    public Var(String name) {
        this.value = 0;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return Double.compare(var.value, value) == 0 && name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, name);
    }
}
