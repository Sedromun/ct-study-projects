package expression;

public interface UnaryExpression extends AnyExpression {
    int calc(int value);
    double calc(double value);
}
