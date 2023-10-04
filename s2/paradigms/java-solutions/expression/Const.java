package expression;

import java.util.Objects;

public class Const extends AbstractOperand {
    private final Number value;
    public Const(int value) {
        this.value = value;
    }

    public Const(double value) {
        this.value = value;
    }

    @Override
    public int evaluate(int value) {
        return this.value.intValue();
    }

    @Override
    public double evaluate(double value) {
        return this.value.doubleValue();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return this.value.intValue();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toMiniString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Const constObject = (Const) object;
        return Objects.equals(value, constObject.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
