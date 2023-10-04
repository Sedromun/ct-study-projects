package expression.exceptions;

import expression.AnyExpression;
import expression.Clear;

public class CheckedClear extends Clear {
    CheckedClear(AnyExpression expression1, AnyExpression expression2) {
        super(expression1, expression2);
    }

    @Override
    public int calc(int first, int second) {
        return super.calc(first, second);
    }
}
