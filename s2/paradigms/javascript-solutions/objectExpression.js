"use strict";

function Expression(calc, operator, ...operands) {
    this.evaluate = function (x, y, z) {
        return calc(...operands.map(operand => operand.evaluate(x, y, z)));
    }
    this.toString = function () {
        return operands.map(operand => operand + " ").join('') + operator;
    }
    this.prefix = function () {
        return "(" + operator + " " + operands.map(operand => operand.prefix()).join(' ') + ")";
    }
    this.postfix = function () {
        return "(" + operands.map(operand => operand.postfix()).join(' ') + " " + operator + ")";
    }
    this.diff = function (diffur) {
        return new Expression(calc, operator, ...operands.map(operand => operand.diff(diffur)));
    }
}

function Const(value) {
    this.evaluate = function () {
        return value;
    }
    this.toString = function () {
        return value.toString();
    }
    this.prefix = function () {
        return value.toString();
    }
    this.postfix = function () {
        return value.toString();
    }
    this.diff = function () {
        return new Const(0);
    }
}

const VARIABLES = ["x", "y", "z"];

function Variable(variable) {
    this.evaluate = function (...args) {
        return args[VARIABLES.indexOf(variable)]
    }
    this.toString = function () {
        return variable;
    }
    this.prefix = function () {
        return variable;
    }
    this.postfix = function () {
        return variable;
    }
    this.diff = function (diffur) {
        return (diffur === variable) ? new Const(1) : new Const(0);
    }
}

function Add(expression1, expression2) {
    return new Expression((a, b) => (a + b), "+", expression1, expression2);
}

function Subtract(expression1, expression2) {
    return new Expression((a, b) => (a - b), "-", expression1, expression2);
}

function Multiply(expression1, expression2) {
    let expression = new Expression((a, b) => (a * b), "*", expression1, expression2);
    expression.diff = function (diffur) {
        return new Add(new Multiply(expression1, expression2.diff(diffur)), new Multiply(expression1.diff(diffur), expression2));
    }
    return expression;
}

function Divide(expression1, expression2) {
    let expression = new Expression((a, b) => (a / b), "/", expression1, expression2);
    expression.diff = function (diffur) {
        return new Divide(new Divide(new Subtract(new Multiply(expression1.diff(diffur), expression2), new Multiply(expression1, expression2.diff(diffur))), expression2), expression2);
    }
    return expression;
}

function Negate(expression) {
    return new Expression(a => -a, "negate", expression);
}

function squareArgs(...args) {
    return args.map(argument => argument * argument).reduce((partialSum, a) => partialSum + a, 0);
}

function Sumsq(operation, ...expressions) {
    let expression = new Expression(squareArgs, operation, ...expressions);
    expression.diff = function (diffur) {
        return expressions.map(expression =>
            new Multiply(new Const(2), new Multiply(expression, expression.diff(diffur))))
            .reduce((diffExpression, partial) => new Add(diffExpression, partial));
    }
    return expression;
}

function Sumsq2(...expressions) {
    return Sumsq("sumsq2", ...expressions);
}

function Sumsq3(...expressions) {
    return Sumsq("sumsq3", ...expressions);
}

function Sumsq4(...expressions) {
    return Sumsq("sumsq4", ...expressions);
}

function Sumsq5(...expressions) {
    return Sumsq("sumsq5", ...expressions);
}

function Distance(operation, ...expressions) {
    let expression = new Expression((...args) => Math.sqrt(squareArgs(...args)), operation, ...expressions);
    expression.diff = function (diffur) {
        let args = new Sumsq("sumsqn", ...expressions);
        return new Divide(args.diff(diffur),new Multiply(new Const(2), new Distance(operation, ...expressions)));
    }
    return expression;
}

function Distance2(...expressions) {
    return new Distance("distance2", ...expressions);
}

function Distance3(...expressions) {
    return new Distance("distance3", ...expressions);
}

function Distance4(...expressions) {
    return new Distance("distance4", ...expressions);
}

function Distance5(...expressions) {
    return new Distance("distance5", ...expressions);
}

function sumexp(...args) {
    return args.map(argument => Math.exp(argument)).reduce((partialSum, a) => partialSum + a, 0);
}

function Sumexp(...expressions) {
    let expr = new Expression(sumexp, "sumexp", ...expressions);
    expr.diff = function (diffur) {
        let partialDiffed = expressions.map(expression =>
            new Multiply(expression.diff(diffur), new Sumexp(expression)));
        if (partialDiffed.length === 1) {
            return partialDiffed.pop();
        } else {
            return partialDiffed.reduce((diffExpression, partial) => new Add(diffExpression, partial));
        }
    }
    return expr;
}

function LSE(...expressions) {
    let expr = new Expression((...args) => Math.log(sumexp(...args)), "lse", ...expressions);
    expr.diff = function (diffur) {
        let args = Sumexp(...expressions);
        return new Divide(args.diff(diffur), args);
    }
    return expr;
}

