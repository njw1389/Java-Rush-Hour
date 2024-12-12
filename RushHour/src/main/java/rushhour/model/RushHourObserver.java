package rushhour.model;

public interface RushHourObserver {
    /**
     * Called by the RushHour class when a successful vehicle move has been completed.
     * The vehicle object should contain the updated positions to reflect the move.
     * 
     * @param vehicle The updated vehicle object after the move.
     */
    void vehicleMoved(Vehicle vehicle);
}