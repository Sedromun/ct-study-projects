import disassembler.ParseElf;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {

        ParseElf parseElf = new ParseElf(args[1]);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(args[2]),
                StandardCharsets.UTF_8
        ))) {
            writer.write(parseElf.parseText().toString());
            writer.newLine();
            writer.write(parseElf.parseSymbolTable().toString());
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Input/output file reading/writing error: " + e.getMessage());
        }
    }
}