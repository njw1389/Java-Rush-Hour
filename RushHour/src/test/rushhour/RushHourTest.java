package rushhour.model;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import rushhour.model.Direction;
import rushhour.model.Move;
import rushhour.model.RushHour;
import rushhour.model.RushHourException;

import static org.junit.jupiter.api.Assertions.*;

class RushHourTest {
    private RushHour rushHour;
    private String filename = "data/test_data.csv";

    @BeforeEach
    void setUp() {
        rushHour = new RushHour(filename);
    }

    @Test
    public void testRegisterObserver() {
        RushHour rushHour = new RushHour();
        RushHourObserver observer = vehicle -> {};
        rushHour.registerObserver(observer);
    }

    @Test
    public void testNotifyObserver() {
        RushHour rushHour = new RushHour();
        Vehicle vehicle = new Vehicle('A', new Position(0, 0), new Position(0, 1));

        // Create a mock observer to test if it's notified
        class MockObserver implements RushHourObserver {
            boolean notified = false;

            @Override
            public void vehicleMoved(Vehicle vehicle) {
                notified = true;
            }
        }

        MockObserver observer = new MockObserver();
        rushHour.registerObserver(observer);
        // Perform a vehicle move and call the notifyObserver method in RushHour
        assertFalse(observer.notified); // Check before notifying
        rushHour.notifyObserver(vehicle);
        assertTrue(observer.notified); // Check after notifying
    }

    @Test
    void testInitializeGrid() {
        assertNotNull(rushHour.Grid, "Grid should be initialized");
    }

    @Test
    void testFileParser() {
        assertEquals(3, rushHour.vehicles.size(), "File parser should read 3 vehicles from the test data file");
    }

    @Test
    void testMoveVehicle() {
        Move moveRight = new Move('A', Direction.RIGHT);
        try {
            rushHour.moveVehicle(moveRight);
        } catch (RushHourException e) {
            fail("Move vehicle should not throw exception for a valid move");
        }

        Move moveInvalid = new Move('Z', Direction.RIGHT);
        assertThrows(RushHourException.class, () -> rushHour.moveVehicle(moveInvalid), "Move vehicle should throw exception for an invalid vehicle");
    }

    @Test
    void testIsGameOver() {
        assertFalse(rushHour.isGameOver(), "Game should not be over at the beginning");

        // Move the red car to the exit
        try {
            rushHour.moveVehicle(new Move('R', Direction.RIGHT));
            rushHour.moveVehicle(new Move('R', Direction.RIGHT));
            rushHour.moveVehicle(new Move('R', Direction.RIGHT));
        } catch (RushHourException e) {
            fail("Moving red car to exit should not throw exception");
        }

        assertTrue(rushHour.isGameOver(), "Game should be over when the red car reaches the exit");
    }

    @Test
    void testGetHint() {
        Move hint = rushHour.getHint();
        assertNotNull(hint, "Get hint should return a valid move");
    }
}
