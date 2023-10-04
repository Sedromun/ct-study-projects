%% [sons], [keys], value
change([H | T], V, CV, R) :- H = V, R = [CV | T].
change([H | T], V, CV, R) :- H \= V, change(T, V, CV, R1), R = [H | R1].

del([H | T], V, R) :- H = V, R = T.
del([H | T], V, R) :- H \= V, del(T, V, R1), R1 \= [], R = [H | R1].
del([H | T], V, R) :- H \= V, del(T, V, []), R = [H].

last([H], H) :- !.
last([_ | T], R) :- last(T, R).

map_get(TreeMap, Key, Value) :- search(TreeMap, Key, tree23([], [Key], Value)).
search(tree23([], Keys, Value), _, tree23([], Keys, Value)) :- !.
search(tree23([_, Right], [TreeKey, _], _), Key, R) :- TreeKey < Key,  !, search(Right, Key, R).
search(tree23([Left, _], [_, _], _), Key, R) :- !, search(Left, Key, R).
search(tree23([_, _, Right], [_, TreeKey2, _], _), Key, R) :- TreeKey2 < Key, !, search(Right, Key, R).
search(tree23([_, Mid, _], [TreeKey1, _, _], _), Key, R) :- TreeKey1 < Key, !, search(Mid, Key, R).
search(tree23([Left, _ , _], _, _), Key, R) :- !, search(Left, Key, R).

map_build([], tree23(null)).
map_build([(K, V)], tree23([], [K], V)) :- !.
map_build([(K, V) | T], MapTree) :-
	map_build(T, Tree), map_put(Tree, K, V, MapTree).

map_put(tree23(null), Key, Value, tree23([], [Key], Value)) :- !.
map_put(Tree, Key, Value, Result) :- recursion_put(Tree, 0, Key, Value, Sons, Keys), map_put_res(Sons, Keys, Result).

map_put_res([Son], _, Son).
map_put_res([Son1, Son2], [Key1, Key2], tree23([Son1, Son2], [Key1, Key2], _)).

map_putIfAbsent(tree23(null), Key, Value, tree23([], [Key], Value)) :- !.
map_putIfAbsent(Tree, Key, Value, Result) :- recursion_put(Tree, 1, Key, Value, Sons, Keys), map_put_res(Sons, Keys, Result).

% current vertice, PutKey, PutValue, RetSons, RetKeys
recursion_put(tree23([], [Key], Value), _, PutKey, PutValue, [tree23([], [Key], Value), tree23([], [PutKey], PutValue)], [Key, PutKey]) :- PutKey > Key, !.
recursion_put(tree23([], [Key], Value), _, PutKey, PutValue, [tree23([], [PutKey], PutValue), tree23([], [Key], Value)], [PutKey, Key]) :- PutKey < Key, !.
recursion_put(tree23([], [PutKey], _), 0, PutKey, PutValue, [tree23([], [PutKey], PutValue)], [PutKey]) :- !.
recursion_put(tree23([], [PutKey], Value), 1, PutKey, _, [tree23([], [PutKey], Value)], [PutKey]) :- !.

recursion_put(tree23([Left, Right], [Key1, _], _), M, PutKey, PutValue, Sons, Keys) :-
    PutKey > Key1, !, recursion_put(Right, M, PutKey, PutValue, Sons1, Keys1),
    append([Left], Sons1, CurSons), append([Key1], Keys1, CurKeys),
    Sons = [tree23(CurSons, CurKeys, _)], last(CurKeys, NewKey), Keys = [NewKey].

recursion_put(tree23([Left, Right], [_, Key2], _), M, PutKey, PutValue, Sons, Keys) :-
    !, recursion_put(Left, M, PutKey, PutValue, Sons1, Keys1),
    append(Sons1, [Right], CurSons), append(Keys1, [Key2], CurKeys),
    Sons = [tree23(CurSons, CurKeys, _)], Keys = [Key2].

