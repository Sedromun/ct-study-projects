package expression.exceptions;

import expression.AnyExpression;
import expression.Multiply;
import expression.TripleExpression;

public class CheckedMultiply extends Multiply {
    CheckedMultiply(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }

    @Override
    public int calc(int first, int second) {
        if (first > 0 && second > 0) {
            if (first > Integer.MAX_VALUE / second) {
                throw new OverflowException("overflow occurred during evaluate in class Multiply");
            }
        } else if (first > 0 && second < 0) {
            if (second < Integer.MIN_VALUE / first) {
                throw new OverflowException("overflow occurred during evaluate in class Multiply");
            }
        } else if (first < 0 && second > 0) {
            if (first < Integer.MIN_VALUE / second) {
                throw new OverflowException("overflow occurred during evaluate in class Multiply");
            }
        } else if (first < 0 && second < 0) {
            if (first < Integer.MAX_VALUE / second) {
                throw new OverflowException("overflow occurred during evaluate in class Multiply");
            }
        }
        return first * second;
    }
}
