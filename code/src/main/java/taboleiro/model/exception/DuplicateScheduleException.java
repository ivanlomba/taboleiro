package taboleiro.model.exception;

public class DuplicateScheduleException extends InstanceException {

    public DuplicateScheduleException(Object key, String className) {
        super("Duplicate Schedule", key, className);
    }
}