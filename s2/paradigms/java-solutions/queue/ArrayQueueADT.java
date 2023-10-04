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

public class ArrayQueueADT {
    private  Object[] elements = new Object[1];
    private int head = 0, tail = 0; // queue.head - first element, tail - where last element to put

    //Pred: element != null
    //Post: n' = n + 1 && a[n'] == element && immutable(n)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;
        enlargeArray(queue);
        queue.elements[queue.tail] = element;
        queue.tail = shifting(queue.elements.length, queue.tail, 1);
    }

    private static void enlargeArray(ArrayQueueADT queue) {
        if (queue.tail + 1 == queue.head) {
            Object[] result = new Object[2 * queue.elements.length];
            System.arraycopy(queue.elements, 0, result, 0, queue.tail + 1);
            System.arraycopy(queue.elements, queue.head, result, queue.elements.length + queue.head, queue.elements.length - queue.head);
            queue.elements = result;
            queue.head += queue.elements.length / 2;
        } else if (queue.tail + 1 == queue.elements.length && queue.head == 0) {
            queue.elements = Arrays.copyOf(queue.elements, 2 * queue.elements.length);
        }
    }

    //Pred: n > 0
    //Post: R == a[1] && immutable(n) && n' == n
    public static Object element(ArrayQueueADT queue) {
        assert size(queue) != 0;
        return queue.elements[queue.head];
    }

    //Pred: n > 0
    //Post: n' = n - 1 && for i = 1..n: a'[i-1] = a[i] && && R == a[1]
    public static Object dequeue(ArrayQueueADT queue) {
        assert size(queue) != 0;
        Object result = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = shifting(queue.elements.length, queue.head,  1);
        return result;
    }

    //Pred: true
    //Post: immutable(n) && n' == n && R == n
    public static int size(ArrayQueueADT queue) {
        return shifting(queue.elements.length, queue.tail, -queue.head);
    }

    //Pred: true
    //Post: immutable(n) & n' == n && R == (n == 0)
    public static boolean isEmpty(ArrayQueueADT queue) {
        return size(queue) == 0;
    }

    //Pred: true
    //Post: n == 0
    public static void clear(ArrayQueueADT queue) {
        queue.head = 0;
        queue.tail = 0;
        queue.elements = new Object[1];
    }

    //Pred: element != null
    //Post: n' = n + 1 && a'[1] == element && for i = 1..n: a'[i+1] == a[i]
    public static void push(ArrayQueueADT queue, Object element) {
        assert element != null;
        enlargeArray(queue);
        queue.head = shifting(queue.elements.length, queue.head, - 1);
        queue.elements[queue.head] = element;
    }

    //Pred: n > 0
    //Post: R == a[n] && immutable(n) && n' == n
    public static Object peek(ArrayQueueADT queue) {
        assert size(queue) != 0;
        return queue.elements[shifting(queue.elements.length, queue.tail, - 1)];
    }

    //Pred: n > 0
    //Post: R == a[n] && n' == n - 1 && immutable(n')
    public static Object remove(ArrayQueueADT queue) {
        assert size(queue) != 0;
        queue.tail = shifting(queue.elements.length, queue.tail, - 1);
        return queue.elements[queue.tail];
    }

    //Pred: true
    //Post: n' == n && immutable(n) && for i=1..n: R[i] == a[i]
    public static Object[] toArray(ArrayQueueADT queue) {
        Object[] result = new Object[size(queue)];
        if (queue.head <= queue.tail) {
            System.arraycopy(queue.elements, queue.head, result, 0, size(queue));
        } else {
            System.arraycopy(queue.elements, queue.head, result, 0, queue.elements.length - queue.head);
            System.arraycopy(queue.elements, 0, result, queue.elements.length - queue.head, queue.tail);
        }
        return result;
    }

    private static int shifting(int length, int value, int shift) {
        return (length + value + shift) % length;
    }
}
