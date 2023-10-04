package expression.exceptions;

import expression.AnyExpression;
import expression.Subtract;
import expression.TripleExpression;

public class CheckedSubtract extends Subtract {
    CheckedSubtract(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }


    @Override
    public int calc(int first, int second) {
        if ((second > 0 && first < Integer.MIN_VALUE + second) || (second <= 0 && first > Integer.MAX_VALUE + second)) {
            throw new OverflowException("overflow occurred during evaluate in class Subtract");
        }
        return first - second;
    }
}
