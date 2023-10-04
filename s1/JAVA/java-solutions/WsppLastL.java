import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class WsppLastL {

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(args[0], "UTF8");
            LinkedHashMap<String, IntList> numbOfStr = new LinkedHashMap<>();
            int cntLine = 1;
            HashSet<String> lineCont = new HashSet<>();
            try {
                while (sc.hasNext()) {
                    String current = sc.next().toLowerCase();
                    if (numbOfStr.containsKey(current)) {
                        if (lineCont.contains(current)) {
                            numbOfStr.get(current).remove();
                        }
                        numbOfStr.get(current).addToFirst();
                    } else {
                        IntList a = new IntList();
                        a.add(1);
                        numbOfStr.put(current, a);
                    }
                    numbOfStr.get(current).add(cntLine);
                    lineCont.add(current);
                    cntLine++;

                    if (sc.endOfLine()) {
                        cntLine = 1;
                        lineCont.clear();
                    }
                }
            } finally {
                sc.close();
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(args[1]),
                    "UTF8"
            ));
            try {
                for (Map.Entry<String, IntList> entry : numbOfStr.entrySet()) {
                    writer.write(entry.getKey());
                    IntList il = numbOfStr.get(entry.getKey());
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

// java -ea -jar  WsppTest.jar LastL