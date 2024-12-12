package rushhour.view;

import java.io.File;
import java.util.Scanner;

import rushhour.model.Direction;
import rushhour.model.Move;
import rushhour.model.RushHour;
import rushhour.model.RushHourException;
import rushhour.model.RushHourSolver;

public class RushHourCLI {
    private RushHour rushHour;
    private boolean gameOver;
    private int moveCount;
    private String filename;
    
    public RushHourCLI() {
        this.gameOver = false;
        this.moveCount = 0;
    }
    
    public void run() throws CloneNotSupportedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter a Rush Hour filename: ");
        filename = scanner.nextLine();
        File file = new File(filename);
        while(!file.exists()) {
            System.out.print("\nEnter a Rush Hour filename: ");
            filename = scanner.nextLine();
            file = new File(filename);
        }
        rushHour = new RushHour(filename);
        System.out.println("Type 'help' for help menu.\n");

        while (!gameOver) {
            System.out.println(rushHour.GridToString());
            System.out.println("Moves: " + moveCount);
            System.out.print("\n> ");
            String command = scanner.nextLine().trim();
            String[] tokens = command.split(" ");
            System.out.println();

            try {
                switch (tokens[0]) {
                    case "help":
                        System.out.println("Help Menu:");
                        System.out.println("\thelp - this menu");
                        System.out.println("\tquit - quit");
                        System.out.println("\thint - display a valid move");
                        System.out.println("\treset - reset the game");
                        System.out.println("\solve - solve the game");
                        System.out.println("\t<symbol> <UP|DOWN|LEFT|RIGHT> - move the vehicle one space in the given direction\n");
                        break;
                    case "quit":
                        System.out.println("Goodbye!\n");
                        scanner.close();
                        return;
                    case "hint":
                        Move hint = rushHour.getHint();
                        if (hint == null) {
                            System.out.println("No moves available.\n");
                        } else {
                            System.out.println("Try " + hint.symbol + " " + hint.dir);
                            System.out.println();
                        }
                        break;
                    case "reset":
                        moveCount = 0;
                        rushHour = new RushHour(filename);
                        break;
                    case "solve":
                        RushHourSolver solver = RushHourSolver.solve(rushHour);
                        for(Move i : solver.moves) {
                            if(rushHour.isGameOver()) {
                                System.out.println("Game has been solved");
                            }
                            try {
                                rushHour.moveVehicle(i);
                                System.out.println("\n" + rushHour.GridToString() + "\n" + ">> " + i.symbol + " " + i.dir);
                            } catch (RushHourException e) {
                            }
                        }
                        break;
                    default:
                        if (tokens.length != 2) {
                            System.out.println("Invalid command. Type 'help' for menu.\n");
                            break;
                        }
                        char symbol = tokens[0].charAt(0);
                        Direction direction = Direction.valueOf(tokens[1].toUpperCase());
                        Move move = new Move(symbol, direction);
                        try {
                            rushHour.moveVehicle(move);
                            moveCount++;
                            if (rushHour.isGameOver()) {
                                System.out.println(rushHour.GridToString());
                                System.out.println("Well done!\nType 'reset' to play again\n");
                            }
                        } catch (RushHourException e) {
                            System.out.println(e.getMessage());
                        }
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command. Type 'help' for menu.\n>");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        RushHourCLI cli = new RushHourCLI();
        cli.run();
    }
}
