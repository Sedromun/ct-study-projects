:- load_library('alice.tuprolog.lib.DCGLibrary').
make_bool(A, 1) :- A > 0, !.
make_bool(_, 0.0).

operation(op_add, A, B, R) :- R is A + B.
operation(op_subtract, A, B, R) :- R is A - B.
operation(op_multiply, A, B, R) :- R is A * B.
operation(op_divide, A, B, R) :- R is A / B.
operation(op_and, A, B, R) :- make_bool(A, A1), make_bool(B, B1), R is A1 * B1.
operation(op_or, A, B, R) :- make_bool(A, A1), make_bool(B, B1), R1 is A1 + B1, make_bool(R1, R).
operation(op_xor, A, B, R) :- make_bool(A, A1), make_bool(B, B1), R is (A1 + B1) mod 2.
operation(op_negate, A, R) :- R is -A.
operation(op_not, A, R) :- make_bool(A, R1), R is 1 - R1.

lookup(K, [(K, V) | _], V).
lookup(K, [_ | T], V) :- lookup(K, T, V).
lower_case(H, H) :- member(H, [x, y, z]), !.
lower_case('X', x).
lower_case('Y', y).
lower_case('Z', z).

evaluate(const(Value), _, Value).
evaluate(variable(Name), Vars, R) :- atom_chars(Name, [First_letter | _]), lower_case(First_letter, Var), lookup(Var, Vars, R).
evaluate(operation(Op, A, B), Vars, R) :- 
    evaluate(A, Vars, AV), 
    evaluate(B, Vars, BV), 
    operation(Op, AV, BV, R).
evaluate(operation(Op, A), Vars, R) :-
		evaluate(A, Vars, AR),
		operation(Op, AR, R).

nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

expr_p(variable(Name)) -->
  { nonvar(Name, atom_chars(Name, Chars))},
  letters_p(Chars),
  { Chars = [_ | _], atom_chars(Name, Chars) }.

letters_p([]) --> [].
letters_p([H | T]) -->
  { member(H, [x, y, z, 'X', 'Y', 'Z'])},
  [H],
  letters_p(T).

expr_p(const(Value)) -->
  { nonvar(Value, number_chars(Value, Chars))},
  sign_digits_p(Chars),
  { Chars = [_ | _], number_chars(Value, Chars) }.

sign_digits_p(['-' | T]) --> ['-'], digits_p(T), {T \= []}.
sign_digits_p(Chars) --> digits_p(Chars).

digits_p([]) --> [].
digits_p([H | T]) -->
  { member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'])},
  [H],
  digits_p(T).

op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_and) --> ['&'], ['&'].
op_p(op_or) --> ['|'], ['|'].
op_p(op_xor) --> ['^'], ['^'].
op_p(op_not) --> ['!'].
op_p(op_negate) --> {atom_chars('negate', Chars)}, Chars.

rec_del --> [' '], rec_del.
rec_del --> [].
delete_ws(A) --> {A = 1}, rec_del.
delete_ws(A) --> {A = 0}, [].
var(A, 1) :- var(A), !.
var(_, 0).

expr_p(operation(Op, A, B)) --> 
	{var(A, R)},
	['('], delete_ws(R), expr_p(A), delete_ws(R), [' '], op_p(Op), delete_ws(R), [' '], expr_p(B), delete_ws(R), [')'].
expr_p(operation(Op, A)) --> {var(A, R)}, op_p(Op), [' '], delete_ws(R), expr_p(A).

expr_full_p(Expr) --> {var(Expr, R)}, delete_ws(R), expr_p(Expr), delete_ws(R).

infix_str(E, A) :- ground(E), phrase(expr_p(E), C), atom_chars(A, C).
infix_str(E, A) :-   atom(A), atom_chars(A, C), phrase(expr_full_p(E), C).
