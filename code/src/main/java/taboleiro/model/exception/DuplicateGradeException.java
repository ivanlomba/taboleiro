package taboleiro.model.exception;

public class DuplicateGradeException extends InstanceException {

    public DuplicateGradeException(Object key, String className) {
        super("Duplicate grade", key, className);
    }
}
