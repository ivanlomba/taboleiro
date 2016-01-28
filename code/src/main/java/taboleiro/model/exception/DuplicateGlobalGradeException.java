package taboleiro.model.exception;

public class DuplicateGlobalGradeException extends InstanceException {

    public DuplicateGlobalGradeException(Object key, String className) {
        super("Duplicate global grade", key, className);
    }
}
