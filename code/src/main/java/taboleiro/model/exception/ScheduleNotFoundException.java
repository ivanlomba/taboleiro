package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Horario no encontrado.")
public class ScheduleNotFoundException extends InstanceException {

    public ScheduleNotFoundException(Object key, String className) {
        super("Schedule not found", key, className);
    }

}