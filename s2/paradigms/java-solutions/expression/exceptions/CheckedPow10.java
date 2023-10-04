package expression.exceptions;

import expression.AnyExpression;
import expression.Pow10;

public class CheckedPow10 extends Pow10 {
    CheckedPow10(AnyExpression expression) {
        super(expression);
    }

    @Override
    public int calc(int first) {
        if (first < 0 || first >= 10) {
            throw new OverflowException("overflow occurred during evaluate in class Pow10");
        }
        return super.calc(first);
    }
}
