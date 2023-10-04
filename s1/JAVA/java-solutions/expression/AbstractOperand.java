package expression;

public abstract class AbstractOperand implements AnyExpression {
    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public int getFirstPriority() {
        return 100;
    }

    @Override
    public int getSecondPriority() {
        return 100;
    }
}
