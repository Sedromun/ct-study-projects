package game;

import java.util.Arrays;

public class Tournament {
    private final int numberOfPlayers;
    private final int m, n, k;
    private final Player[] players;
    private final int[][] games;
    private final int[] sumOfBalls;
    private final int[] obstaclesInRow;
    private final int[] obstaclesInCol;

    public Tournament(int numberOfPlayers, Player[] players, int m, int n, int k, int[] obstaclesInRow, int[] obstaclesInCol) {
        this.numberOfPlayers = numberOfPlayers;
        this.players = players;
        this.m = m;
        this.n = n;
        this.k = k;
        this.games = new int[numberOfPlayers][numberOfPlayers];
        for (int[] row : games) {
            Arrays.fill(row, 0);
        }
        sumOfBalls = new int[numberOfPlayers];
        this.obstaclesInRow = obstaclesInRow;
        this.obstaclesInCol = obstaclesInCol;
    }

    public int game(Player player1, Player player2) {
        final Game game = new Game(false, new MNKBoard(m, n, k), player1, player2);
        int result;
        for(int i = 0; i < obstaclesInCol.length; i++) {
            game.addObstacle(obstaclesInRow[i], obstaclesInCol[i]);
        }
        result = game.play();
        System.out.println("Game result: " + result);
        return result;
    }

    public int[] startTournament() {
        for(int i = 0; i < numberOfPlayers; i++) {
            for(int j = i + 1; j < numberOfPlayers; j++) {
                Player player1 = players[i];
                Player player2 = players[i];
                System.out.println("The game between " + (i + 1) + " is X, " + (j + 1) + " is O");
                int result1 = game(player1, player2);
                if (result1 == 1) {
                    games[i][j] += 3;
                } else if (result1 == 2) {
                    games[j][i] += 3;
                } else {
                    games[i][j]++;
                    games[j][i]++;
                }

                System.out.println("The game between " + (j + 1) + " is X, " + (i + 1) + " is O");
                int result2 = game(player2, player1);
                if (result2 == 2) {
                    games[i][j] += 3;
                } else if (result1 == 1) {
                    games[j][i] += 3;
                } else {
                    games[i][j]++;
                    games[j][i]++;
                }
            }
        }

        int maxSum = 0;
        int numberOfWinners = 0;

        for(int i = 0; i < numberOfPlayers; i++) {
            int sum = 0;
            for(int j = 0; j < numberOfPlayers; j++) {
                sum += games[i][j];
            }
            if (sum > maxSum) {
                maxSum = sum;
                numberOfWinners = 1;
            } else if (sum == maxSum) {
                numberOfWinners++;
            }

            sumOfBalls[i] = sum;
        }

        System.out.println(this);
        int[] winner = new int[numberOfWinners];
        int index = 0;

        for(int i = 0; i < numberOfPlayers; i++) {
            if (sumOfBalls[i] == maxSum) {
                winner[index] = i + 1;
                index++;
            }
        }

        return winner;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final int tab = String.valueOf(numberOfPlayers).length();
        sb.append(" ".repeat(tab + 1)).append('|');
        for(int i = 1; i <= numberOfPlayers; i++) {
            final int curTab = String.valueOf(i).length();
            sb.append(i).append(" ".repeat(tab - curTab + 1));
        }

        sb.append("| SUM");
        final int len = sb.length();
        sb.append('\n').append("-".repeat(len));

        for (int r = 1; r <= numberOfPlayers; r++) {
            final int curTab = String.valueOf(r).length();
            sb.append("\n").append(r).append(" ".repeat(tab-curTab)).append(" |");
            for (int c = 1; c <= numberOfPlayers; c++) {
                sb.append(games[r-1][c-1]).append(" ".repeat(tab));
            }
            sb.append("| ").append(sumOfBalls[r-1]);
        }

        return sb.toString();
    }

}
