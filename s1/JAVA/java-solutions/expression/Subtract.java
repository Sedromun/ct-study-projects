package expression;


public class Subtract extends AbstractBinaryExpression {

    public Subtract(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }

    @Override
    public int calc(int first, int second) {
        return first - second;
    }

    @Override
    public double calc(double first, double second) {
        return first - second;
    }

    @Override
    public int getFirstPriority() {
        return 10;
    }

    @Override
    public int getSecondPriority() {
        return 15;
    }

    @Override
    public String getOperationSign() {
        return "-";
    }
}
