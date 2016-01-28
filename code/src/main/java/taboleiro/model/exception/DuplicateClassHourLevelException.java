package taboleiro.model.exception;

public class DuplicateClassHourLevelException extends InstanceException {

    public DuplicateClassHourLevelException(Object key, String className) {
        super("Duplicate classHourLevel", key, className);
    }
}
