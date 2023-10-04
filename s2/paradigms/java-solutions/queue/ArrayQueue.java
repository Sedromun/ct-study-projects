package queue;

import java.util.Arrays;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[1];
    private int head = 0, tail = 0; // head - first element, tail - where last element to put
    @Override
    protected void enqueueImpl(Object element) {
        enlargeArray();
        elements[tail] = element;
        tail = shifting(tail, 1);
    }

    private void enlargeArray() {
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

    @Override
    protected Object elementImpl() {
        return elements[head];
    }

    @Override
    protected Object dequeueImpl() {
        Object result = elements[head];
        elements[head] = null;
        head = shifting(head, 1);
        return result;
    }

    @Override
    public int size() {
        return shifting(tail, -head);
    }

    @Override
    public void clear() {
        head = 0;
        tail = 0;
        elements = new Object[1];
    }

    @Override
    protected void pushImpl(Object element) {
        enlargeArray();
        head = shifting(head, -1);
        elements[head] = element;
    }

    @Override
    protected Object peekImpl() {
        return elements[shifting(tail, -1)];
    }

    @Override
    protected Object removeImpl() {
        tail = shifting(tail, -1);
        return elements[tail];
    }

    @Override
    protected void fillArray(Object[] result) {
        if (head <= tail) {
            System.arraycopy(elements, head, result, 0, size());
        } else {
            System.arraycopy(elements, head, result, 0, elements.length - head);
            System.arraycopy(elements, 0, result, elements.length - head, tail);
        }
    }

    private int shifting(int value, int shift) {
        return (elements.length + value + shift) % elements.length;
    }

    @Override
    public boolean contains(Object element) {
        return findFirstOccurrence(element) != -1;
    }

    @Override
    public boolean removeFirstOccurrence(Object element) {
        int index = findFirstOccurrence(element);
        if (index == -1) {
            return false;
        }
        tail = shifting(tail, -1);
        for(int i = index; i != tail; i = shifting(i, 1)) {
            elements[i] = elements[shifting(i, 1)];
        }
        return true;
    }

    private int findFirstOccurrence(Object element) {
        for (int i = head; i != tail; i = shifting(i, 1)) {
            if (elements[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }
}


