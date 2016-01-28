package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Grupo-asignatura no encontrado.")
public class GroupSubjectNotFoundException extends InstanceException {

    public GroupSubjectNotFoundException(Object key, String className) {
        super("GroupSubject not found", key, className);
    }

}