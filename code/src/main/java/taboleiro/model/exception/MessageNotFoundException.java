package taboleiro.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Mensaje no encontrado.")
public class MessageNotFoundException extends InstanceException {

    public MessageNotFoundException(Object key, String className) {
        super("Message not found", key, className);
    }

}