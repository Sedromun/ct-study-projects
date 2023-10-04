package search;

public class BinarySearch {
    //Pre:
    // a.length > 0
    // for all 0 <= i < j < a.length => a[i] >= a[j]
    // we consider a[-1] = INF, a[a.length] = -INF

    //Post: a[R] <= x < a[R - 1] && 0 <= R < a.length
    public static int iterBinarySearch(int x, int[] a) {
        int left = -1, right = a.length;

        // a[left] > x >= a[right] && right = a.length > 0 && left = -1
        // a[left] > x >= a[right] && right - left > 1
        while (right - left > 1) {
            // a[left] > x >= a[right] && right - left > 1
            int middle = (left + right) / 2;
            // a[left] > x >= a[right] && right - left > 1 && left < middle < right

            if (a[middle] <= x) {
                // a[left] > x >= a[right] && right - left > 1 && left < middle < right && a[middle] <= x
                // a[left] > x >= a[middle] && right - left > 1 && left < middle < right
                right = middle;
                // a[left] > x >= a[right]
            } else {
                // a[left] > x >= a[right] && right - left > 1 && left < middle < right && a[middle] > x
                // a[middle] > x >= a[right] && right - left > 1 && left < middle < right
                left = middle;
                // a[left] > x >= a[right]
            }
            // a[left] > x >= a[right]
        }
        // a[left] > x >= a[right] && right - left == 1
        return right;
    }

    // :NOTE: нет условия на left right
    //Pre:
    // a.length > 0
    // for all 0 <= i < j < a.length => a[i] >= a[j]
    // we consider a[-1] = INF, a[a.length] = -INF
    // left >= -1 && right <= a.length
    // a[left] > x >= a[right]
    // right - left >= 1

    //Post: a[R] <= x < a[R - 1] && left < R <= right
    public static int recursiveBinarySearch(int x, int[] a, int left, int right) {
        // a[left] > x >= a[right]
        if (right - left == 1) {
            // a[left] > x >= a[right] && right - left == 1
            return right;
        }

        // a[left] > x >= a[right] && right - left > 1
        int middle = (left + right) / 2;
        // a[left] > x >= a[right] && right - left > 1 && left < middle < right

        if (a[middle] <= x) {
            // a[left] > x >= a[right] && right - left > 1 && left < middle < right && a[middle] <= x
            // a[left] > x >= a[middle] && right - left > 1 && left < middle < right

            // a.length > 0
            // for all 0 <= i < j < a.length => a[i] >= a[j]
            // a[left] > x >= a[middle]
            // middle - left >= 1
            return recursiveBinarySearch(x, a, left, middle);
        } else {
            // a[left] > x >= a[right] && right - left > 1 && left < middle < right && a[middle] > x
            // a[middle] > x >= a[right] && right - left > 1 && left < middle < right

            // a.length > 0
            // for all 0 <= i < j < a.length => a[i] >= a[j]
            // a[middle] > x >= a[right]
            // right - middle >= 1
            return recursiveBinarySearch(x, a, middle, right);
        }
    }

    // args.length >= 2
    // for all i = 0 ... args.length-1 args[i] - Integer
    // for all 1 <= i < j < args.length => int(a[i]) >= int(a[j])
    public static void main(String[] args) {
        // 0 < args.length && args[0] - Integer
        int x = Integer.parseInt(args[0]);
        // x = int(args[0])
        // args.length >= 2
        int[] a = new int[args.length - 1];
        int sum = 0;
        // a.length = args.length - 1 > 0
        for (int i = 1; i < args.length; i++) {
            // i < args.length => i-1 < args.length - 1 = a.length && args[i] - Integer
            a[i-1] = Integer.parseInt(args[i]);
            // a[i-1] = int(args[i])
            // sum = SUM(a[j]) j = 0...(i-2)
            sum += a[i-1];
            // sum = sum + a[i-1]
        }


        if (sum % 2 == 0) {
            // sum % 2 == 0
            // we consider a[-1] = INF, a[a.length] = -INF
            // a.length > 0
            // for all 0 <= i < j < a.length => a[i] >= a[j]
            // left = -1, right = a.length, a.length > 0 => right - left >= 1
            // a[-1] = INF, a[a.length] = -INF => a[left] > x >= a[right]
            System.out.println(recursiveBinarySearch(x, a, -1, a.length));
        } else {
            // sum % 2 != 0
            // we consider a[-1] = INF, a[a.length] = -INF
            // a.length > 0
            // for all 0 <= i < j < a.length => a[i] >= a[j]
            System.out.println(iterBinarySearch(x, a));
        }

    }
}
