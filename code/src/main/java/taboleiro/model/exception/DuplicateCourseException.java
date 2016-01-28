package taboleiro.model.exception;

public class DuplicateCourseException extends InstanceException {

    public DuplicateCourseException(Object key, String className) {
        super("Duplicate course", key, className);
    }
}
