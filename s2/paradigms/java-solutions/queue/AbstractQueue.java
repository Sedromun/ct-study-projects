package queue;

public abstract class AbstractQueue implements Queue {

    @Override
    public void enqueue(Object element) {
        assert element != null;
        enqueueImpl(element);
    }

    protected abstract void enqueueImpl(Object element);

    @Override
    public Object element() {
        assert size() != 0;
        return elementImpl();
    }

    protected abstract Object elementImpl();

    @Override
    public Object dequeue() {
        assert size() != 0;
        return dequeueImpl();
    }

    protected abstract Object dequeueImpl();

    //Pred: true
    //Post: immutable(n) && n' == n && R == (n == 0)
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void push(Object element) {
        assert element != null;
        pushImpl(element);
    }

    protected abstract void pushImpl(Object element);

    @Override
    public Object peek() {
        assert size() != 0;
        return peekImpl();
    }

    protected abstract Object peekImpl();

    @Override
    public Object remove() {
        assert size() != 0;
        return removeImpl();
    }

    protected abstract Object removeImpl();

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size()];
        fillArray(result);
        return result;
    }

    protected abstract void fillArray(Object[] result);

}
