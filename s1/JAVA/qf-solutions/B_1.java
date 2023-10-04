import java.util.Scanner;

public class B_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int k = -710 * 25000;
        for(int i = 0; i < n; i++) {
            System.out.println(k);
            k += 710;
        }
    }
}
