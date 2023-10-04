package game;

import java.util.Scanner;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m, n, k;
        do {
            System.out.println("Enter m, n, k: ");
            m = sc.nextInt();
            n = sc.nextInt();
            k = sc.nextInt();
        } while(k > Math.max(m, n) || m < 1 || n < 1);

        System.out.println("Enter the number of Obstacles");
        int numberOfObstacles = sc.nextInt();
        int[] obstaclesInRow = new int[numberOfObstacles];
        int[] obstaclesInCol = new int[numberOfObstacles];
        if (numberOfObstacles > 0) {
            System.out.println("Enter " + numberOfObstacles + " obstacles in format (row, col)");
        }

        for (int i = 0; i < numberOfObstacles; i++) {
            int row, col;
            do {
                System.out.println("Enter the " + (i + 1) + " obstacle");
                row = sc.nextInt();
                col = sc.nextInt();
            } while(row > m || col > n || row < 1 || col < 1);
            obstaclesInRow[i] = row - 1;
            obstaclesInCol[i] = col - 1;
        }

        int numberOfPlayers;
        do {
            System.out.println("Enter the number of players: ");
            numberOfPlayers = sc.nextInt();
        } while(numberOfPlayers < 2);
        Player[] players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new RandomPlayer(m, n);
        }

        Tournament tournament = new Tournament(numberOfPlayers, players, m, n, k, obstaclesInRow, obstaclesInCol);

        int[] winner = tournament.startTournament();
        if (winner.length == 1) {
            System.out.println("Winner is " + winner[0]);
        } else {
            System.out.print("Winners are: ");
            for (int i = 0; i < winner.length; i++) {
                System.out.print(winner[i] + " ");
            }
        }


    }
}
