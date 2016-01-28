package taboleiro.model.exception;

public class DuplicateSchoolYearException extends InstanceException {

    public DuplicateSchoolYearException(Object key, String className) {
        super("Duplicate schoolYear", key, className);
    }
}
