public class Sum{
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 0; i < args.length; i++) {
            String s = args[i] + " ";
            int first = -1;
            for (int j = 0; j < s.length(); j++) {
                if (!Character.isWhitespace(s.charAt(j))) {
                    if (first == -1) {
                        first = j;
                    }
                } else if (first != -1) {
                    sum += Integer.parseInt(s.substring(first, j));
                    first = -1;
                }
            }
        }
        System.out.println(sum);
    }
}