import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import java.util.Map;
import java.io.*;
import java.lang.Character;
import java.lang.StringBuilder;

public class WordStatWordsPrefix {
	public static void main(String[] args) {
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]),
					StandardCharsets.UTF_8
			));
			TreeMap<String, Integer> numbOfStr = new TreeMap<>();
			try {
				char[] buffer = new char[1024];
				int read = reader.read(buffer);
				StringBuilder cur = new StringBuilder();
				while (read >= 0) {
					String line = new String(buffer, 0, read);
					for(int i = 0; i < line.length(); i++){
						char c = line.charAt(i);
						boolean symb = Character.isLetter(c) || 
							Character.getType(c) == Character.DASH_PUNCTUATION
							|| c == '\'';
						if (symb) {
							cur.append(c);
						} 
						if (!(cur.toString()).equals("") && !symb) {
							String ad = cur.toString().toLowerCase().substring(0, Math.min(3, cur.length()));
							numbOfStr.put(ad, numbOfStr.getOrDefault(ad, 0) + 1);
							cur = new StringBuilder();
						}
					}
											
					read = reader.read(buffer);
				}
				if(!(cur.toString()).equals("")) {
					String ad = cur.toString().toLowerCase().substring(0, Math.min(3, cur.length()));
					numbOfStr.put(ad, numbOfStr.getOrDefault(ad, 0) + 1);
				}
			} finally {
				reader.close();
			}	
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(args[1]),
						StandardCharsets.UTF_8
				));
				try {
					for (Map.Entry<String, Integer> entry : numbOfStr.entrySet()) {
						String k = entry.getKey();
						Integer v = entry.getValue();
						writer.write(k + " " + v + '\n');
					}
				} finally {
					writer.close();
				}
			} catch (IOException e) {
				System.out.println("Input/output file reading/writing error:" + e.getMessage());
			}
		} catch (FileNotFoundException e) {
			System.out.println("FNFE" + e.getMessage());
		} catch (NullPointerException e) { 
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Input/output file reading/writing error:" + e.getMessage());
		}
	}
}

// java -ea -jar WordStatTest.jar WordsPrefix