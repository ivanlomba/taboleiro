package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Asignatura no encontrada.")
public class SubjectNotFoundException extends InstanceException {

    public SubjectNotFoundException(Object key, String className) {
        super("Subject not found", key, className);
    }

}