const operations = {
    "sumsq2": (x1, x2) => new Sumsq2(x1, x2),
    "sumsq3": (x1, x2, x3) => new Sumsq3(x1, x2, x3),
    "sumsq4": (x1, x2, x3, x4) => new Sumsq4(x1, x2, x3, x4),
    "sumsq5": (x1, x2, x3, x4, x5) => new Sumsq5(x1, x2, x3, x4, x5),
    "distance2": (x1, x2) => new Distance2(x1, x2),
    "distance3": (x1, x2, x3) => new Distance3(x1, x2, x3),
    "distance4": (x1, x2, x3, x4) => new Distance4(x1, x2, x3, x4),
    "distance5": (x1, x2, x3, x4, x5) => new Distance5(x1, x2, x3, x4, x5),
    "+": (x, y) => new Add(x, y),
    "-": (x, y) => new Subtract(x, y),
    "*": (x, y) => new Multiply(x, y),
    "/": (x, y) => new Divide(x, y),
    "negate": x => new Negate(x),
};

const parse = expression => {
    return expression.trim().split(/\s+/).reduce((stack, token) => {
        if (token in operations) {
            const args = stack.splice(-operations[token].length);
            stack.push(operations[token](...args));
        } else if (VARIABLES.includes(token)) {
            stack.push(new Variable(token));
        } else {
            stack.push(new Const(parseFloat(token)));
        }
        return stack;
    }, []).pop();
}

const postfixOperations = {
    "+": (x, y) => new Add(x, y),
    "-": (x, y) => new Subtract(x, y),
    "*": (x, y) => new Multiply(x, y),
    "/": (x, y) => new Divide(x, y),
    "negate": (x) => new Negate(x),
    "sumexp": (...args) => new Sumexp(...args),
    "lse": (...args) => new LSE(...args)
};

function isWhitespace(c) {
    return /\s/.test(c)
}

function skipWhitespace(expression, i) {
    while (i < expression.length && isWhitespace(expression[i])) {
        i++;
    }
    return i;
}

function getToken(expression, i) {
    i = skipWhitespace(expression, i);
    let ans;
    if (expression.charAt(i) === '(') {
        ans = '(';
        i++;
    } else if (expression.charAt(i) === ')') {
        ans = ')';
        i++;
    } else {
        let token = "";
        while (i < expression.length && !/\s/.test(expression[i]) && expression[i] !== '(' && expression[i] !== ')') {
            token += expression[i];
            i++;
        }
        ans = token;
    }
    i = skipWhitespace(expression, i);
    return [ans, i];
}

function parseBrackets(expression, i, type) {
    let getTokenRet = getToken(expression, i);
    let token = getTokenRet[0];
    i = getTokenRet[1];
    if (type === "prefix" && !(token in postfixOperations)) {
        throw new UnexpectedToken("operator expected, '" + token + "' - got");
    }
    let operands = [];
    let operator = "";
    while (i < expression.length && token !== ')') {
        if (token === '(') {
            let parsedBracket = parseBrackets(expression, i, type);
            operands.push(parsedBracket[0]);
            i = parsedBracket[1];
        } else if (token in postfixOperations) {
            if (operator !== "") {
                throw new IllegalOperatorException("only one operator is available");
            }
            operator = token;
            if (type === "postfix") {
                getTokenRet = getToken(expression, i);
                token = getTokenRet[0];
                i = getTokenRet[1];
                break;
            }
        } else if (VARIABLES.includes(token)) {
            operands.push(new Variable(token));
        } else if (!isNaN(token)) {
            operands.push(new Const(parseFloat(token)));
        } else {
            throw new UnexpectedToken("'" + token + "' on pos " + i + ", variable or const expected");
        }
        getTokenRet = getToken(expression, i);
        token = getTokenRet[0];
        i = getTokenRet[1];
    }
    if (token === ')') {
        if (operator === '') {
            throw new IllegalOperatorException("no operator in expression")
        }
        if (operator !== "sumexp" && operator !== "lse" && operands.length !== postfixOperations[operator].length) {
            throw new IllegalOperatorException("incorrect number of operation '" + operator + "', expected " + postfixOperations[operator].length + ", got " + operands.length);
        }
        return [postfixOperations[operator](...operands), i];
    }
    if (i === expression.length) {
        throw new BracketError(token, i);
    }
}

function parseExpression(expression, type) {
    let tokenRet = getToken(expression, 0);
    let token = tokenRet[0];
    if (token === '(') {
        let expr = parseBrackets(expression, tokenRet[1], type);
        if (expr[1] !== expression.length) {
            throw new BracketError(token, tokenRet[1]);
        }
        return expr[0];
    } else {
        if (tokenRet[1] !== expression.length) {
            throw new BracketError(token, tokenRet[1]);
        }
        if (VARIABLES.includes(token)) {
            return new Variable(token);
        } else if (!isNaN(token) && token !== '') {
            return new Const(parseFloat(token));
        } else {
            throw new UnexpectedToken("'" + token + "' on pos " + tokenRet[1] + ", variable or const expected");
        }
    }
}

const parsePrefix = expression => {
    return parseExpression(expression, "prefix");
}

const parsePostfix = expression => {
    return parseExpression(expression, "postfix");
}

function ParserError(message) {
    Error.call(this, "Error in parsing expression: " + message);
    this.message = "Error in parsing expression: " + message;
}

function IllegalOperatorException(message) {
    ParserError.call(this, "Illegal operator exception: " + message);
    this.message = "Illegal operator exception: " + message;
}

function UnexpectedToken(message) {
    ParserError.call(this, "Unexpected token: " + message);
    this.message = "Unexpected token: " + message;
}

function BracketError(token, finishPos) {
    UnexpectedToken.call(this, "'" + token + "' that finishes on pos " + finishPos + ", bracket expected");
    this.message = "'" + token + "' that finishes on pos " + finishPos + ", bracket expected";
}
