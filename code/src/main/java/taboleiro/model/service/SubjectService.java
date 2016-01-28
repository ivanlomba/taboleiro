package taboleiro.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.controller.subject.SubjectCreateForm;
import taboleiro.controller.subject.TaskCreateForm;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.student.StudentAttendance;
import taboleiro.model.domain.subject.*;
import taboleiro.model.domain.subject.Attendance.FaultType;
import taboleiro.model.exception.*;
import java.time.LocalDate;
import java.util.List;

public interface SubjectService {

    /*
     * Subject Operations
     */

    Subject findSubjectById(Long subjectId) throws SubjectNotFoundException;

    Page<Subject> listSubject(Pageable page);

    Subject addSubject(String subjectName, Long courseId, String codigoMateriaAvaliable)
            throws DuplicateSubjectException, CourseNotFoundException;

    Subject findSubjectBySubjectNameAndCourseName(String subjectName, String courseName)
            throws SubjectNotFoundException, CourseNotFoundException;

    List<Subject> findSubjectByCourse(Long courseId) throws CourseNotFoundException;

    Subject updateSubject(Long subjectId, SubjectCreateForm form) throws SubjectNotFoundException;

    void deleteSubject(Long subjectId) throws SubjectNotFoundException;

    /*
     * Attendance Operations
     */

    Page<Attendance> listAllAttendance(Pageable page);

    Attendance findAttendanceById(Long attendanceId) throws AttendanceNotFoundException;

    Attendance findAttendance(Long studentId, Long subjectId, LocalDate date) throws AttendanceNotFoundException;

    Attendance addAttendance(Student student, Subject subject, LocalDate date, ClassGroup classGroup, FaultType type)
            throws DuplicateAttendanceException;

    Attendance addAttendance(Student student, Subject subject, ClassGroup classGroup, FaultType type)
            throws DuplicateAttendanceException;

    Page<Attendance> findAttendanceByStudent(Pageable page, Long studentId);

    Page<Attendance> findNotJustifiedAttendanceByStudent(Pageable page, Long studentId) throws StudentNotFoundException;

    Page<Attendance> findJustifiedAttendanceByStudent(Pageable page, Long studentId) throws StudentNotFoundException;

    Integer countAttendanceByStudentNotJustified(Long studentId) throws StudentNotFoundException;

    void updateJustified(Long attendanceId) throws AttendanceNotFoundException;

    Page<Attendance> findAttendanceByClassGroup(Pageable page, Long classGroupId);

    void deleteAttendance(Long attendanceId) throws AttendanceNotFoundException;

    List<StudentAttendance> AttendanceOfTheDay(Long classGroupId, Long subjectId, LocalDate faultDate)
            throws ClassGroupNotFoundException, SubjectNotFoundException;

    Page<Attendance> notJustifiedAttendanceByClassGroup(Pageable page, Long classGroupId, Long subjectId)
            throws ClassGroupNotFoundException, SubjectNotFoundException;

    Page<Attendance> justifiedAttendanceByClassGroup(Pageable page, Long classGroupId, Long subjectId)
            throws ClassGroupNotFoundException, SubjectNotFoundException;

    /*
     * Task Operations
     */

    Task findTask(String taskName, Long subjectId, LocalDate taskDate, Long classGroupId,
                  GlobalGrade.Evaluation evaluation) throws TaskNotFoundException;

    Task findTaskById(Long taskId) throws TaskNotFoundException;

    Task addTask(String taskName, LocalDate taskDate, Subject subject, ClassGroup classGroup,
                 GlobalGrade.Evaluation evaluation, Task.TaskType taskType) throws DuplicateTaskException;

    Page<Task> findTaskByClassGroup(Pageable page, Long classGroupId);

    Page<Task> findPendingTaskByClassGroup(Pageable page, Long classGroupId);

    Page<Task> findOldTaskByClassGroup(Pageable page, Long classGroupId);

    Page<Task> findExams(Pageable page, Long classGroupId);

    Page<Task> findProjectAndHomework(Pageable page, Long classGroupId);

    Page<Task> findTaskByClassGroupAndSubject(Pageable page, Long classGroupId, Long subjectId);

    Page<Task> findOldTaskByClassGroupAndSubject(Pageable page, Long classGroupId, Long subjectId);

    Task updateTask(Long taskId, TaskCreateForm form) throws TaskNotFoundException;

    void deleteTask(Long taskId) throws TaskNotFoundException;

    List<StudentTaskGrade> findStudentTaskGradeByTask(Long taskId) throws TaskNotFoundException,
            ClassGroupNotFoundException;

    /*
     * View TaskByTeacher
     */

    Page<ViewTeacherTask> findTeacherTask(Long teacherId);

    Page<ViewTeacherTask> findTeacherExam(Long teacherId);

    /*
     * Grade Operations
     */

    Grade findGradebyId(Long GradeId) throws GradeNotFoundException;

    Grade addGrade(Task task, Student student, Integer grade, String observation) throws DuplicateGradeException;

    Grade findGradeByTaskAndStudent(Long taskId, Long studentId) throws GradeNotFoundException;

    List<Grade> findGradeByTask(Long taskId);

    Page<Grade> findGradeByStudent(Pageable page, Long studentId);

    Grade updateGrade(Long gradeId, Integer newGrade, String observation) throws GradeNotFoundException;

}
