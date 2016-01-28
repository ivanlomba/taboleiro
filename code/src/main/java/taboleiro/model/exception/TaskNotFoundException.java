package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Tarea no encontrada.")
public class TaskNotFoundException extends InstanceException {

    public TaskNotFoundException(Object key, String className) {
        super("task not found", key, className);
    }

}