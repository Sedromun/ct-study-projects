package queue;

import java.util.Arrays;

import static queue.ArrayQueueADT.*;

public class ArrayQueueModuleTest {
    public static void main(String[] args) {
        for(int i = 1; i < 8; i++) {
            ArrayQueueModule.enqueue("e" + i);
        }
        System.out.println(ArrayQueueModule.dequeue());
        ArrayQueueModule.push("e9");
        System.out.println(Arrays.toString(ArrayQueueModule.toArray()));
        System.out.println(ArrayQueueModule.dequeue());
        System.out.println(ArrayQueueModule.peek());
        System.out.println(ArrayQueueModule.remove());
        System.out.println(ArrayQueueModule.size());
    }
}
