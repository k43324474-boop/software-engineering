package crss;

/** Thrown when no valid schedule exists; nothing is saved. */
public class ScheduleException extends Exception {
    public ScheduleException(String message) { super(message); }
}
