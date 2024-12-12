package rushhour.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import rushhour.view.RushHourGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
      

public class RushHour {
    public int BOARD_DIM = 6;
    final char RED_SYMBOL = 'R';
    public final static char EMPTY_SYMBOL= ' ';
    public int moves;
    public HashMap<Character, Vehicle> vehicles = new HashMap<>();
    public Position[][] Grid = new Position[BOARD_DIM][BOARD_DIM];
    final Position EXT_POS = new Position(2, 5);
    public HashMap<Direction, Direction> opposites = new HashMap<>();

    public void initializeGrid() {
        for (int row = 0;row <= BOARD_DIM-1; row++) {
            for (int col = 0; col <= BOARD_DIM-1; col++) {
                Grid[row][col] = new Position(row, col);
                Grid[row][col].symbol = EMPTY_SYMBOL;
            }
        }
    }

    public RushHour(String filename) {
        initializeGrid();
        try {
            fileParser(filename);
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        opposites.put(Direction.DOWN, Direction.UP);
        opposites.put(Direction.UP, Direction.DOWN);
        opposites.put(Direction.LEFT, Direction.RIGHT);
        opposites.put(Direction.RIGHT, Direction.LEFT);
    }

    public RushHour(RushHour other) {
        this.moves = other.moves;
        this.vehicles = new HashMap<>(other.vehicles);
        this.Grid = new Position[BOARD_DIM][BOARD_DIM];
        for (int row = 0; row < BOARD_DIM; row++) {
            for (int col = 0; col < BOARD_DIM; col++) {
                this.Grid[row][col] = new Position(other.Grid[row][col].getRow(), other.Grid[row][col].getCol());
                this.Grid[row][col].setSymbol(other.Grid[row][col].symbol);
            }
        }
        this.observers = new ArrayList<>(); // Initialize observers as an empty list
    }
    


    private List<RushHourObserver> observers = new ArrayList<>();
    public void registerObserver(RushHourObserver observer) {
        observers.add(observer);
    }
    private void notifyObserver(Vehicle vehicle) {
        for (RushHourObserver observer : observers) {
            observer.vehicleMoved(new Vehicle(vehicle)); // Pass a deep copy of the vehicle object
        }
    }
    
    public Collection<Vehicle> getVehicles() {
        List<Vehicle> vehicleList = new ArrayList<>();
        for (Vehicle vehicle : vehicles.values()) {
            vehicleList.add(new Vehicle(vehicle)); // Return deep copies of vehicle objects
        }
        return vehicleList;
    }

    public Vehicle getVehicleBySymbol(char symbol) {
        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.symbol == symbol) {
                return vehicle;
            }
        }
        return null;
    }
    public char getSymbolAt(int row, int col) {
        if (row >= 0 && row < 6 && col >= 0 && col < 6) {
            return Grid[row][col].symbol;
        } else {
            throw new IllegalArgumentException("Invalid row or column index");
        }
    }   


    public void moveVehicle(Move move) throws RushHourException {
        Vehicle vehicle = vehicles.get(move.symbol);
    
        if (vehicle == null) {
            throw new RushHourException("Vehicle does not exist\n");
        }
    
        Position[][] grid0 = new Position[BOARD_DIM][BOARD_DIM];
    
        // Clear Board and copy board
        int col = 0;
        int row = 0;
    
        for (Position[] i : Grid) {
            col = 0;
            for (Position x : i) {
                grid0[row][col] = new Position(row, col);
                grid0[row][col].symbol = x.symbol;
                if (x.symbol == move.symbol) {
                    Grid[row][col].symbol = EMPTY_SYMBOL;
                }
                col++;
            }
            row++;
        }
    
        Position newBack = vehicle.back.add(move.dir);
        Position newFront = vehicle.front.add(move.dir);
    
        // Check if new Position is clear
        if (newBack.row < 0 || newBack.row >= BOARD_DIM || newBack.col < 0 || newBack.col >= BOARD_DIM || newFront.row < 0 || newFront.row >= BOARD_DIM || newFront.col < 0 || newFront.col >= BOARD_DIM) {
            Grid = grid0;
            throw new RushHourException("Invalid move: Vehicle is at the edge of the board", "Vehicle is at the edge of the board");
        }
    
        if (vehicle.getVertical()) {
            for (int x = 0; x < vehicle.getLength(); x++) {
                if (newFront.row - x < 0 || Grid[newFront.row - x][newFront.col].symbol != EMPTY_SYMBOL) {
                    Grid = grid0;
                    throw new RushHourException("Invalid move: Vehicle is obstructed", "Vehicle is obstructed");
                }
            }
        } else {
            for (int x = 0; x < vehicle.getLength(); x++) {
                if (newFront.col - x < 0 || Grid[newFront.row][newFront.col - x].symbol != EMPTY_SYMBOL) {
                    Grid = grid0;
                    throw new RushHourException("Invalid move: Vehicle is obstructed", "Vehicle is obstructed");
                }
            }
        }
    
        vehicle.move(move.dir);
    
        if (newBack.row < 0 || newBack.row >= BOARD_DIM || newBack.col < 0 || newBack.col >= BOARD_DIM || newFront.row < 0 || newFront.row >= BOARD_DIM || newFront.col < 0 || newFront.col >= BOARD_DIM) {
            Grid = grid0;
            throw new RushHourException("Invalid move: Vehicle is at the edge of the board", "Vehicle is at the edge of the board");
        }
        // Add new position to grid
        if (vehicle.getVertical()) {
            for (int x = 0; x < vehicle.getLength(); x++) {
                Grid[newFront.row - x][newFront.col].symbol = vehicle.getSymbol();
            }
        } else {
            for (int x = 0; x < vehicle.getLength(); x++) {
                Grid[newFront.row][newFront.col - x].symbol = vehicle.getSymbol();
            }
        }
    
        moves++;
        notifyObserver(vehicle); // Notify observers of the successful move
    }
    

    public String GridToString() {
        StringBuilder out = new StringBuilder();
        for (int row = 0; row < BOARD_DIM; row++) {
            for (int col = 0; col < BOARD_DIM; col++) {
                out.append(Grid[row][col].symbol != EMPTY_SYMBOL ? Grid[row][col].symbol : "-");
            }
            if (row == EXT_POS.row) {
                out.append("  < EXIT");
            }
            out.append("\n");
        }
        return out.toString();
    }

    public void fileParser(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String current;
            int lineNumber = 0;
            while((current = reader.readLine()) != null) {
                lineNumber++;
                String[] line = current.split(",");
                if (line.length != 5) {
                    throw new IOException("Invalid line in file: " + lineNumber);
                }
                char symbol = line[0].charAt(0);
                int backRow = Integer.parseInt(line[1]);
                int backCol = Integer.parseInt(line[2]);
                int frontRow = Integer.parseInt(line[3]);
                int frontCol = Integer.parseInt(line[4]);
                if (backRow < 0 || backRow >= BOARD_DIM || backCol < 0 || backCol >= BOARD_DIM ||
                        frontRow < 0 || frontRow >= BOARD_DIM || frontCol < 0 || frontCol >= BOARD_DIM) {
                    throw new IOException("Invalid position in file: " + lineNumber);
                }
                Grid[backRow][backCol].symbol = symbol;
                Grid[frontRow][frontCol].symbol = symbol;


                Position back = new Position(backRow, backCol);
                Position front = new Position(frontRow, frontCol);

                // create the vehicle object with the correct positions
                Vehicle vehicle = new Vehicle(symbol, back, front);

                // add the vehicle object to the hashmap
                vehicles.put(symbol, vehicle);

                // update the grid with the vehicle's position
                if (vehicle.getVertical() == true) {
                    for (int x = 0; x < vehicle.getLength(); x++) {
                        Grid[back.row+x][back.col].symbol = vehicle.getSymbol();
                    }
                } else {
                    for (int x = 0; x < vehicle.getLength(); x++) {
                        Grid[back.row][back.col+x].symbol = vehicle.getSymbol();
                    }
                }
            }
            reader.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } 
    }
    
    

    public Boolean isGameOver() {
        return Grid[EXT_POS.row][EXT_POS.col].symbol == RED_SYMBOL;
    }

    public int getMoveCount() {
        return moves;
    }

    public int heuristic(RushHour state) {
        Vehicle redCar = state.vehicles.get(RED_SYMBOL);
        int distanceToExit = EXT_POS.col - redCar.getFront().getCol();
        return distanceToExit;
    }

    public Move getHint() throws CloneNotSupportedException {
        // Create a new start node with the current game state
        RushHourNode startNode = new RushHourNode(new RushHour(this), null, new ArrayList<>(), 0, heuristic(this));
    
        // Create a priority queue for the nodes to be visited
        PriorityQueue<RushHourNode> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.priority));
    
        // Create a set to keep track of visited game states
        Set<RushHour> visitedStates = new HashSet<>();
    
        // Add the start node to the queue and mark the current game state as visited
        queue.add(startNode);
        visitedStates.add(this);
    
        // Loop until the queue is empty
        while (!queue.isEmpty()) {
            // Get the node with the highest priority (lowest cost + heuristic value)
            RushHourNode currentNode = queue.poll();
            RushHour currentState = currentNode.state;
    
            // If the current state is the goal state, return the first move in the list of moves from the start node to the current node
            if (currentState.isGameOver()) {
                return currentNode.moves.get(0);
            }
    
            // Get all possible moves from the current state
            List<Move> possibleMoves = currentState.getAllPossibleMoves();
    
            // Loop through each possible move
            for (Move move : possibleMoves) {
                // Create a new game state by cloning the current state
                RushHour newState = new RushHour(currentState);
    
                try {
                    // Attempt to make the move in the new game state
                    newState.moveVehicle(move);
                } catch (RushHourException e) {
                    // If the move is invalid, skip it
                    continue;
                }
    
                // If the new game state has not been visited before
                if (!visitedStates.contains(newState)) {
                    // Add it to the set of visited states
                    visitedStates.add(newState);
    
                    // Create a new list of moves by adding the current move to the list of moves from the start node to the current node
                    List<Move> newMovesList = new ArrayList<>(currentNode.moves);
                    newMovesList.add(move);
    
                    // Calculate the new cost and priority for the new node
                    int newCost = currentNode.cost + 1;
                    int newPriority = newCost + heuristic(newState);
    
                    // Add the new node to the queue
                    queue.add(new RushHourNode(newState, move, newMovesList, newCost, newPriority));
                }
            }
        }
        // If no solution was found, return null
        return null;
    }
    

    

    public List<Move> getAllPossibleMoves(){
        List<Move> possibleMoves = new ArrayList<>();
    
        for (Vehicle vehicle : vehicles.values()) {

            char symbol = vehicle.getSymbol();
    
            if (vehicle.getVertical()) {
                if (vehicle.getBack().getRow() > 0 && Grid[vehicle.getBack().getRow() - 1][vehicle.getFront().getCol()].symbol == EMPTY_SYMBOL) {
                    possibleMoves.add(new Move(symbol, Direction.UP));
                }
                if (vehicle.getFront().getRow() < BOARD_DIM - 1  && Grid[vehicle.getFront().getRow() + 1][vehicle.getFront().getCol()].symbol == EMPTY_SYMBOL) {
                    possibleMoves.add(new Move(symbol, Direction.DOWN));
                }
            } else {
                if (vehicle.getBack().getCol() > 0 && Grid[vehicle.getBack().getRow()][vehicle.getBack().getCol() - 1].symbol == EMPTY_SYMBOL) {
                    possibleMoves.add(new Move(symbol, Direction.LEFT));
                }
                if (vehicle.getFront().getCol() < BOARD_DIM - 1 && Grid[vehicle.getFront().getRow()][vehicle.getFront().getCol() + 1].symbol == EMPTY_SYMBOL) {
                    possibleMoves.add(new Move(symbol, Direction.RIGHT));
                }
            }
        }
        return possibleMoves;
    }
    
    
    // Implement equals and hashCode methods to allow comparison in visitedStates
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RushHour other = (RushHour) obj;
        return this.Grid.equals(other.Grid);
    }

    private static class RushHourNode {
        RushHour state;
        List<Move> moves;
        int cost;
        int priority;
    
        RushHourNode(RushHour state, Move move, List<Move> moves, int cost, int priority) {
            this.state = state;
            this.moves = moves;
            this.cost = cost;
            this.priority = priority;
        }
    }


    public static void main(String[] args) throws RushHourException {
        String filename = "data/03_00.csv";
        RushHour game = new RushHour(filename);
        RushHour copy = new RushHour(game);
        System.out.println();
        copy.moveVehicle(new Move('O', Direction.DOWN));
        System.out.println(game.GridToString());
        System.out.println(copy.GridToString());
    }

    public void registerObserver(RushHourGUI rushHourGUI) {
    }
}