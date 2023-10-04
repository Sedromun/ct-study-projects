package queue;

import java.util.Arrays;

import static queue.ArrayQueueADT.*;

public class ArrayQueueADTTest {
    public static void main(String[] args) {
        ArrayQueueADT queueADT = new ArrayQueueADT();
        for(int i = 1; i < 8; i++) {
            enqueue(queueADT, "e" + i);
        }
        System.out.println(dequeue(queueADT));
        push(queueADT, "e9");
        System.out.println(Arrays.toString(toArray(queueADT)));
        System.out.println(dequeue(queueADT));
        System.out.println(peek(queueADT));
        System.out.println(remove(queueADT));
        System.out.println(size(queueADT));
    }
}
