package expression.exceptions;


import expression.Const;
import expression.TripleExpression;
import expression.AnyExpression;
import expression.Variable;

import expression.parser.BaseParser;
import expression.parser.CharSource;
import expression.parser.StringSource;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExpressionParser implements TripleParser {

    @Override
    public TripleExpression parse(final String source) throws Exception {
        //System.out.println("'" + source + "'");
        return parse(new StringSource(source));
    }

    public static AnyExpression parse(final CharSource source) throws ExpressionParserException {
        return new ExpressionParse(source).parseFullExpression();
    }

    private static class ExpressionParse extends BaseParser {
        private String token;

        public ExpressionParse(final CharSource source) {
            super(source);
        }

        public AnyExpression parseFullExpression() throws UnexpectedTokenException {
            token = getToken();
            final AnyExpression result = parsePriority(1);
            if (token.equals(String.valueOf(BaseParser.END))) {
                return result;
            }
            throw new EndOfFileExpectedException("End of Expression expected");
        }

        private AnyExpression parsePriority(int priority) throws UnexpectedTokenException {
            if (priority == 4) {
                return parseUnary();
            }
            AnyExpression expression1 = parsePriority(priority + 1);
            while (getPriorityTokens(priority).contains(token)) {
                String operationType = token;
                token = getToken();
                AnyExpression expression2 = parsePriority(priority + 1);

                expression1 = switch (operationType) {
                    case "set" -> new CheckedSet(expression1, expression2);
                    case "clear" -> new CheckedClear(expression1, expression2);
                    case "+" -> new CheckedAdd(expression1, expression2);
                    case "-" -> new CheckedSubtract(expression1, expression2);
                    case "*" -> new CheckedMultiply(expression1, expression2);
                    case "/" -> new CheckedDivide(expression1, expression2);
                    default -> throw new IllegalArgumentException(
                        "Illegal operationType in parsePriority: " + operationType);
                };
            }
            return expression1;
        }

        private List<String> getPriorityTokens(int priority) throws UnexpectedTokenException {
            return switch (priority) {
                case 1 -> List.of("set", "clear");
                case 2 -> List.of("+", "-");
                case 3 -> List.of("/", "*");
                default -> throw new UnexpectedTokenException("Unexpected value in getPriorityTokens: " + priority);
            };
        }

        private AnyExpression parseUnary() throws UnexpectedTokenException {
            AnyExpression expression;
            if (token.equals("-")) {
                token = getTokenDoNotSkipWS();
                if (isNumber()) {
                    return parseConst(true);
                }
                if (token.equals("")) {
                    token = getToken();
                }
                expression = parseUnary();
                expression = new CheckedNegate(expression);
            } else if (token.equals("count")) {
                token = getToken();
                expression = parseUnary();
                expression = new CheckedCount(expression);
            } else if (token.equals("pow10")) {
                token = getToken();
                expression = parseUnary();
                expression = new CheckedPow10(expression);
            } else if (token.equals("log10")) {
                token = getToken();
                expression = parseUnary();
                expression = new CheckedLog10(expression);
            } else {
                expression = parseBrackets();
            }
            return expression;
        }

        private AnyExpression parseBrackets() throws UnexpectedTokenException {
            AnyExpression expression;
            if (token.equals("(")) {
                token = getToken();
                expression = parsePriority(1);

                if (!token.equals(")")) {
                    throw new BracketExpectedException("Unexpected token: '" + token + "', ')' was expected");
                }
                token = getToken();
            } else {
                expression = parseConstVariable();
            }
            return expression;
        }

        private AnyExpression parseConstVariable() throws UnexpectedTokenException {
            if (token.equals("x") || token.equals("y") || token.equals("z")) {
                return parseVariable();
            } else if (isNumber()) {
                return parseConst(false);
            } else {
                throw new UnexpectedTokenException("Unexpected token for Const or Variable: '" + token + "'");
            }
        }

        private boolean isNumber() {
            try {
                Integer.parseInt(token);
            } catch (NumberFormatException e) {
                return token.equals(Integer.toString(Integer.MIN_VALUE).substring(1, 11));
            }
            return true;
        }

        private AnyExpression parseVariable() {
            Variable variable = new Variable(token);
            token = getToken();
            return variable;
        }

        private AnyExpression parseConst(boolean isMinus) {
            Const constant = new Const(Integer.parseInt((isMinus ? "-" : "") + token));
            token = getToken();
            return constant;
        }
    }
}

