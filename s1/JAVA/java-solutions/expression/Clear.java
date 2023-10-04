package expression;

public class Clear extends AbstractBinaryExpression {
    public Clear(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }

    @Override
    public int calc(int first, int second) {
        return first & ~(1 << second);
    }

    @Override
    public double calc(double first, double second) {
        throw new IllegalArgumentException("can't make operation 'clear' from double arguments");
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
        return "clear";
    }
}
