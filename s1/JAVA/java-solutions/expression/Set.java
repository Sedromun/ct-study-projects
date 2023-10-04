package expression;

public class Set extends AbstractBinaryExpression {
    public Set(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }

    @Override
    public int calc(int first, int second) {
        return first | (1 << second);
    }

    @Override
    public double calc(double first, double second) {
        throw new IllegalArgumentException("can't make operation 'set' from double arguments");
    }

    @Override
    public int getFirstPriority() {
        return 0;
    }

    @Override
    public int getSecondPriority() {
        return 5;
    }

    @Override
    public String getOperationSign() {
        return "set";
    }
}
