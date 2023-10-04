package expression.exceptions;

import expression.AnyExpression;
import expression.Log10;

public class CheckedLog10 extends Log10 {
    CheckedLog10(AnyExpression expression) {
        super(expression);
    }

    @Override
    public int calc(int first) {
        if (first <= 0) {
            throw new OverflowException("overflow occurred during evaluate in class Log10");
        }
        return super.calc(first);
    }
}
