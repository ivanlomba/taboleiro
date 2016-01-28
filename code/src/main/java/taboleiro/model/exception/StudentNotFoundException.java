package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Estudiante no encontrado.")
public class StudentNotFoundException extends InstanceException {

    public StudentNotFoundException(Object key, String className) {
        super("Student not found", key, className);
    }

}