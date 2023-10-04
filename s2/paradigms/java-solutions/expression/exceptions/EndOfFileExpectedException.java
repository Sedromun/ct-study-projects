package expression.exceptions;

public class EndOfFileExpectedException extends UnexpectedTokenException {
    public EndOfFileExpectedException(String message) {
        super(message);
    }
}
