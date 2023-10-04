package queue;

/*
Model:
    a[1] ... a[n]
Invariant:
    n >= 0
    for i = 1..n:  a[i] != null

Let immutable(n): for i=1..n: a'[i] == a[i]
 */

import java.util.Arrays;

public class ArrayQueueModule {
    private static Object[] elements = new Object[1];
    private static int head = 0, tail = 0; // head - first element, tail - where last element to put

    //Pred: element != null
    //Post: n' = n + 1 && a[n'] == element && immutable(n)
    public static void enqueue(Object element) {
        assert element != null;
        enlargeArray();
        elements[tail] = element;
        tail = shifting(tail, 1);
    }

    private static void enlargeArray() {
        if (tail + 1 == head) {
            Object[] result = new Object[2 * elements.length];
            System.arraycopy(elements, 0, result, 0, tail + 1);
            System.arraycopy(elements, head, result, elements.length + head, elements.length - head);
            elements = result;
            head += elements.length / 2;
        } else if (tail + 1 == elements.length && head == 0) {
            elements = Arrays.copyOf(elements, 2 * elements.length);
        }
    }

    //Pred: n > 0
    //Post: R == a[1] && immutable(n) && n' == n
    public static Object element() {
        assert size() != 0;
        return elements[head];
    }

    //Pred: n > 0
    //Post: n' = n - 1 && for i = 1..n: a'[i-1] = a[i] && && R == a[1]
    public static Object dequeue() {
        assert size() != 0;
        Object result = elements[head];
        elements[head] = null;
        head = shifting(head, 1);
        return result;
    }

    //Pred: true
    //Post: immutable(n) && n' == n && R == n
    public static int size() {
        return shifting(tail, -head);
    }

    //Pred: true
    //Post: immutable(n) & n' == n && R == (n == 0)
    public static boolean isEmpty() {
        return size() == 0;
    }

    //Pred: true
    //Post: n == 0
    public static void clear() {
        head = 0;
        tail = 0;
        elements = new Object[1];
    }

    //Pred: element != null
    //Post: n' = n + 1 && a'[1] == element && for i = 1..n: a'[i+1] == a[i]
    public static void push(Object element) {
        assert element != null;
        enlargeArray();
        head = shifting(head, -1);
        elements[head] = element;
    }

    //Pred: n > 0
    //Post: R == a[n] && immutable(n) && n' == n
    public static Object peek() {
        assert size() != 0;
        return elements[shifting(tail, -1)];
    }

    //Pred: n > 0
    //Post: R == a[n] && n' == n - 1 && immutable(n')
    public static Object remove() {
        assert size() != 0;
        tail = shifting(tail, -1);
        return elements[tail];
    }

    //Pred: true
    //Post: n' == n && immutable(n) && for i=1..n: R[i] == a[i]
    public static Object[] toArray() {
        Object[] result = new Object[size()];
        if (head <= tail) {
            System.arraycopy(elements, head, result, 0, size());
        } else {
            System.arraycopy(elements, head, result, 0, elements.length - head);
            System.arraycopy(elements, 0, result, elements.length - head, tail);
        }
        return result;
    }

    private static int shifting(int value, int shift) {
        return (elements.length + value + shift) % elements.length;
    }
}


