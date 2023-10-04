package expression.exceptions;

import expression.AnyExpression;
import expression.Negate;

public class CheckedNegate extends Negate {
    CheckedNegate(AnyExpression expression) {
        super(expression);
    }

    @Override
    public int calc(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException("overflow occurred during evaluate in class Negate");
        }
        return -value;
    }
}
