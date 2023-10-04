package expression;

public interface AnyExpression extends Expression, DoubleExpression, TripleExpression {
    String getOperationSign();
    int getFirstPriority();
    int getSecondPriority();
}
