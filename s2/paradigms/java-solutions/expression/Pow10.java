package expression;



public class Pow10 extends AbstractUnaryOperation {
    public Pow10(AnyExpression anyExpression) {
        super(anyExpression);
    }

    @Override
    public int calc(int value) {
        return pow(value);
    }

    private int pow(int value) {
        int ans = 1;
        for(int i = 0; i < value; i++) {
            ans *= 10;
        }
        return ans;
    }

    @Override
    public double calc(double value) {
        throw new IllegalArgumentException("can't make operation 'pow10' from double arguments");
    }

    @Override
    public String getOperationSign() {
        return "pow10";
    }
}
