package expression.exceptions;

public class UnexpectedTokenException extends ExpressionParserException {
    public UnexpectedTokenException(String message) {
        super(message);
    }
}
