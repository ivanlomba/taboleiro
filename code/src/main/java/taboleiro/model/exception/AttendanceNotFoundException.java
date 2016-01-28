package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Falta de asistencia no encontrada.")
public class AttendanceNotFoundException extends InstanceException {

    public AttendanceNotFoundException(Object key, String className) {
        super("Attendance not found", key, className);
    }

}