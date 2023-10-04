package md2html;

import markup.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Md2Html {
    private static final List<Triple> markdownBorders = new ArrayList<>();
    private static final StringBuilder text = new StringBuilder();
    private static int curInBord;
    private static final HashSet<Integer> shielded = new HashSet<>();
    
    public static Pairs toAppend(String line, int i, int curInSB) {
        StringBuilder appending = new StringBuilder();
        if (line.charAt(i) == '\\' && i + 1 < line.length() && isMarkupSymbol(line.charAt(i + 1))) {
            i++;
            appending.append(line.charAt(i));
            shielded.add(curInSB);
            if (i + 1 < line.length() && line.charAt(i + 1) == line.charAt(i) && line.charAt(i) != '\'') {
                i++;
                appending.append(line.charAt(i));
                shielded.add(curInSB + 1);
            }
        } else if (line.charAt(i) == '<') {
            appending.append("&lt;");
        } else if (line.charAt(i) == '>') {
            appending.append("&gt;");
        } else if (line.charAt(i) == '&') {
            appending.append("&amp;");
        } else {
            appending.append(line.charAt(i));
        }
        return new Pairs(appending, i);
    }

    public static boolean isMarkupSymbol(char c) {
        return c == '*' || c == '_' || c == '-' || c == '`' || c == '[' || c == ']' || c == '(' || c == ')' || c == '!';
    }

    public static List<ItemsOfParagraph> toParagraph(int begin, int end) {
        List<ItemsOfParagraph> list = new LinkedList<>();
        
        if (curInBord < 0) {
            return List.of(new Text(text.substring(begin, end)));
        }
        
        Triple triple = markdownBorders.get(curInBord);

        if (triple.finish < begin) {
            return List.of(new Text(text.substring(begin, end)));
        }
        
        while (end > begin) {

            curInBord--;
            if(triple.finish < begin) {
                list.add(new Text(text.substring(begin, end)));
                curInBord++;
                break;
            }

            if (triple.type.equals("strong") || triple.type.equals("s")) {
                if (end >= triple.finish + 2) {
                    list.add(new Text(text.substring(triple.finish + 2, end)));
                }
                end = triple.start - 1;
            } else {
                if (end >= triple.finish + 1) {
                    list.add(new Text(text.substring(triple.finish + 1, end)));
                }
                end = triple.start;
            }

            switch (triple.type) {
                case "strong" -> list.add(new Strong(toParagraph(triple.start + 1, triple.finish)));
                case "em" -> list.add(new Emphasis(toParagraph(triple.start + 1, triple.finish)));
                case "s" -> list.add(new Strikeout(toParagraph(triple.start + 1, triple.finish)));
                case "code" -> list.add(new Code(toParagraph(triple.start + 1, triple.finish)));
            }

            if (curInBord < 0) {
                list.add(new Text(text.substring(begin, end)));
                break;
            }

            triple = markdownBorders.get(curInBord);
        }

        Collections.reverse(list);
        return list;
    }

    public static void main(String[] args) {
        try {

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(args[0]),
                    StandardCharsets.UTF_8
            ))) {
                int add = 0;
                boolean inParagraph = false;
                boolean inHeader = false;
                int cntOfHashtag = 0;
                String line;
                StringBuilder textWoImages = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    if (inHeader) {
                        if (line.equals("")) {
                            inHeader = false;
                            textWoImages.append("</h").append(cntOfHashtag).append(">");
                            cntOfHashtag = 0;
                            textWoImages.append(System.lineSeparator());
                        } else {
                            textWoImages.append(System.lineSeparator());
                            for (int i = 0; i < line.length(); i++) {
                                Pairs pair = toAppend(line, i, textWoImages.length());
                                textWoImages.append(pair.sb);
                                i = pair.number;
                            }
                        }
                    } else if (inParagraph) {
                        if (line.equals("")) {
                            inParagraph = false;
                            textWoImages.append("</p>");
                            textWoImages.append(System.lineSeparator());
                        } else {
                            textWoImages.append(System.lineSeparator());
                            for (int i = 0; i < line.length(); i++) {
                                Pairs pair = toAppend(line, i, textWoImages.length());
                                textWoImages.append(pair.sb);
                                i = pair.number;
                            }
                        }
                    } else if (!line.equals("")) {
                        int i = 0;
                        while (i < line.length() && line.charAt(i) == '#') {
                            cntOfHashtag++;
                            i++;
                        }

                        if (cntOfHashtag != 0 && i != line.length() && Character.isWhitespace(line.charAt(i))) {
                            inHeader = true;
                            textWoImages.append("<h").append(cntOfHashtag).append(">");
                            i++;
                        } else {
                            i = 0;
                            textWoImages.append("<p>");
                            inParagraph = true;
                            cntOfHashtag = 0;
                        }

                        for (; i < line.length(); i++) {
                            Pairs pair = toAppend(line, i, textWoImages.length());
                            textWoImages.append(pair.sb);
                            i = pair.number;
                        }
                    }

                }
                if (inHeader) {
                    textWoImages.append("</h").append(cntOfHashtag).append(">");
                }
                if (inParagraph) {
                    textWoImages.append("</p>");
                }

                for (int i = 0; i < textWoImages.length(); i++) {
                    if (!(textWoImages.charAt(i) == '!' && !shielded.contains(i + add)) ||
                            !(textWoImages.charAt(i + 1) == '[' && !shielded.contains(i + add))) {
                        text.append(textWoImages.charAt(i));
                    } else {
                        i += 2;
                        StringBuilder alt = new StringBuilder();
                        while (i < textWoImages.length() &&
                                (textWoImages.charAt(i) != '<' &&
                                        !(textWoImages.charAt(i) == ']' && !shielded.contains(i + add)))) {
                            alt.append(textWoImages.charAt(i));
                            i++;
                        }
                        if (i >= textWoImages.length()) {
                            text.append("![").append(alt);
                        } else if (textWoImages.charAt(i) == '<') {
                            text.append("![").append(alt).append("<");
                        } else {
                            i++;
                            if (textWoImages.charAt(i) != '(') {
                                text.append("![").append(alt).append("]").append(textWoImages.charAt(i));
                            } else {
                                StringBuilder src = new StringBuilder();
                                i++;
                                while (textWoImages.charAt(i) != '<' &&
                                        !(textWoImages.charAt(i) == ')' && !shielded.contains(i + add))) {
                                    src.append(textWoImages.charAt(i));
                                    i++;
                                }
                                if (textWoImages.charAt(i) == '<') {
                                    text.append("![").append(alt).append("](").append(src).append("<");
                                } else {
                                    text.append("<img alt='").append(alt).append("' src='").append(src).append("'>");
                                    add += 14;
                                }
                            }
                        }
                    }
                }


                int index = 1;

                if (text.charAt(index) == 'h') {
                    index++;
                }
                index += 2;


                while (index < text.length()) {
                    int doubleStar = -1;
                    int doubleMinus = -1;
                    int singleStar = -1;
                    int singleUnderline = -1;
                    int doubleUnderline = -1;
                    int apostrophe = -1;

                    while (index < text.length() && !(text.charAt(index) == '<' && text.charAt(index + 1) != 'i')) {

                        if (text.charAt(index) == '<') {
                            while (text.charAt(index) != '>')
                                index++;
                        }


                        if (text.charAt(index) == '`' && !shielded.contains(index - add)) {
                            if (apostrophe == -1) {
                                apostrophe = index;
                            } else {
                                markdownBorders.add(new Triple("code", apostrophe, index));
                                apostrophe = -1;
                            }
                        }

                        if (text.charAt(index) == '-' && text.charAt(index + 1) == '-' && !shielded.contains(index - add)) {
                            if (doubleMinus == -1) {
                                index++;
                                doubleMinus = index;
                            } else {
                                markdownBorders.add(new Triple("s", doubleMinus, index));
                                index++;
                                doubleMinus = -1;
                            }
                        }


                        if (text.charAt(index) == '*' && text.charAt(index + 1) != '*' && !shielded.contains(index - add)) {
                            if (singleStar == -1) {
                                singleStar = index;
                            } else {
                                markdownBorders.add(new Triple("em", singleStar, index));
                                singleStar = -1;
                            }
                        }

                        if (text.charAt(index) == '*' && text.charAt(index + 1) == '*') {

                            if (shielded.contains(index - add)) {
                                index++;
                            } else if (doubleStar == -1) {
                                index++;
                                doubleStar = index;
                            } else {
                                markdownBorders.add(new Triple("strong", doubleStar, index));
                                index++;
                                doubleStar = -1;
                            }
                        }


                        if (text.charAt(index) == '_' && text.charAt(index + 1) != '_' && !shielded.contains(index - add)) {
                            if (singleUnderline == -1) {
                                singleUnderline = index;
                            } else {
                                markdownBorders.add(new Triple("em", singleUnderline, index));
                                singleUnderline = -1;
                            }
                        }

                        if (text.charAt(index) == '_' && text.charAt(index + 1) == '_') {
                            if (shielded.contains(index - add)) {
                                index++;
                            } else if (doubleUnderline == -1) {
                                index++;
                                doubleUnderline = index;
                            } else {
                                markdownBorders.add(new Triple("strong", doubleUnderline, index));
                                index++;
                                doubleUnderline = -1;
                            }
                        }


                        index++;
                    }

                    if (text.charAt(index + 2) == 'h') {
                        index++;
                    }
                    index += 7;
                    if (index < text.length() && text.charAt(index) == 'h') {
                        index++;
                    }
                    index += 2;

                }

            }
        
            curInBord = markdownBorders.size() - 1;

            Paragraph paragraph = new Paragraph(toParagraph(0, text.length()));
            StringBuilder answer = new StringBuilder();
            paragraph.toHtml(answer);
            text.setLength(0);
            markdownBorders.clear();
            shielded.clear();
            
            try {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(args[1]),
                        StandardCharsets.UTF_8
                ))) {
                    writer.write(answer.toString());
                    answer.setLength(0);
                }
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported encoding exception " + e.getMessage());
            } catch (FileNotFoundException e) {
                System.out.println("Output file was not found " + e.getMessage());
            } catch (IOException e){
                System.out.println("Output writing in file error " + e.getMessage());
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding exception " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Inout file was not found " + e.getMessage());
        } catch (IOException e){
            System.out.println("Input reading in file error " + e.getMessage());
        }
    }
}
