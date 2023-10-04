import java.util.Arrays;


class IntList {
    public int[] arr;
    public int cur;

    public IntList() {
        arr = new int[1];
        cur = 0;
    }

    public void add(int x) {
        if (cur + 1 > arr.length) {
            arr = Arrays.copyOf(arr, arr.length * 2);
        }
        arr[cur] = x;
        cur++;
    }

    public void remove() {
        if (cur == 0) {
            throw new ArrayIndexOutOfBoundsException("Can't remove, because there is no elements");
        }
        cur--;
    }

    public int get(int i) {
        int ans = 0;
        if (i < cur) {
            ans = arr[i];
        } else {
            throw new ArrayIndexOutOfBoundsException("Index is out of bounds");
        }
        return ans;
    }

    public int length() {
        return cur;
    }

    public void addToFirst() {
        arr[0]++;
    }

}