import java.util.Scanner;
import java.util.Arrays;
import java.io.*;
import java.lang.Character;

public class WordStatInput {
	public static void main(String[] args) {
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]),
				"UTF8"
			));
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(args[1]),
					"UTF8"
				));
				try {
					String line = reader.readLine();
					String[] words = new String[1];
					int[] count = new int[1];
					String cur = "";
					int cnt = 0;
					while (line != null) {
						for(int i = 0; i < line.length(); i++){
							char c = line.charAt(i);
							boolean symb = Character.isLetter(c) || 
								Character.getType(c) == Character.DASH_PUNCTUATION
								|| c == '\'';
							if (symb) {
								cur += c;
							} 
							if ((i == line.length()-1 && !cur.equals("")) || (!cur.equals("") && !symb)) {
								
								cur = cur.toLowerCase();
								
								int i0 = -1;
								for (int j = 0; j < cnt; j++) {
									if (words[j].equals(cur)) {
										i0 = j;
									}
								}
								if (i0 == -1) {
									if (cnt + 1 > words.length) {
										words = Arrays.copyOf(words, words.length * 2);
										count = Arrays.copyOf(count, count.length * 2);
									}
									words[cnt] = cur;
									count[cnt] = 1;
									cnt++;
								} else {
									count[i0]++;
								}
								
								cur = "";
							}
						}
									
						line = reader.readLine();
					}
					for (int i = 0; i < cnt; i++) {
						writer.write(words[i] + " " + count[i]);
						writer.newLine();
					}
				} finally {
					writer.close();
				}
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}