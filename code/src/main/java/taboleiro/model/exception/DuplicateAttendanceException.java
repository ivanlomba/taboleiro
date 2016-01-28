package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DuplicateAttendanceException extends InstanceException {

    public DuplicateAttendanceException(Object key, String className) {
        super("Duplicate attendance", key, className);
    }
}
