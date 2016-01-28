package taboleiro.model.exception;

public class DuplicateSubjectException extends InstanceException {

    public DuplicateSubjectException(Object key, String className) {
        super("Duplicate subject", key, className);
    }
}
