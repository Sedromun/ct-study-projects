import java.util.Scanner;

public class I_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        final int MAX = 100000001;
        int xl = MAX, xr = -MAX, yl = MAX, yr = -MAX;
        for (int i = 0; i < n; i++) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            int h = sc.nextInt();
            xl = Math.min(xl, x - h);
            xr = Math.max(xr, x + h);
            yl = Math.min(yl, y - h);
            yr = Math.max(yr, y + h);
        }
        System.out.print((xl + xr) / 2 + " " + (yl + yr) / 2 + " ");
        System.out.println((Math.max(xr - xl, yr - yl) - 1) / 2 + 1);

    }
}
