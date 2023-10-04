package expression.exceptions;

import expression.Add;
import expression.AnyExpression;
import expression.TripleExpression;

public class CheckedAdd extends Add {

    CheckedAdd(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }

    @Override
    public int calc(int first, int second) {
        if ((second > 0 && first > Integer.MAX_VALUE - second) || (second <= 0 && first < Integer.MIN_VALUE - second)) {
            throw new OverflowException("overflow occurred during evaluate in class Add");
        }
        return first + second;
    }
}
