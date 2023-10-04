package expression.exceptions;

import expression.AnyExpression;
import expression.Count;

public class CheckedCount extends Count {
    CheckedCount(AnyExpression expression) {
        super(expression);
    }

    @Override
    public int calc(int first) {
        return super.calc(first);
    }
}
