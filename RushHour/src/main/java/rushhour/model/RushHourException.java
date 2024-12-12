package rushhour.model;

public class RushHourException extends Exception {
    private String reason;

    public RushHourException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public RushHourException() {
        super();
    }

    public RushHourException(String message) {
        super(message);
    }

    public RushHourException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RushHourException(Throwable cause) {
        super(cause);
    }
}