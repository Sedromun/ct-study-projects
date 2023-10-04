import java.util.Scanner;
import java.util.Arrays;

public class ReverseSum {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int[] allInts = new int[1];
		int[] strLen = new int[1];
		int curNum = 0;
		int curStr = 0;
		int mxLenStr = 0;
		while (sc.hasNextLine()) {
			String curLine = sc.nextLine();
			Scanner readLn = new Scanner(curLine);
			int cnt = 0;
			while (readLn.hasNextInt()) {
				cnt++;
				if (curNum + 1 > allInts.length) {
					allInts = Arrays.copyOf(allInts, allInts.length * 2);
				}
				allInts[curNum] = readLn.nextInt();
				curNum++;
			}
			if (mxLenStr < cnt) {
				mxLenStr = cnt;
			}
			
			if (curStr + 1 > strLen.length) {
				strLen = Arrays.copyOf(strLen, strLen.length * 2);
			}
			
			strLen[curStr] = cnt;
			curStr++;
			
			readLn.close();
		}
		
		strLen = Arrays.copyOf(strLen, curStr);
		allInts = Arrays.copyOf(allInts, curNum);
		
		int[] sumCol = new int[mxLenStr];
		
		for (int i = 0; i < mxLenStr; i++) {
			int sum = 0;
			int curVal = 0;
			for (int j = 0; j < curStr; j++) {
				if (strLen[j] >= i + 1) {
					sum += allInts[curVal + i];
				}
				curVal += strLen[j];
			}
			sumCol[i] = sum;
		}
		int curVal = 0;
		for (int i = 0; i < curStr; i++) {
			int sum = 0;
			int len = strLen[i];
			
			for (int j = 0; j < len; j++) {
				sum += allInts[curVal + j];
			}
			
			for (int j = 0; j < len; j++) {
				System.out.print((sum + sumCol[j] - allInts[curVal + j]) + " ");
			}
			System.out.println();
			curVal += len;
		}
		sc.close();
	}
}

//java -ea -jar ReverseTest.jar Sum