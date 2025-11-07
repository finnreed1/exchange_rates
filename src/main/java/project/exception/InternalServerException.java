package project.exception;

import java.sql.SQLException;

public class InternalServerException extends SQLException {
    public InternalServerException(String message) { super(message); }
}
