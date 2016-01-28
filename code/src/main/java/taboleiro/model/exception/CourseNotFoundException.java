package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="El grupo no existe.")
public class CourseNotFoundException extends InstanceException {

    public CourseNotFoundException(Object key, String className) {
        super("Course not found", key, className);
    }

}