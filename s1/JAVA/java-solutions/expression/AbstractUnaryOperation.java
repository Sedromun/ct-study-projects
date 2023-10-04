package expression;

import java.util.Objects;

public abstract class AbstractUnaryOperation implements UnaryExpression {
    private final AnyExpression expression;

    public AbstractUnaryOperation(AnyExpression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return this.getOperationSign() + "(" + expression + ")";
    }

    @Override
    public String toMiniString() {
        if (expression.getFirstPriority() < this.getFirstPriority()) {
            return this.getOperationSign() + "(" + expression.toMiniString() + ")";
        } else {
            return this.getOperationSign() + " " + expression.toMiniString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == getClass()) {
            final AbstractUnaryOperation unaryOperationExpr = (AbstractUnaryOperation) obj;
            return unaryOperationExpr.equals(expression);
        }
        return false;
    }

    @Override
    public int hashCode() { // :NOTE: .hashCode
        return Objects.hash(expression);
    }

    @Override
    public int evaluate(int value) {
        return calc(expression.evaluate(value));
    }

    @Override
    public double evaluate(double value) {
        return calc(expression.evaluate(value));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return calc(expression.evaluate(x, y, z));
    }

    @Override
    public int getFirstPriority() {
        return 50;
    }

    @Override
    public int getSecondPriority() {
        return 50;
    }
}
