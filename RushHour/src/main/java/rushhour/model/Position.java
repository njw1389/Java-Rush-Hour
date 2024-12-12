package rushhour.model;

public class Position {
    public int row;
    public int col;
    public char symbol;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
        this.symbol = RushHour.EMPTY_SYMBOL;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public Position add(Direction dir) {
        int newRow = row, newCol = col;
        switch (dir) {
            case UP:
                newRow = row - 1;
                break;
            case DOWN:
                newRow = row + 1;
                break;
            case LEFT:
                newCol = col - 1;
                break;
            case RIGHT:
                newCol = col + 1;
                break;
        }
        return new Position(newRow, newCol);
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}