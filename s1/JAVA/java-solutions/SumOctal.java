public class SumOctal{
	public static void main(String[] args) {
		int sum = 0;
		for (String arg : args) {
			String s = arg + " ";
			int first = -1;
			for (int j = 0; j < s.length(); j++) {
				if (!Character.isWhitespace(s.charAt(j)) && s.charAt(j) != 'O' && s.charAt(j) != 'o') {
					if (first == -1) {
						first = j;
					}
				} else if (first != -1) {
					if (s.charAt(j) == 'O' || s.charAt(j) == 'o') {
						sum += Integer.parseUnsignedInt(s.substring(first, j), 8);
					} else {
						sum += Integer.parseInt(s.substring(first, j));
					}
					first = -1;
				}
			}
		}
		System.out.println(sum);
	}
}
