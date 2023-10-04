import java.util.HashMap;
import java.util.Scanner;

public class M_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int t = sc.nextInt();
        for(int t0 = 0; t0 < t; t0++) {
            int n = sc.nextInt();
            int[] a = new int[n];
            for(int j = 0; j < n; j++) {
                a[j] = sc.nextInt();
            }
            HashMap<Integer, Integer> map = new HashMap<>();
            int ans = 0;
            for (int j = n-1; j > 0; j--) {

                for(int i = 0; i < j; i++) {
                    int val = 2 * a[j] - a[i];
                    ans += map.getOrDefault(val, 0);
                }
                map.put(a[j], map.getOrDefault(a[j], 0) + 1);

            }
            System.out.println(ans);
        }

    }
}
