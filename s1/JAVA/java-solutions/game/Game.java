package game;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Game {
    private final boolean log;
    private final Player player1, player2;
    private Board board;

    public Game(final boolean log, Board board, final Player player1, final Player player2) {
        this.log = log;
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
    }


    public int play() {
        while (true) {
           final int result1 = move(board, player1, 1);
            if (result1 != -1) {
                return result1;
            }

            final int result2 = move(board, player2, 2);
            if (result2 != -1) {
                return result2;
            }

        }
    }

    public void addObstacle(int row, int col) {
        board.addObstacle(row, col);
    }

    private int move(final Board board, final Player player, final int no) {
        final Move move;
        try {
            move = player.move(board.getPosition(), board.getCell());
        } catch (Exception e) {
            System.out.println("You lose" + e.getMessage()); // :NOTE: странно делать вывод локально
            return 3 - no;
        }

        final Result result = board.makeMove(move);
        log("Player " + no + " move: " + move);
        log("Position:\n" + board);
        if (result == Result.WIN) {
            log("Player " + no + " won");
            return no;
        } else if (result == Result.LOSE) {
            log("Player " + no + " lose");
            return 3 - no;
        } else if (result == Result.DRAW) {
            log("Draw");
            return 0;
        } else {
            return -1;
        }
    }

    private void log(final String message) {
        if (log) {
            System.out.println(message);
        }
    }

}
