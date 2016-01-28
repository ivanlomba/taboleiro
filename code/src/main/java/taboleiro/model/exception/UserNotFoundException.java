package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Usuario no encontrado.")
public class UserNotFoundException extends InstanceException {

    public UserNotFoundException(Object key, String className) {
        super("User not found", key, className);
    }

}