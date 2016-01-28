package taboleiro.model.exception;

public class DuplicateEventException extends InstanceException {

    public DuplicateEventException(Object key, String className) {
        super("Duplicate event", key, className);
    }
}
