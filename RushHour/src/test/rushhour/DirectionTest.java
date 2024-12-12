package rushhour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DirectionTest {

    @Test
    public void testDirectionValues() {
        assertEquals(Direction.UP, Direction.valueOf("UP"));
        assertEquals(Direction.RIGHT, Direction.valueOf("RIGHT"));
        assertEquals(Direction.DOWN, Direction.valueOf("DOWN"));
        assertEquals(Direction.LEFT, Direction.valueOf("LEFT"));
    }

    @Test
    public void testDirectionOrdinal() {
        assertEquals(0, Direction.UP.ordinal());
        assertEquals(1, Direction.RIGHT.ordinal());
        assertEquals(2, Direction.DOWN.ordinal());
        assertEquals(3, Direction.LEFT.ordinal());
    }
}