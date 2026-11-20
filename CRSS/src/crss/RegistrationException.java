package crss;

/** Thrown when a registration is rejected; the message is shown to the student. */
public class RegistrationException extends Exception {
    public RegistrationException(String message) { super(message); }
}
