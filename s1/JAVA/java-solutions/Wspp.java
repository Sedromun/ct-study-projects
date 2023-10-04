import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wspp {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(args[0], "UTF8");
            LinkedHashMap<String, IntList> numbOfStr = new LinkedHashMap<>();
            try {
                int cnt = 1;
                while (sc.hasNext()) {
                    String cur = sc.next().toLowerCase();
                    if (!numbOfStr.containsKey(cur)) {
                        IntList a = new IntList();
                        numbOfStr.put(cur, a);
                    }
                    numbOfStr.get(cur).add(cnt);

                    cnt++;
                }
            } finally {
                sc.close();
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(args[1]),
                    "UTF8"
            ));

            try {
                for (Map.Entry entry : numbOfStr.entrySet()) {
                    IntList il = numbOfStr.get(entry.getKey());
                    writer.write(entry.getKey() + " " + il.length());
                    for (int i = 0; i < il.length(); i++) {
                        writer.write(" " + il.get(i));
                    }
                    writer.newLine();
                }
            } finally {
                writer.close();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Input/output file reading/writing error: " + e.getMessage());
        }
    }
}

// java -ea -jar  WsppTest.jar Base