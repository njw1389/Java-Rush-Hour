package rushhour;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PositionTest {
    private Position position;

    @BeforeEach
    public void setUp() {
        position = new Position(2, 3);
    }

    @Test
    public void testConstructor() {
        assertEquals(2, position.getRow());
        assertEquals(3, position.getCol());
        assertEquals(RushHour.EMPTY_SYMBOL, position.symbol);
    }

    @Test
    public void testGetRow() {
        assertEquals(2, position.getRow());
    }

    @Test
    public void testGetCol() {
        assertEquals(3, position.getCol());
    }

    @Test
    public void testToString() {
        assertEquals(String.valueOf(RushHour.EMPTY_SYMBOL), position.toString());
    }
}