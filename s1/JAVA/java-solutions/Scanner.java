import java.io.*;
import java.util.Arrays;

class Scanner {
    private final int BufSize = 2048;
    private char[] buffer = new char[BufSize];
    private Reader reader;
    private int curCh = 0;
    private int read = -1;

    public Scanner(String file, String encoding) {
        try {
            reader = new InputStreamReader(new FileInputStream(file), encoding);
        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exists: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding: " + e.getMessage());
        }
    }

    public boolean endOfLine() {
        boolean eol = false;
        boolean sln = false;
        try {
            if (read == -1) {
                read = reader.read(buffer);
                curCh = 0;
            }
            char c = buffer[curCh];
            char prev = ' ';

            while (read >= 0) {
                while (!(Character.isLetter(c) ||
                        Character.getType(c) == Character.DASH_PUNCTUATION
                        || c == '\'')
                        && c != '\n' && c != '\r' && curCh < buffer.length) {
                    curCh++;
                    if (curCh == buffer.length) {
                        read = reader.read(buffer);
                        curCh = 0;
                    }
                    prev = c;
                    c = buffer[curCh];
                }

                if (curCh == buffer.length && c != '\n' && c != '\r') {
                    read = reader.read(buffer);
                    curCh = 0;
                    prev = c;
                    c = buffer[curCh];
                } else {
                    break;
                }
            }

            StringBuilder sb1 = new StringBuilder().append(prev).append(c);
            StringBuilder sb2 = new StringBuilder().append(c);
            StringBuilder sb3 = new StringBuilder();
            if (curCh + 1 < buffer.length) {
                sb3.append(c).append(buffer[curCh + 1]);
            }


            if((sb1.toString()).equals(System.lineSeparator()) || sb2.toString().equals(System.lineSeparator())) {
                sln = true;
            } else if (curCh + 1 < buffer.length && sb3.toString().equals(System.lineSeparator())) {
                sln = true;
                curCh++;
            }
        } catch (IOException e) {
            System.out.println("Input exception in endOfLine method: " + e.getMessage());
        }
        return sln;
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Input exception int close method: " + e.getMessage());
        }
    }

    public boolean hasNext() {
        boolean inWord = false;
        try {
            if (read == -1) {
                read = reader.read(buffer);
                if (read >= 0) {
                    buffer = Arrays.copyOf(buffer, read);
                }
            }

            int len = buffer.length;

            while (read >= 0 && !inWord) {

                while (curCh < len && !inWord) {
                    char c = buffer[curCh];

                    if (Character.isLetter(c) ||
                            Character.getType(c) == Character.DASH_PUNCTUATION
                            || c == '\'') {
                        inWord = true;
                        break;
                    }

                    curCh++;

                }

                if (!inWord) {
                    read = reader.read(buffer);
                    if (read >= 0) {
                        buffer = Arrays.copyOf(buffer, read);
                    }
                    curCh = 0;
                    len = buffer.length;
                }
            }
        } catch (IOException e) {
            System.out.println("Input exception in method hasNext: " + e.getMessage());
        }
        return inWord;
    }

    public String next() {
        StringBuilder str = new StringBuilder();
        boolean inWord = false;
        boolean finishedWord = false;
        try {
            if (read == -1) {
                read = reader.read(buffer);
                if (read >= 0) {
                    buffer = Arrays.copyOf(buffer, read);
                }
            }

            int len = buffer.length;

            while (read >= 0 && !finishedWord) {

                while (curCh < len && !inWord) {    //пока не найдем первую букву
                    char c = buffer[curCh];

                    if (Character.isLetter(c) ||
                            Character.getType(c) == Character.DASH_PUNCTUATION
                            || c == '\'') {
                        inWord = true;
                        break;
                    }

                    curCh++;

                }

                while (curCh < len && inWord) {    //пока не закончится слово
                    char c = buffer[curCh];


                    if (!(Character.isLetter(c) ||
                            Character.getType(c) == Character.DASH_PUNCTUATION
                            || c == '\'')) {
                        finishedWord = true;
                        inWord = false;
                        break;
                    }
                    curCh++;
                    str.append(c);
                }

                if (!finishedWord) {
                    read = reader.read(buffer);
                    if (read >= 0) {
                        buffer = Arrays.copyOf(buffer, read);
                    }
                    curCh = 0;
                    len = buffer.length;
                }
            }
        } catch (IOException e) {
            System.out.println("Input exception in next method: " + e.getMessage());
        }
        return str.toString();
    }
}