recursion_put(tree23([Left, Middle, Right], [Key1, Key2, _], _), M, PutKey, PutValue, Sons, Keys) :-
    PutKey > Key2, recursion_put(Right, M, PutKey, PutValue, [Son], [Key]), !,
    Sons = [tree23([Left, Middle, Son], [Key1, Key2, Key], _)], Keys = [Key].

recursion_put(tree23([Left, Middle, Right], [Key1, Key2, _], _), M, PutKey, PutValue, Sons, Keys) :-
    PutKey > Key2, !, recursion_put(Right, M, PutKey, PutValue, [Son1, Son2], [SonKey1, SonKey2]),
    Sons = [tree23([Left, Middle], [Key1, Key2], _), tree23([Son1, Son2], [SonKey1, SonKey2], _)], Keys = [Key2, SonKey2].

recursion_put(tree23([Left, Middle, Right], [Key1, _, Key3], _), M, PutKey, PutValue, Sons, Keys) :-
    PutKey > Key1, recursion_put(Middle, M, PutKey, PutValue, [Son], [Key]), !,
    Sons = [tree23([Left, Son, Right], [Key1, Key, Key3], _)], Keys = [Key3].

recursion_put(tree23([Left, Middle, Right], [Key1, _, Key3], _), M, PutKey, PutValue, Sons, Keys) :-
    PutKey > Key1, !, recursion_put(Middle, M, PutKey, PutValue, [Son1, Son2], [SonKey1, SonKey2]),
    Sons = [tree23([Left, Son1], [Key1, SonKey1], _), tree23([Son2, Right], [SonKey2, Key3], _)], Keys = [SonKey1, Key3].

recursion_put(tree23([Left, Middle, Right], [_, Key2, Key3], _), M, PutKey, PutValue, Sons, Keys) :-
    recursion_put(Left, M, PutKey, PutValue, [Son], [Key]), !,
    Sons = [tree23([Son, Middle, Right], [Key, Key2, Key3], _)], Keys = [Key3].

recursion_put(tree23([Left, Middle, Right], [_, Key2, Key3], _), M, PutKey, PutValue, Sons, Keys) :-
    !, recursion_put(Left, M, PutKey, PutValue, [Son1, Son2], [SonKey1, SonKey2]),
    Sons = [tree23([Son1, Son2], [SonKey1, SonKey2], _), tree23([Middle, Right], [Key2, Key3], _)], Keys = [SonKey2, Key3].

map_remove(tree23(null), _, tree23(null)) :- !.
map_remove(tree23([], [Key], _), Key, tree23(null)) :- !.
map_remove(tree23([], [TreeKey], Value), _, tree23([], [TreeKey], Value)) :- !.
map_remove(Tree, Key, Result) :-
    recursion_erase(Tree, Key, null, Sons, Keys),
    map_remove_res(Sons, Keys, Result).

map_remove_res([Son], _, Son) :- !.
map_remove_res(Sons, Keys, tree23(Sons, Keys, _)).

left_right(EK, K, _, Right, Right) :- EK > K, !.
left_right(_, _, Left, _, Left).

recursion_erase(tree23([], [EraseKey], EraseValue), EraseKey, tree23(Psons, Pkeys, _), Sons, Keys) :- !,
    del(Psons, tree23([], [EraseKey], EraseValue), Sons), del(Pkeys, EraseKey, Keys).
recursion_erase(tree23([], [_], _), _, tree23(Psons, Pkeys, _), Psons, Pkeys) :- !.

% nothing changes
recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23(Psons, Pkeys, _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son),
    recursion_erase(Son, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Left1, Right1], [Key11, Key22]), !,
    change(Psons, tree23([Left, Right], [Key1, Key2], _), tree23([Left1, Right1], [Key11, Key22], _), Sons),
    change(Pkeys, Key2, Key22, Keys).

% root
recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, null, Sons, _) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], _), !,
    Sons = [Son].

% gp - 3 sons, np - 2 sons
recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([Pleft, tree23([L, R], [K1, K2], _), Pright], [_, _, Pkey3], _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pleft = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([Son, L, R], [Key, K1, K2], _), Pright], Keys = [K2, Pkey3].

recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([Pleft, tree23([L, R], [K1, K2], _), Pright], [Pkey1, _, _], _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pright = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [Pleft, tree23([L, R, Son], [K1, K2, Key], _)], Keys = [Pkey1, Key].

recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([tree23([L, R], [K1, K2], _), Pmiddle, Pright], [_, _, Pkey3], _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pmiddle = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([L, R, Son], [K1, K2, Key], _), Pright], Keys = [Key, Pkey3].

% gp - 3 sons, np - 3 sons
recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([tree23([L, M, R], [K1, K2, K3], _), Pmiddle, Pright], [_, _, Pkey3], _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pmiddle = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([L, M], [K1, K2], _), tree23([R, Son], [K3, Key], _), Pright], Keys = [K2, Key, Pkey3].

recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([Pleft, tree23([L, M, R], [K1, K2, K3], _), Pright], [_, _, Pkey3], _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pleft = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([Son, L], [Key, K1], _), tree23([M, R], [K2, K3], _), Pright], Keys = [K1, K3, Pkey3].

recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([Pleft, tree23([L, M, R], [K1, K2, K3], _), Pright], [Pkey1, _, _], _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pright = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [Pleft, tree23([L, M], [K1, K2], _), tree23([R, Son], [K3, Key], _)], Keys = [Pkey1, K2, Key].

%gp - 2 sons, np - 3 sons
recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([Pleft, tree23([L, M, R], [K1, K2, K3], _)], _, _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pleft = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([Son, L], [Key, K1], _), tree23([M, R], [K2, K3], _)], Keys = [K1, K3].

recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([tree23([L, M, R], [K1, K2, K3], _), Pright], _, _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pright = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([L, M], [K1, K2], _), tree23([R, Son], [K3, Key], _)], Keys = [K2, Key].

% gp - 2 sons, np - 2 sons
recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([Pleft, tree23([L, R], [K1, K2], _)], _, _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pleft = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([Son, L, R], [Key, K1, K2], _)], Keys = [K2].

recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, tree23([tree23([L, R], [K1, K2], _), Pright], _, _), Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son0),
    recursion_erase(Son0, EraseKey, tree23([Left, Right], [Key1, Key2], _), [Son], [Key]), Pright = tree23([Left, Right], [Key1, Key2], _), !,
    Sons = [tree23([L, R, Son], [K1, K2, Key], _)], Keys = [Key].

erasing(Son, _, EK, C, null, Sons, Keys) :-
    recursion_erase(Son, EK, C, Sons, Keys).

erasing(Son, LastKey, EK, C, tree23(Psons, Pkeys, _), Sons, Keys) :-
    recursion_erase(Son, EK, C, Sons1, Keys1),
    change(Psons, C, tree23(Sons1, Keys1, _), Sons),
    last(Keys1, NewKey), change(Pkeys, LastKey, NewKey, Keys).

recursion_erase(tree23([Left, Right], [Key1, Key2], _), EraseKey, Parent, Sons, Keys) :-
    left_right(EraseKey, Key1, Left, Right, Son), !, erasing(Son, Key2, EraseKey, tree23([Left, Right], [Key1, Key2], _), Parent, Sons, Keys).

recursion_erase(tree23([Left, Middle, Right], [Key1, Key2, Key3], _), EraseKey, Parent, Sons, Keys) :-
    EraseKey > Key2, !, erasing(Right, Key3, EraseKey, tree23([Left, Middle, Right], [Key1, Key2, Key3], _), Parent, Sons, Keys).

recursion_erase(tree23([Left, Middle, Right], [Key1, Key2, Key3], _), EraseKey, Parent, Sons, Keys) :-
    EraseKey > Key1, !, erasing(Middle, Key3, EraseKey, tree23([Left, Middle, Right], [Key1, Key2, Key3], _), Parent, Sons, Keys).

recursion_erase(tree23([Left, Middle, Right], [Key1, Key2, Key3], _), EraseKey, Parent, Sons, Keys) :-
    EraseKey =< Key1, !, erasing(Left, Key3, EraseKey, tree23([Left, Middle, Right], [Key1, Key2, Key3], _), Parent, Sons, Keys).