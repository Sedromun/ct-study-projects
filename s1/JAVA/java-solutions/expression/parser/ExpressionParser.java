package expression.parser;

import expression.*;

import java.util.List;

public final class ExpressionParser implements TripleParser {
    @Override
    public TripleExpression parse(final String source) {
        //System.out.println("'" + source + "'");
        return parse(new StringSource(source));
    }
    public static AnyExpression parse(final CharSource source) {
        return new ExpressionParse(source).parseFullExpression();
    }
    private static class ExpressionParse extends BaseParser {
        private String token;
        public ExpressionParse(final CharSource source) {
            super(source);
        }

        public AnyExpression parseFullExpression() {
            token = getToken();
            final AnyExpression result = parsePriority(1);
            if (eof()) {
                return result;
            }
            throw error("End of Expression expected");
        }

        private AnyExpression parsePriority(int priority) {
            if (priority == 4) {
                return parseUnary();
            }
            AnyExpression expression1 = parsePriority(priority + 1);
            while (getPriorityTokens(priority).contains(token)) {
                String operationType = token;
                token = getToken();
                AnyExpression expression2 = parsePriority(priority + 1);

                expression1 = switch (operationType) {
                    case "set" -> new Set(expression1, expression2);
                    case "clear" -> new Clear(expression1, expression2);
                    case "+" -> new Add(expression1, expression2);
                    case "-" -> new Subtract(expression1, expression2);
                    case "*" -> new Multiply(expression1, expression2);
                    case "/" -> new Divide(expression1, expression2);
                    default -> throw new IllegalArgumentException("Illegal operationType in parsePriority: " + operationType);
                };
            }
            return expression1;
        }

        private List<String> getPriorityTokens(int priority) {
            return switch (priority) {
                case 1 -> List.of("set", "clear");
                case 2 -> List.of("+", "-");
                case 3 -> List.of("/", "*");
                default -> throw new IllegalStateException("Unexpected value in getPriorityTokens: " + priority);
            };
        }

        private AnyExpression parseUnary() {
            AnyExpression expression;
            if(token.equals("-")) {
                token = getTokenDoNotSkipWS();
                if (isNumber()) {
                    return parseConst(true);
                }
                if (token.equals("")) {
                    token = getToken();
                }
                expression = parseUnary();
                expression = new Negate(expression);
            } else if (token.equals("count")) {
                token = getToken();
                expression = parseUnary();
                expression = new Count(expression);
            } else {
                expression = parseBrackets();
            }
            return expression;
        }

        private AnyExpression parseBrackets() {
            AnyExpression expression;
            if (token.equals("(")) {
                token = getToken();
                expression = parsePriority(1);

                if (!token.equals(")")) {
                    throw error("Unexpected token: '" + token + "', ')' was expected");
                }
                token = getToken();
            } else {
                expression = parseConstVariable();
            }
            return expression;
        }

        private AnyExpression parseConstVariable() {
            if (token.equals("x") || token.equals("y") || token.equals("z")) {
                return parseVariable();
            } else if (isNumber()) {
                return parseConst(false);
            } else {
                throw error("Unexpected token for Const or Variable: '" + token + "'");
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
