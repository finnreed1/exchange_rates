package project.exception;

import lombok.Getter;

@Getter
public class FieldsIncorrectException extends RuntimeException {
    public FieldsIncorrectException(String message) {
        super(message);
    }
}
