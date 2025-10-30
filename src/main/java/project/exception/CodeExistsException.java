package project.exception;

public class CodeExistsException extends RuntimeException {
    public CodeExistsException(String message) {
        super(message);
    }
}
