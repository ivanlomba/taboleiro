package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Año escolar no encontrado.")
public class SchoolYearNotFoundException extends InstanceException {

    public SchoolYearNotFoundException(Object key, String className) {
        super("SchoolYear not found", key, className);
    }

}