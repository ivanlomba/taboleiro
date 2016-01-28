package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Nota global- nivel no encontrado.")
public class GlobalGradeLevelNotFoundException extends InstanceException {

    public GlobalGradeLevelNotFoundException(Object key, String className) {
        super("GlobalGradeLevel not found", key, className);
    }

}