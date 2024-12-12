package rushhour.model;

public class Vehicle {
    public char symbol;
    public Position back;
    public Position front;
    public int length;
    public Boolean vertical;

    public Vehicle(char symbol, Position back, Position front) {
        this.symbol = symbol;
        this.back = back;
        this.front = front;
        this.vertical = null;
    
        if ((vertical != null && vertical && back.row > front.row) || (vertical != null && !vertical && back.col > front.col)) {
            Position temp = back;
            back = front;
            front = temp;
        }
    
        if (back.col == front.col) {
            this.length = Math.abs(back.row - front.row) + 1;
            this.vertical = true;
        } else {
            this.length = Math.abs(back.col - front.col) + 1;
            this.vertical = false;
        }
    }
    
    public Vehicle(Vehicle other) {
        this.symbol = other.symbol;
        this.back = other.back;
        this.front = other.front;

        if (back.col == front.col) {
            this.length = Math.abs(back.row - front.row) + 1;
            this.vertical = true;
        } else {
            this.length = Math.abs(back.col - front.col) + 1;
            this.vertical = false;
        }
    }
    

    public char getSymbol() {
        return symbol;
    }
    
    public Vehicle(char symbol, boolean vertical, Position back) {
        this.symbol = symbol;
        this.vertical = vertical;
        this.back = back;
    }

    public void setFront(Position front) {
        this.front = front;
    }

    public Position getBack() {
        return back;
    }

    public Position getFront() {
        return front;
    }

    public Boolean getVertical() {
        return vertical;
    }

    public int getLength() {
        return length;
    }

    public void move(Direction dir) throws RushHourException {
        Position newPosition = null;

        if (dir == Direction.DOWN && vertical) {
            newPosition = new Position(front.row + 1, front.col);
        } else if (dir == Direction.UP && vertical) {
            newPosition = new Position(back.row - 1, back.col);
        } else if (dir == Direction.LEFT && !vertical) {
            newPosition = new Position(back.row, back.col - 1);
        } else if (dir == Direction.RIGHT && !vertical) {
            newPosition = new Position(front.row, front.col + 1);
        } else {
            throw new RushHourException("Invalid move direction for the vehicle");
        }

        updatePositions(newPosition);
    }

    public void updatePositions(Position newPosition) {
        if (vertical) {
            if (newPosition.row < back.row) {
                front.row -= 1;
                back = newPosition;
            } else {
                back.row += 1;
                front = newPosition;
            }
        } else {
            if (newPosition.col < back.col) {
                front.col -= 1;
                back = newPosition;
            } else {
                back.col += 1;
                front = newPosition;
            }
        }

    }

    @Override
    public String toString() {
        return symbol + "\nback: " + back .toString()+ "\nfront: " + front.toString();
    }
}