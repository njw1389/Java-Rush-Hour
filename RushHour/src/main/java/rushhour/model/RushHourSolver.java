package rushhour.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import backtracker.Backtracker;

public class RushHourSolver implements backtracker.Configuration<RushHourSolver> {
    public RushHour rushHour;
    public ArrayList<Move> moves;

    public RushHourSolver(RushHour rushHour, ArrayList<Move> moves) {
        this.rushHour = new RushHour(rushHour);
        this.moves = moves;
    }

    public boolean isGoal() {
        return rushHour.isGameOver();
    }

    @Override
    public boolean isValid() {
        if (rushHour.equals(new RushHour("data/03_00.csv"))) {
                return false;
            }
        return true;
    }
    

    public ArrayList<Move> getMoves() {
        return moves;
    }

    @Override
    public Collection<RushHourSolver> getSuccessors() {
        ArrayList<RushHourSolver> successors = new ArrayList<>();
        List<Move> Moves = rushHour.getAllPossibleMoves();  
        for(Move i : Moves) {
            if(i.dir != Direction.DOWN && rushHour.vehicles.get(i.symbol).vertical) {
                continue;
            }
            RushHour child = new RushHour(rushHour);
            try {
                child.moveVehicle(i);
                ArrayList<Move> childMoves = new ArrayList<>(this.moves); // Copy the current moves list
                childMoves.add(i); // Add the current move to the child's moves list
                successors.add(new RushHourSolver(child, childMoves)); // Pass the updated moves list
                System.out.println(child.GridToString());
            } catch (RushHourException e) {
                // Ignore
            }
        }
        return successors;
    }


    public static RushHourSolver solve(RushHour rushHour) {
        ArrayList<Move> initialMoves = new ArrayList<>();
        RushHourSolver initialSolver = new RushHourSolver(rushHour, initialMoves);
        Backtracker<RushHourSolver> backtracker = new Backtracker<>(true);
        return backtracker.solve(initialSolver);
    }

    public static void main(String[] args) {
        RushHour game = new RushHour("data/03_00.csv");
        RushHourSolver solution = RushHourSolver.solve(game);
        System.out.println(solution.rushHour.GridToString());
    }
}