"use strict";
const cnst = value => (...args) => value;
const variable = vrbl => (...args) => args[vrbl.charCodeAt(0) - 'x'.charCodeAt(0)];
// :NOTE: всем операциям приходится что-то знать про переменные в выражении (...args)
// все что им нужно - это знать что есть какие-то дочерние выражения и что с им с ними надо делать
// то есть все операции должны иметь вид add = operation((a, b) -> (a + b))
const madd = (a, b, c) => (...args) => (a(...args) * b(...args) + c(...args));
const add = (a, b) => (...args) => (a(...args) + b(...args));
const subtract = (a, b) => (...args) => (a(...args) - b(...args));
const multiply = (a, b) => (...args) => (a(...args) * b(...args));
const divide = (a, b) => (...args) => (a(...args) / b(...args));
const negate = a => (...args) => -a(...args);
const floor = a => (...args) => Math.floor(a(...args));
const ceil = a => (...args) => Math.ceil(a(...args));
const one = (...args) => cnst(1)(...args);
const two = (...args) => cnst(2)(...args);

const operations = {
    // :NOTE: это точно не то место, где нужно реверсить аргументы, это нужно делать в парсере
    "*+": (x, y, z) => madd(z, y, x),
    "+": (x, y) => add(y, x),
    "-": (x, y) => subtract(y, x),
    "*": (x, y) => multiply(y, x),
    "/": (x, y) => divide(y, x),
    "negate": x => negate(x),
    "_": x => floor(x),
    "^": x => ceil(x),
    "one": () => one,
    "two": () => two
};
const parse = expression => (...args) => {
    return expression.trim().split(/\s+/).reduce((stack, token) => {
        if (token in operations) {
            let a = [];
            // :NOTE: для такого цикла есть стандартная функция splice
            for (let i = 0; i < operations[token].length; i++) {
                a.push(stack.pop());
            }
            stack.push(operations[token](...a));
            // :NOTE: вот так захардкодить x y z гораздо хуже, чем просто завести множество подходящих имен
        } else if (Math.abs(token.charCodeAt(0) - "y".charCodeAt(0)) <= 1) {
            stack.push(variable(token));
        } else {
            stack.push(cnst(parseFloat(token)));
        }
        return stack;
    }, []).pop()(...args);
}