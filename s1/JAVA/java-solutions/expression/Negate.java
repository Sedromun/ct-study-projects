package expression;



public class Negate extends AbstractUnaryOperation {
    public Negate(AnyExpression anyExpression) {
        super(anyExpression);
    }

    @Override
    public int calc(int value) {
        return -value;
    }

    @Override
    public double calc(double value) {
        return -value;
    }

    @Override
    public String getOperationSign() {
        return "-";
    }
}
