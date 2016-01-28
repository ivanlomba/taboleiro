package taboleiro.model.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

public class DuplicateStudentException extends InstanceException {
    public DuplicateStudentException(Object key, String className) {
        super("Duplicate student", key, className);
    }
}
