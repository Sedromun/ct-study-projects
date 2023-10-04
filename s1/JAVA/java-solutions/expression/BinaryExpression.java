package expression;

public interface BinaryExpression extends AnyExpression {
    double calc(double first, double second);
    int calc(int first, int second);
}
