package taboleiro.model.exception;

public class DuplicateUserException extends InstanceException {

    public DuplicateUserException(Object key, String className) {
        super("Duplicate message", key, className);
    }
}
