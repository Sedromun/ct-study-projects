package expression;



public class Log10 extends AbstractUnaryOperation {
    public Log10(AnyExpression anyExpression) {
        super(anyExpression);
    }

    @Override
    public int calc(int value) {
        return log(value);
    }

    private int log(int value) {
        return String.valueOf(value).length() - 1;
    }

    @Override
    public double calc(double value) {
        throw new IllegalArgumentException("can't make operation 'log10' from double arguments");
    }

    @Override
    public String getOperationSign() {
        return "log10";
    }
}
