import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Arrays;

public class Reverse {
	public static void main(String[] args) {
		Scanner sc = new Scanner(new InputStreamReader(System.in));
		int[][] numbers = new int[100][];
		int st = 0;
		while (sc.hasNextLine()) {
			String curLine = sc.nextLine();
			Scanner readLn = new Scanner(curLine);
			int[] strArr = new int[10];
			int cnt = 0;
			while (readLn.hasNext()) {
				if (cnt >= strArr.length) {
					strArr = Arrays.copyOf(strArr, 2 * strArr.length);
				}
				strArr[cnt] = readLn.nextInt();
				cnt++;
			}
			
			strArr = Arrays.copyOf(strArr, cnt);
			if(st >= numbers.length) {
				numbers = Arrays.copyOf(numbers, 2 * numbers.length);
			}
			numbers[st] = strArr;
			st++;
			readLn.close();
		}
		
		for(int i = st-1; i >= 0; i--){
			int ln = numbers[i].length;
			for(int j = ln-1; j >= 0; j--){
				System.out.print(numbers[i][j] + " ");
			}
			System.out.println();
		}
		
		sc.close();
	}
}