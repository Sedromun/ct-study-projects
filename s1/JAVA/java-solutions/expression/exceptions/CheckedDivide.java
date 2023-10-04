package expression.exceptions;

import expression.AnyExpression;
import expression.Divide;
import expression.TripleExpression;

public class CheckedDivide extends Divide {
    CheckedDivide(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }

    @Override
    public int calc(int first, int second) {
        if (second == 0) {
            throw new DivisionByZeroException("division by zero occurred during evaluate");
        } else if (second == -1 && first == Integer.MIN_VALUE) {
            throw new OverflowException("overflow occurred during evaluate in class Divide");
        }
        return first / second;
    }
}
