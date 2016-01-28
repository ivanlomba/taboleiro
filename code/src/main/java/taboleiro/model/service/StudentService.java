package taboleiro.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.student.GlobalGrade.Evaluation;
import taboleiro.model.domain.student.*;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.*;
import java.time.LocalDate;
import java.util.List;

public interface StudentService {

    /* * *
     *  Student Operations
     * * */

    Student findStudentById(Long studentId) throws StudentNotFoundException;

    Student findStudentByDni(String dni) throws StudentNotFoundException;

    Page<Student> listStudent(Pageable page);

    Student addStudent(String firstName, String lastName, String dni, LocalDate birthDate, Long guardianId,
                       Long classGroupId) throws DuplicateStudentException, ClassGroupNotFoundException,
                        UserNotFoundException;

    List<Student> findStudentByClassGroup(Long classGroupId) throws ClassGroupNotFoundException;

    Page<Student> findStudentByCourse(Pageable pageable, Long courseId);

    Student updateStudent(Long studentId, String firstName, String lastName, String dni, LocalDate birthDate,
                          Long guardianId, Long classGroupId, String codigoAlumno)
            throws StudentNotFoundException, ClassGroupNotFoundException, UserNotFoundException;

    Student updateCourse(Long studentId, Long courseId) throws StudentNotFoundException, CourseNotFoundException;

    Student enrollStudent(Long classGroupId, Long studentId) throws StudentNotFoundException,
            ClassGroupNotFoundException;

    Student unenrollStudent(Long classGroupId, Long studentId) throws StudentNotFoundException,
            ClassGroupNotFoundException;

    void deleteStudent(Long student) throws StudentNotFoundException;

    List<Student> findStudentByGuardian(Long guardianId);

    /* * *
     *  GlobalGrade Operations
     * * */

    Page<GlobalGrade> listGlobalGrades(Pageable page);

    GlobalGrade findGlobalGrade(Long studentId, Long subjectId, Long schoolYearId,
                               Evaluation evaluation) throws GlobalGradeNotFoundException;

    List<GlobalGrade> findGlobalGradeByStudentAndSchoolYear(Long studentId, Long schoolYearId);

    List<GlobalGrade> findGlobalGradeByStudent(Long studentId);

    Page<GlobalGrade> findGlobalGradeBySubjectAndSchoolYear(Pageable page, Long subjectId, Long schoolYearId);

    GlobalGrade addGlobalGrade(Long studentId, Subject subject, SchoolYear schoolYear, GlobalGradeLevel grade,
                               Evaluation evaluation, String observation) throws DuplicateGlobalGradeException;

    GlobalGrade findGlobalGradeById(Long globalGradeId) throws GlobalGradeNotFoundException;

    GlobalGrade updateGlobalGrade(Long globalGradeId, GlobalGradeLevel grade) throws GlobalGradeNotFoundException;

    List<GlobalGrade> findGlobalGradeByGroupSubjectAndEvaluation(Long groupSubjectId, Evaluation evaluation)
            throws GroupSubjectNotFoundException;

    List<StudentGlobalGradeCO> findGlobalGradeEvByClassGroupSubject(Long classGroupId)
            throws GroupSubjectNotFoundException;

    /* * *
     *  GlobalGradeLevel Operations
     * * */

    Page<GlobalGradeLevel> findAllGlobalGradeLevel(Pageable page);

    GlobalGradeLevel findGlobalGradeLevelById(Long globalGradeLevelId);

    List<GlobalGradeLevel> findGlobalGradeLevelByCourseLevel(Course.CourseLevel courseLevel);

    GlobalGradeLevel addGlobalGradeLevel(String grade, String gradeName, String xadeGrade,
                                         Course.CourseLevel courseLevel)  throws DuplicateGlobalGradeLevelException;

    void deleteGlobalGradeLevel(Long globalGradeLevelId) throws GlobalGradeLevelNotFoundException;

}
