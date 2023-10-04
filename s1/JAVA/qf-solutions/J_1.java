import java.util.Scanner;

public class J_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] a = new int[n][n];
        int[][] ans = new int[n][n];
        for(int i = 0; i < n; i++) {
            String s = sc.next();
            for(int j = 0; j < n; j++) {
                a[i][j] = s.charAt(j) - '0';
            }
        }
        for(int i = 0; i < n-1; i++) {
            // on the i-th row
            for(int j = i+1; j < n; j++) {
                if (a[i][j] == 1) {
                    ans[i][j] = 1;
                } else {
                    continue;
                }
                for (int r = j+1; r < n; r++) {
                    a[i][r] -= a[j][r];
                    if (a[i][r] < 0) {
                        a[i][r] += 10;
                    }
                }
            }
        }

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                System.out.print(ans[i][j]);
            }
            System.out.println();
        }
    }
}
