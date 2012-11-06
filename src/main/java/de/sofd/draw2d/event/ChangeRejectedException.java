package de.sofd.draw2d.event;

/**
 *
 * @author olaf
 */
public class ChangeRejectedException extends RuntimeException {

    public ChangeRejectedException(Throwable cause) {
        super(cause);
    }

    public ChangeRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangeRejectedException(String message) {
        super(message);
    }

    public ChangeRejectedException() {
    }


    private static final ThreadLocal<ChangeRejectedException> lastException
            = new ThreadLocal<ChangeRejectedException>();

    public static void setLastException(ChangeRejectedException e) {
        lastException.set(e);
    }

    public static ChangeRejectedException getLastException() {
        return lastException.get();
    }

    public static void resetLastException() {
        lastException.set(null);
    }

}
