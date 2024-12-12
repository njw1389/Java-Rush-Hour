package rushhour.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RushHourExceptionTest {

    @Test
    public void testDefaultConstructor() {
        Exception exception = new RushHourException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testMessageConstructor() {
        String message = "Test Message";
        Exception exception = new RushHourException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testMessageCauseConstructor() {
        String message = "Test Message";
        Throwable cause = new Throwable("Test Cause");
        Exception exception = new RushHourException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testCauseConstructor() {
        Throwable cause = new Throwable("Test Cause");
        Exception exception = new RushHourException(cause);
        assertEquals("java.lang.Throwable: Test Cause", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}