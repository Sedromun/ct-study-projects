package queue;

import java.util.Arrays;

import static queue.ArrayQueueADT.*;
import static queue.ArrayQueueADT.size;

public class ArrayQueueMyTest {
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();
        System.out.println(queue.size());
        for(int i = 1; i < 8; i++) {
            queue.enqueue("e" + i);
        }
        System.out.println(queue.dequeue());
        queue.push("e9");
        System.out.println(Arrays.toString(queue.toArray()));
        System.out.println(queue.dequeue());
        System.out.println(queue.peek());
        System.out.println(queue.remove());
        System.out.println(queue.size());
        while(!(queue.size() == 1)) {
            System.out.println(queue.size() + " : " + queue.dequeue());
        }
        System.out.println(Arrays.toString(queue.toArray()));
        queue.dequeue();
        System.out.println(Arrays.toString(queue.toArray()));
    }
}
