package game;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Position {
    Board board;

    public Position(Board board) {
        this.board = board;
    }

    public boolean isValid(final Move move) {
        return board.isValid(move);
    }

    @Override
    public String toString() {
        return board.toString();
    }
}
