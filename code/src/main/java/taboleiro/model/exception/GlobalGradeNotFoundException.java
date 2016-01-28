package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Nota global no encontrada.")
public class GlobalGradeNotFoundException extends InstanceException {

    public GlobalGradeNotFoundException(Object key, String className) {
        super("GlobalGrade not found", key, className);
    }

}