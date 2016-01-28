package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Hora de clase nivel no encontrada.")
public class ClassHourLevelNotFoundException extends InstanceException {

    public ClassHourLevelNotFoundException(Object key, String className) {
        super("ClassHourLevel not found", key, className);
    }

}