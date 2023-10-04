package queue;

import java.util.Arrays;

public class LinkedQueue extends AbstractQueue {
    private Node tail;
    private Node head;
    private int size;
    private static class Node {
        private Node next;
        private final Object element;
        private Node(Object element) {
            this.element = element;
        }
    }


    @Override
    protected void enqueueImpl(Object element) {
        Node previousTail = tail;
        tail = new Node(element);
        if(isEmpty()) {
            head = tail;
        } else {
            previousTail.next = tail;
        }
        size++;
    }


    @Override
    protected Object elementImpl() {
        return head.element;
    }


    @Override
    protected Object dequeueImpl() {
        size--;
        Object result = head.element;
        head = head.next;
        emptyCheck();
        return result;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        tail = null;
        head = null;
        size = 0;
    }


    @Override
    protected void pushImpl(Object element) {
        Node newHead = new Node(element);
        if (!emptyCheck()) {
            newHead.next = head;
            head = newHead;
        }
        size++;
    }


    @Override
    protected Object peekImpl() {
        return head.element;
    }

    @Override
    protected Object removeImpl() {
        Object element = head.element;
        head = head.next;
        size--;
        emptyCheck();
        return element;
    }

    @Override
    protected void fillArray(Object[] result) {
        int i = 0;
        if (!isEmpty()) {
            for (Node current = head; current != null; current = current.next) {
                result[i++] = current.element;
            }
        }
    }

    @Override
    public boolean contains(Object element) {
        return findFirstOccurrence(element).next != null;
    }

    @Override
    public boolean removeFirstOccurrence(Object element) {
        Node firstOccurrence = findFirstOccurrence(element);
        if (firstOccurrence.next == null) {
            return false;
        }
        size--;
        if (firstOccurrence.next == head) {
            head = head.next;
        } else if (firstOccurrence.next == tail) {
            tail = firstOccurrence;
            tail.next = null;
        } else {
            firstOccurrence.next = firstOccurrence.next.next;
        }
        return true;
    }

    private boolean emptyCheck() {
        if (isEmpty()) {
            tail = null;
            return true;
        }
        return false;
    }

    private Node findFirstOccurrence(Object element) {
        Node node = new Node(null);
        node.next = head;
        Node current = node;
        while (current.next != null) {
            if (current.next.element.equals(element)) {
                break;
            }
            current = current.next;
        }
        return current;
    }
}
