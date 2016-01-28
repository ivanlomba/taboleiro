package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Nota no encontrada.")
public class GradeNotFoundException extends InstanceException {

    public GradeNotFoundException(Object key, String className) {
        super("Grade not found", key, className);
    }

}