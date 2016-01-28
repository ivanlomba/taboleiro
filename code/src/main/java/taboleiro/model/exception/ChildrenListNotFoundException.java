package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Lista de estudiantes no encontrada.")
public class ChildrenListNotFoundException extends InstanceException {

    public ChildrenListNotFoundException(Object key, String className) {
        super("ChildrenList not found", key, className);
    }

}