package taboleiro.model.exception;

public class DuplicateGroupSubjectException extends InstanceException {

    public DuplicateGroupSubjectException(Object key, String className) {
        super("Duplicate groupSubject", key, className);
    }
}
