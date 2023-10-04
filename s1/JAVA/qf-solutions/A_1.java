import java.util.Scanner;

public class A_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt(), b = sc.nextInt(), n = sc.nextInt();
        System.out.println(2 * ( (n - b - 1) / (b - a) + 1 ) + 1);
    }
}
