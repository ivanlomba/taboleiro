package taboleiro.model.exception;

public class DuplicateGlobalGradeLevelException extends InstanceException {

    public DuplicateGlobalGradeLevelException(Object key, String className) {
        super("Duplicate globalGradeLevel", key, className);
    }
}
