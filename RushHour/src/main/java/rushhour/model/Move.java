package rushhour.model;

public class Move {
    public char symbol;
    public Direction dir;

    public Move(char symbol, Direction dir) {
        this.symbol = symbol;
        this.dir = dir;
    }

    public char getSymbol() {
        return symbol;
    }

    public Direction getDir() {
        return dir;
    }

    @Override
    public String toString() {
        return "Move: [symbol=" + symbol + ", direction=" + dir + "]";
    }
}