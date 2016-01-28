package taboleiro.model.exception;

public class DuplicateClassGroupException extends InstanceException {

    public DuplicateClassGroupException(Object key, String className) {
        super("Duplicate class group", key, className);
    }
}
