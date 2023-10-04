min_div(1, 1).
iter_add(S, _, N) :- S > N.
iter_add(S, Add, N) :- S1 is S + Add, assert(min_div(S, Add)), iter_add(S1, Add, N).
sieve(S, N) :- \+ min_div(S, _), assert(prime(S)), assert(min_div(S, S)), S1 is S * S, iter_add(S1, S, N).

iter(S, F) :- S > F.
iter(S, F) :- S1 is S + 1, (sieve(S, F); true), iter(S1, F).
init(N) :- iter(2, N), !.

composite(N) :- min_div(N, D), N \= D.

factorisation(1, []).
factorisation(N, [Div | T]) :- min_div(N, Div), N1 is N / Div, factorisation(N1, T).

mult(1, _, []).
mult(Acc, Prev, [H | T]) :- prime(H), Prev =< H, mult(Acc1, H, T), Acc is Acc1 * H.

prime_divisors(N, Divisors) :- number(N), factorisation(N, Divisors), !.
prime_divisors(N, Divisors) :- mult(N, 1, Divisors).

pow(_, 0, 1).
pow(A, B, R) :-
   B > 0, 1 is mod(B, 2),
   B1 is B - 1, pow(A, B1, R1),
   R is A * R1.
pow(A, B, R) :-
    B > 0, 0 is mod(B, 2),
    B2 is div(B, 2), pow(A, B2, R2),
    R is R2 * R2.

compact_mult(1, _, []) :- !.
compact_mult(Acc, Prev, [(Div, Pow) | T]) :-
    prime(Div), Prev =< Div, compact_mult(Acc1, Div, T),
    pow(Div, Pow, R), Acc is Acc1 * R.

compact_factorisation(1, []).
compact_factorisation(N, [(D, P) | T]) :-
    min_div(N, D),
    divide_while_can(N, D, N1, P),
    compact_factorisation(N1, T).

divide_while_can(N, Div, N, 0) :- \+ 0 is N mod Div.
divide_while_can(N, Div, ND, R) :- N1 is N / Div, divide_while_can(N1, Div, ND, R1), R is R1 + 1.

compact_prime_divisors(N, CDs) :- number(N), compact_factorisation(N, CDs), !.
compact_prime_divisors(N, CDs) :- compact_mult(N, 1, CDs).