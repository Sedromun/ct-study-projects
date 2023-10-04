package search;

public class BinarySearchUni {

    // :NOTE: что такое X?
    //Pre:
    // a.length > 0
    // X - some integer value for index in a: 0 <= X < a.length
    // for all left < i < j <= X => a[i] < a[j]
    // for all X < i < j < right => a[i] > a[j]
    // left >= -1 && right <= a.length
    // right - left >= 2
    // let 'R0' for X then 2,3 cond => left <= R0 < right

    //Post: left <= R < right
    // for all 0 <= i < j <= R => a[i] < a[j]
    // for all R < i < j < a.length => a[i] > a[j]
    public static int ternarySearch(int[] a, int left, int right) {
        // left <= R0 < right
        if (right - left == 2) {
            // left <= R0 < right && right - left == 2
            return left;
        }

        // left <= R0 < right && right - left > 2
        int middle1 = (left * 2 + right) / 3;
        int middle2 = (left + right * 2) / 3;
        // left <= R0 < right && right - left > 2 && left < middle1 < middle2 < right
        if (a[middle1] <= a[middle2]) {
            // left <= R0 < right && right - left > 2 && left < middle1 < middle2 < right && a[middle1] <= a[middle2]
            // a[middle1] <= a[middle2] && a - increases before R0 and decreases after R0 =>
            // R0 > middle1 because on the subsegment of segment middle1...middle2,
            // middle1...x 'a' - definitely increases

            // then:
            // a.length > 0
            // for all middle1 < i < j <= R0 => a[i] < a[j]
            // for all R0 < i < j < right => a[i] > a[j]
            return ternarySearch(a, middle1, right);
        } else {
            // left <= R0 < right && right - left > 2 && left < middle1 < middle2 < right && a[middle1] > a[middle2]
            // a[middle1] > a[middle2] && a - increases before R0 and decreases after R0 =>
            // R0 < middle2 because on the subsegment of segment middle1...middle2,
            // x...middle2 'a' - definitely decreases

            // then:
            // a.length > 0
            // for all left < i < j <= R0 => a[i] < a[j]
            // for all R0 < i < j < middle2 => a[i] > a[j]
            return ternarySearch(a, left, middle2);
        }

    }

    // :NOTE: нет контракта
    // Pre:
    // args.length > 0
    // for all i = 0 ... args.length-1: args[i] - Integer
    // Exists integer X: 0 <= X < args.length
    // for all 0 <= i < j <= X => int(args[i]) < int(a[j])
    // for all X < i < j < args.length => int(a[i]) > int(a[j])
    public static void main(String[] args) {
        // args.length > 0
        int[] a = new int[args.length];

        // a.length = args.length - 1 > 0
        for (int i = 0; i < args.length; i++) {
            // i < args.length => i-1 < args.length - 1 = a.length && args[i] - Integer
            a[i] = Integer.parseInt(args[i]);
            // a[i-1] = int(args[i])
        }

        // a.length > 0
        // for all -1 < i < j < X => a[i] < a[j]
        // for all X <= i < j < a.length => a[i] > a[j]
        System.out.println(ternarySearch(a, -1, a.length) + 1);
    }
}
