package rushhour.model;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class VehicleTest {
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        Position back = new Position(0, 0);
        Position front = new Position(0, 2);
        vehicle = new Vehicle('A', back, front);
    }

    @Test
    public void testCopyConstructor() {
        Vehicle original = new Vehicle('A', new Position(0, 0), new Position(0, 1));
        Vehicle copied = new Vehicle(original);
        assertNotSame(original, copied);
        assertEquals(original.getSymbol(), copied.getSymbol());
        assertEquals(original.getFront(), copied.getFront());
        assertEquals(original.getBack(), copied.getBack());
    }

    @Test
    void testGetSymbol() {
        assertEquals('A', vehicle.getSymbol());
    }

    @Test
    void testGetBack() {
        assertEquals(new Position(0, 0), vehicle.getBack());
    }

    @Test
    void testGetFront() {
        assertEquals(new Position(0, 2), vehicle.getFront());
    }

    @Test
    void testGetVertical() {
        assertFalse(vehicle.getVertical());
    }

    @Test
    void testGetLength() {
        assertEquals(3, vehicle.getLength());
    }

    @Test
    void testMove() {
        assertDoesNotThrow(() -> vehicle.move(Direction.RIGHT));
        assertEquals(new Position(0, 1), vehicle.getBack());
        assertEquals(new Position(0, 3), vehicle.getFront());
    }

    @Test
    void testMoveInvalidDirection() {
        assertThrows(RushHourException.class, () -> vehicle.move(Direction.UP));
    }

    @Test
    void testUpdatePositions() {
        vehicle.updatePositions(new Position(0, 3));
        assertEquals(new Position(0, 1), vehicle.getBack());
        assertEquals(new Position(0, 3), vehicle.getFront());
    }

    @Test
    void testToString() {
        String expected = "A\nback: (0, 0)\nfront: (0, 2)";
        assertEquals(expected, vehicle.toString());
    }
}