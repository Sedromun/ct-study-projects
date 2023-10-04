package game;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class MNKBoard implements Board {
    protected static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.',
            Cell.B, '#'
    );


    private final int m;
    private final int n;
    private final int k;
    private int empty;


    private final Cell[][] cells;
    private Cell turn;

    public MNKBoard(int m, int n, int k) {
        this.m = m;
        this.n = n;
        this.k = k;
        empty = m * n;
        this.cells = new Cell[m][n];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    public void addObstacle(int row, int col) {
        cells[row][col] = Cell.B;
        empty--;
    }

    public Position getPosition() {
        return new Position(this);
    }

    public Cell getCell() {
        return turn;
    }

    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }


    @Override
    public Result makeMove(final Move move) { // :NOTE: копипаста
        if (!isValid(move)) {
            return Result.LOSE;
        }

        cells[move.getRow()][move.getColumn()] = move.getValue();
        int row = move.getRow();
        int col = move.getColumn();
        Cell cur = move.getValue();
        empty--;



        int left = 0, leftUp = 0, up = 0, rightUp = 0, right = 0, rightDown = 0, down = 0, leftDown = 0;

        int i;

        //Left
        i = 0;
        while(i < k && col - i >= 0 && cells[row][col - i] == cur) {
            left++;
            i++;
        }

        //Left - Up
        i = 0;
        while(i < k && Math.min(col, row) - i >= 0 && cells[row-i][col-i] == cur) {
            i++;
            leftUp++;
        }

        //Up
        i = 0;
        while(i < k && row - i >= 0 && cells[row - i][col] == cur) {
            up++;
            i++;
        }

        //Right - Up
        i = 0;
        while(i < k && row - i >= 0 && col + i < n && cells[row-i][col+i] == cur) {
            rightUp++;
            i++;
        }

        //Right
        i = 0;
        while(i < k && i + col < n && cells[row][i + col] == cur) {
            right++;
            i++;
        }

        //Right - Down
        i = 0;
        while(i < k && row + i < m && col + i < n && cells[row+i][col+i] == cur) {
            rightDown++;
            i++;
        }

        //Down
        i = 0;
        while(i < k && i + row < m && cells[i + row][col] == cur) {
            down++;
            i++;
        }

        //Left - Down
        i = 0;
        while(i < k && row + i < m && col - i >= 0 && cells[row+i][col-i] == cur) {
            i++;
            leftDown++;
        }

        if (leftUp + rightDown > k || left + right > k || up + down > k || leftDown + rightUp > k) {
            return Result.WIN;
        }

        if (empty == 0) {
            return Result.DRAW;
        }

        turn = turn == Cell.X ? Cell.O : Cell.X;

        return Result.UNKNOWN;
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        final int tab = String.valueOf(m).length();
        final int ent = String.valueOf(n).length();
        sb.append(" ".repeat(tab + 1));
        for(int i = 1; i <= n; i++) {
            final int curTab = String.valueOf(i).length();
            sb.append(i).append(" ".repeat(ent - curTab + 1));
        }

        for (int r = 1; r <= m; r++) {
            final int curTab = String.valueOf(r).length();
            sb.append("\n").append(r).append(" ".repeat(tab-curTab)).append(' ');
            for (int c = 1; c <= n; c++) {
                sb.append(SYMBOLS.get(cells[r-1][c-1])).append(" ".repeat(ent));
            }
        }
        return sb.toString();
    }
}
