package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Grupo no encontrado.")
public class ClassGroupNotFoundException extends InstanceException {

    public ClassGroupNotFoundException(Object key, String className) {
        super("Class Group not found", key, className);
    }

}