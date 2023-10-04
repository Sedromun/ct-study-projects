package queue;
/*
Model:
    a[1] ... a[n]
Invariant:
    n >= 0
    for i = 1..n:  a[i] != null

Let immutable(n): for i=1..n: a'[i] == a[i]
 */
public interface Queue {

    //Pred: element != null
    //Post: n' = n + 1 && a[n'] == element && immutable(n)
    void enqueue(Object element);

    //Pred: n > 0
    //Post: R == a[1] && immutable(n) && n' == n
    Object element();

    //Pred: true
    //Post: immutable(n) && n' == n && R == n
    int size();

    //Pred: n > 0
    //Post: n' = n - 1 && for i = 1..n: a'[i-1] = a[i] && R == a[1]
    Object dequeue();

    //Pred: true
    //Post: immutable(n) && n' == n && R == (n == 0)
    boolean isEmpty();

    //Pred: true
    //Post: n == 0
    void clear();

    //Pred: element != null
    //Post: n' = n + 1 && a'[1] == element && for i = 1..n: a'[i+1] == a[i]
    void push(Object element);

    //Pred: n > 0
    //Post: R == a[n] && immutable(n) && n' == n
    Object peek();

    //Pred: n > 0
    //Post: R == a[n] && n' == n - 1 && immutable(n')
    Object remove();

    //Pred: true
    //Post: n' == n && immutable(n) && for i=1..n: R[i] == a[i]
    Object[] toArray();

    //Pred: true
    //Post n' == n && immutable(n) && if exists 1 <= x <= n: a[x] == element R == true, else R == false
    boolean contains(Object element);

    //Pred: true
    //Post: if exists 1 <= x <= n: a[x] == element: n' == n - 1 &&
    //                                         for i=1..(x-1) a'[i]=a[i] && for i=(x+1)..n a'[i-1] = a[i] && R == true
    //      else: n' == n && immutable(n) && R == false
    boolean removeFirstOccurrence(Object element);
}
