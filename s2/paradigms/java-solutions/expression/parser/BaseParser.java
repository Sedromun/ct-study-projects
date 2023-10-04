package expression.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseParser {
    protected static final char END = '\0';
    private final CharSource source;
    private char ch = 0xffff;

    protected BaseParser(final CharSource source) {
        this.source = source;
        take();
    }

    protected String getToken() {
        skipWhitespace();
        return getTokenDoNotSkipWS();
    }
    protected String getTokenDoNotSkipWS() {
        StringBuilder sb = new StringBuilder();

        if(ch == END) {
            return String.valueOf(END);
        }
        if (isOneCharOperation()) {
            return String.valueOf(take());
        }
        if (Character.isDigit(ch)) {
            if (take('0')) {
                return "0";
            }
            while (Character.isDigit(ch)) {
                sb.append(take());
            }
            return sb.toString();
        }
        while(Character.isDigit(ch) || Character.isLetter(ch)) {
            sb.append(ch);
            take();
        }
        return sb.toString();
    }

    private boolean isOneCharOperation() {
        return ch == '*' || ch == '/' || ch == '+' || ch == '-' || ch == '(' || ch == ')';
    }

    protected char take() {
        final char result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected boolean test(final char expected) {
        return ch == expected;
    }

    protected boolean test(final char expected1, final char expected2) {
        return ch == expected1 && source.getNext() == expected2;
    }

    protected boolean take(final char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected boolean isWhitespace() {
        if (Character.isWhitespace(ch)) {
            take();
            return true;
        }
        return false;
    }

    protected boolean eof() {
        return ch == END;
    }

    protected IllegalArgumentException error(final String message) {
        return source.error(message);
    }

    protected void skipWhitespace() {
        while (isWhitespace()) {
            // skip
        }
    }

}
