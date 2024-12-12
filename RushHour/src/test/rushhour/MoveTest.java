package rushhour.model;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MoveTest {

    private Move move;

    @BeforeEach
    public void setUp() {
        move = new Move('A', Direction.RIGHT);
    }

    @Test
    public void testGetSymbol() {
        char expectedSymbol = 'A';
        assertEquals(expectedSymbol, move.getSymbol());
    }

    @Test
    public void testGetDir() {
        Direction expectedDirection = Direction.RIGHT;
        assertEquals(expectedDirection, move.getDir());
    }

    @Test
    public void testToString() {
        String expectedString = "Move: [symbol=A, direction=RIGHT]";
        assertEquals(expectedString, move.toString());
    }
}