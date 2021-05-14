package server.common.exception;

public class DuplicateUsernameException extends T3Exception {

    public DuplicateUsernameException() {

    }

    public DuplicateUsernameException(String message) {
        super(message);
    }
}