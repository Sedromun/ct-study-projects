package expression;

public class Count extends AbstractUnaryOperation {
    public Count(AnyExpression anyExpression) {
        super(anyExpression);
    }

    @Override
    public int calc(int value) {
        return Integer.bitCount(value);
    }

    @Override
    public double calc(double value) {
        throw new IllegalArgumentException("can't make operation 'count' from double arguments");
    }

    @Override
    public int getFirstPriority() {
        return 50;
    }

    @Override
    public int getSecondPriority() {
        return 50;
    }

    @Override
    public String getOperationSign() {
        return "count";
    }
}
