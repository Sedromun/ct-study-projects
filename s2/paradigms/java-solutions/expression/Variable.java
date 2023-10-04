package expression;

import java.util.Objects;

public class Variable extends AbstractOperand {
    private final String variable;
    public Variable(final String variable) {
        this.variable = variable;
    }

    @Override
    public int evaluate(final int value) {
        return value;
    }

    @Override
    public double evaluate(final double value) {
        return value;
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        if ("x".equals(variable)) {
            return x;
        } else if ("y".equals(variable)) {
            return y;
        } else {
            return z;
        }
    }

    @Override
    public String toMiniString() {
        return variable;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Variable variableObject = (Variable) object;
        return Objects.equals(variable, variableObject.variable);
    }

    @Override
    public String toString() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable);
    }
}
