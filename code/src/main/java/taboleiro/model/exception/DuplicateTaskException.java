package taboleiro.model.exception;


public class DuplicateTaskException extends InstanceException {

    public DuplicateTaskException(Object key, String className) {
        super("Duplicate task", key, className);
    }
}
