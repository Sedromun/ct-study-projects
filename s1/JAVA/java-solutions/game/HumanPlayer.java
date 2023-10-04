package game;

import java.io.PrintStream;
import java.util.Scanner;

import static java.lang.Character.isWhitespace;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;

    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    public boolean whiteSpaceSequence(String s) {
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        // :NOTE: можно вытащить доску
        out.println("Position");
        out.println(position);
        out.println(cell + "'s move");
        out.println("Enter row and column");
        int row, col;
        Move move = new Move();
        do {
            if (move.getValue() != Cell.E) {
                out.println("Move " + move + " is invalid");
            }
            if (in.hasNextInt()) {
                row = in.nextInt();
            } else {
                String buf = in.nextLine();
                if (!whiteSpaceSequence(buf)) {
                    out.println("\"" + buf + "\"" + " is invalid value, please write two numbers");
                }
                move = new Move();
                continue;
            }

            if(in.hasNextInt()) {
                col = in.nextInt();
            } else {
                String buf = in.nextLine();
                if (!whiteSpaceSequence(buf)) {
                    out.println("\"" + row + buf + "\"" + " is invalid value, please write two numbers");
                }
                move = new Move();
                continue;
            }

            String buf = in.nextLine();
            if(!whiteSpaceSequence(buf)) {
                out.println("\"" + row + " " + col + buf + "\"" + " is invalid value, please write two numbers");
                move = new Move();
                continue;
            }

            move = new Move(row - 1, col - 1, cell);
        } while(!position.isValid(move));

        return move;
    }
}
