package expression;

import java.util.Objects;

public abstract class AbstractBinaryExpression implements BinaryExpression {
    protected final AnyExpression expression1;
    protected final AnyExpression expression2;

    protected AbstractBinaryExpression(AnyExpression expression1, AnyExpression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public String toString() {
        return "(" + expression1.toString() + " " + this.getOperationSign() + " " + expression2.toString() + ")";
    }

    public static String miniStringPart(AnyExpression expression, boolean needBrackets) {
        if (needBrackets) {
            return "(" + expression.toMiniString() + ")";
        } else {
            return expression.toMiniString();
        }
    }

    @Override
    public String toMiniString() {
        return miniStringPart(expression1,
                this.getFirstPriority() > expression1.getFirstPriority() + 1) +
                " " + this.getOperationSign() + " " +
                miniStringPart(expression2,
                        this.getSecondPriority() > expression2.getFirstPriority());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == getClass()) {
            final AbstractBinaryExpression absExp = (AbstractBinaryExpression) obj;
            return absExp.expression1.equals(expression1)
                    && absExp.expression2.equals(expression2);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression1, expression2, this.getOperationSign());
    }

    @Override
    public int evaluate(int value) {
        return calc(expression1.evaluate(value), expression2.evaluate(value));
    }

    @Override
    public double evaluate(double value) {
        return calc(expression1.evaluate(value), expression2.evaluate(value));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return calc(expression1.evaluate(x, y, z), expression2.evaluate(x, y, z));
    }
}
