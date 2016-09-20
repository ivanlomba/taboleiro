package taboleiro.model.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import static com.google.common.base.Preconditions.checkNotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.controller.subject.SubjectCreateForm;
import taboleiro.controller.subject.TaskCreateForm;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.subject.*;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.exception.*;
import taboleiro.model.repository.subject.*;
import taboleiro.model.domain.student.StudentAttendance;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
public class SubjectServiceImpl implements SubjectService {

    private CourseService courseService;
    private StudentService studentService;
    private SubjectRepository subjectRepository;
    private AttendanceRepository attendanceRepository;
    private TaskRepository taskRepository;
    private GradeRepository gradeRepository;
    private ViewTeacherTaskRepository viewTeacherTaskRepository;

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository, AttendanceRepository attendanceRepository,
                              TaskRepository taskRepository, GradeRepository gradeRepository,
                              StudentService studentService, ViewTeacherTaskRepository viewTeacherTaskRepository,
                              CourseService courseService) {

        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.taskRepository = taskRepository;
        this.gradeRepository = gradeRepository;
        this.studentService = studentService;
        this.viewTeacherTaskRepository = viewTeacherTaskRepository;
        this.courseService = courseService;
    }

    /* * *
     *  Subject Operations
     * * */


    @Transactional
    @Override
    public Subject findSubjectById(Long subjectId) throws SubjectNotFoundException {
        Subject s = subjectRepository.findSubjectBySubjectId(subjectId);
        if (s == null) {
            throw new SubjectNotFoundException(subjectId, "subjectId not found");
        } else {
            return s;
        }
    }

    @Transactional
    @Override
    public Page<Subject> listSubject(Pageable page) {
        return subjectRepository.findAll(checkNotNull(page, "page"));
    }

    @Transactional
    @Override
    public Subject findSubjectBySubjectNameAndCourseName(String subjectName, String courseName)
            throws SubjectNotFoundException, CourseNotFoundException {
        checkNotNull(subjectName, "SubjectName");
        checkNotNull(courseName, "CourseName");

        Course c = courseService.findCourseByCourseName(courseName);

        Subject s = subjectRepository.findSubjectBySubjectNameAndCourse(subjectName, c.getCourseId());
        if (s == null) {
            throw new SubjectNotFoundException(subjectName, "subjectName");
        }
        else {
            return s;
        }
    }

    @Transactional
    @Override
    public Subject addSubject(String subjectName, Long courseId, String codigoMateriaAvaliable)
            throws DuplicateSubjectException, CourseNotFoundException {
        checkNotNull(subjectName, "subjectName");
        checkNotNull(courseId, "course");

        Course course = courseService.findCourseById(courseId);
        if(subjectRepository.findSubjectBySubjectNameAndCourse(subjectName, courseId) != null) {
            throw new DuplicateSubjectException(subjectName, Subject.class.getName());
        }
        Subject newSubject = Subject.builder().subjectName(subjectName).course(course)
                .codigoMateriaAvaliable(codigoMateriaAvaliable).build();
        return subjectRepository.save(newSubject);
    }

    @Transactional
    @Override
    public void deleteSubject(Long subjectId) throws SubjectNotFoundException {
        checkNotNull(subjectId, "subjectId");

        Subject subject = subjectRepository.findSubjectBySubjectId(subjectId);
        if (subject == null) {
            throw new SubjectNotFoundException(subjectId, "subjectId not found");
        } else {
            subjectRepository.delete(subject);
        }
    }

    @Transactional
    @Override
    public Subject updateSubject(Long subjectId, SubjectCreateForm form) throws SubjectNotFoundException {
        checkNotNull(subjectId, "subjectId");

        Subject s = subjectRepository.findSubjectBySubjectId(subjectId);
        s.setSubjectName(form.getSubjectName());
        s.setCourse(form.getCourse());
        s.setCodigoMateriaAvaliable(form.getCodigoMateriaAvaliable());
        return s;
    }

    @Transactional
    @Override
    public List<Subject> findSubjectByCourse(Long courseId) throws CourseNotFoundException {
        checkNotNull(courseId, "courseId");
        Course course = courseService.findCourseById(courseId);
        return subjectRepository.findSubjectByCourse(course.getCourseId());
    }

    /*
     * Attendance Operations
     */

    @Transactional
    @Override
    public Page<Attendance> listAllAttendance(Pageable page){
        checkNotNull(page, "page");
        return attendanceRepository.findAll(page);
    }

    public Attendance findAttendanceById(Long attendanceId) throws AttendanceNotFoundException {
        checkNotNull(attendanceId, "attendanceId");

        Attendance attendance = attendanceRepository.findAttendanceByAttendanceId(attendanceId);
        if (attendance == null) {
            throw new AttendanceNotFoundException(attendanceId, "Attendance Not Found");
        }
        else {
            return attendance;
        }
    }

    @Transactional
    @Override
    public Attendance addAttendance(Student student, Subject subject, LocalDate date, ClassGroup classGroup,
                                    Attendance.FaultType type) throws DuplicateAttendanceException {
        checkNotNull(student, "student");
        checkNotNull(subject, "subject");
        checkNotNull(date, "date");
        checkNotNull(classGroup, "classGroup");
        checkNotNull(type, "type");

        if (attendanceRepository.findAttendanceByStudentAndSubjectAndFaultDate(student.getStudentId(),
                subject.getSubjectId(), date) != null) {
            throw new DuplicateAttendanceException(date, Attendance.class.getName());
        }
        Attendance newAtt = Attendance.builder().student(student).subject(subject).faultDate(date)
                .classGroup(classGroup).faultType(type).justified(false).build();
        return attendanceRepository.save(newAtt);
    }

    @Transactional
    @Override
    public Attendance addAttendance(Student student, Subject subject, ClassGroup classGroup, Attendance.FaultType type)
            throws DuplicateAttendanceException {
        checkNotNull(student, "student");
        checkNotNull(subject, "subject");
        checkNotNull(classGroup, "classGroup");
        checkNotNull(type, "type");

        LocalDate date = LocalDate.now(ZoneId.of("GMT+1"));
        if (attendanceRepository.findAttendanceByStudentAndSubjectAndFaultDate(student.getStudentId(),
                subject.getSubjectId(), date) != null) {
            throw new DuplicateAttendanceException(date, Attendance.class.getName());
        }
        Attendance newAtt = Attendance.builder().student(student).subject(subject).faultDate(date)
                .classGroup(classGroup).faultType(type).justified(false).build();
        return attendanceRepository.save(newAtt);
    }

    @Transactional
    @Override
    public Attendance findAttendance(Long studentId, Long subjectId, LocalDate date)
            throws AttendanceNotFoundException {
        checkNotNull(studentId, "student");
        checkNotNull(subjectId, "subject");
        checkNotNull(date, "date");

        Attendance att = attendanceRepository.findAttendanceByStudentAndSubjectAndFaultDate(studentId,
                subjectId, date);
        if (att == null) {
            throw new AttendanceNotFoundException(date,"attendance");
        }
        else {
            return att;
        }
    }

    @Transactional
    @Override
    public Page<Attendance> findAttendanceByStudent(Pageable page, Long studentId){
        checkNotNull(studentId, "student");
        checkNotNull(page, "page");

        return attendanceRepository.findAttendanceByStudent(page, studentId);
    }

    @Transactional
    @Override
    public Page<Attendance> findNotJustifiedAttendanceByStudent(Pageable page, Long studentId)
            throws StudentNotFoundException {
        checkNotNull(studentId, "studentId");
        Student student = studentService.findStudentById(studentId);
        return attendanceRepository.findNotJustifiedAttendanceByStudent(page, student.getStudentId(),
                student.getCurrentClassGroup().getClassGroupId());
    }

    @Transactional
    @Override
    public Page<Attendance> findJustifiedAttendanceByStudent(Pageable page, Long studentId)
            throws StudentNotFoundException {
        checkNotNull(studentId, "studentId");
        Student student = studentService.findStudentById(studentId);
        return attendanceRepository.findJustifiedAttendanceByStudent(page, student.getStudentId(),
                student.getCurrentClassGroup().getClassGroupId());
    }

    @Transactional
    @Override
    public Integer countAttendanceByStudentNotJustified(Long studentId)
            throws StudentNotFoundException {
        checkNotNull(studentId, "studentId");
        Student student = studentService.findStudentById(studentId);
        return attendanceRepository.countAttendanceByStudentNotJustified(student.getStudentId(),
                student.getCurrentClassGroup().getClassGroupId());
    }

    @Transactional
    @Override
    public void updateJustified(Long attendanceId) throws AttendanceNotFoundException {
        checkNotNull(attendanceId, "attendanceId");

        Attendance att = attendanceRepository.findAttendanceByAttendanceId(attendanceId);
        if (att == null) {
            throw new AttendanceNotFoundException(attendanceId,"attendanceId");
        }
        else {
            att.setJustified(true);
        }
    }

    @Transactional
    @Override
    public Page<Attendance> findAttendanceByClassGroup(Pageable page, Long classGroupId) {
        checkNotNull(page, "page");
        checkNotNull(classGroupId, "classGroupId");

        return attendanceRepository.findAttendanceByClassGroup(page, classGroupId);
    }

    @Transactional
    @Override
    public void deleteAttendance(Long attendanceId) throws AttendanceNotFoundException {
        checkNotNull(attendanceId, "attendanceId");

        Attendance att = attendanceRepository.findAttendanceByAttendanceId(attendanceId);
        if (att == null) {
            throw new AttendanceNotFoundException(attendanceId, "AttendanceId");
        }
        else {
            attendanceRepository.delete(att);
        }
    }

    @Transactional
    @Override
    public List<StudentAttendance> AttendanceOfTheDay(Long classGroupId, Long subjectId, LocalDate faultDate)
            throws ClassGroupNotFoundException, SubjectNotFoundException {
        checkNotNull(classGroupId, "classGroupId");
        checkNotNull(subjectId, "subjectId");

        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        Subject s = subjectRepository.findSubjectBySubjectId(subjectId);
        List<Attendance> attendanceList = attendanceRepository
                .findAttendanceByClassGroupAndSubjectAndFaultDate(classGroup.getClassGroupId(), s.getSubjectId(),
                        faultDate);

        List<Student> studentList = studentService.findStudentByClassGroup(classGroup.getClassGroupId());
        List<StudentAttendance> attendanceToday = new ArrayList<>();
        Iterator iter = studentList.iterator();
        String fault;
        while (iter.hasNext()) {
            Iterator attIter = attendanceList.iterator();
            Student student = (Student)iter.next();
            fault = "false";
            while(attIter.hasNext()) {
                Attendance attendance = (Attendance)attIter.next();
                if (attendance.getStudent().getStudentId().equals(student.getStudentId())) {
                    fault = attendance.getFaultType().toString();
                }
            }
            StudentAttendance studentAttendance = new StudentAttendance(
                    student.getStudentId(), student.getFirstName(), student.getLastName(), fault);
            attendanceToday.add(studentAttendance);
        }
        return attendanceToday;
    }

    @Transactional
    @Override
    public Page<Attendance> notJustifiedAttendanceByClassGroup(Pageable page, Long classGroupId, Long subjectId)
            throws ClassGroupNotFoundException, SubjectNotFoundException {
        checkNotNull(classGroupId, "classGroupId");
        checkNotNull(subjectId, "subjectId");

        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        Subject s = subjectRepository.findSubjectBySubjectId(subjectId);

        return attendanceRepository.findAttendanceByClassGroupAndSubjectAndNotJustified(classGroup.getClassGroupId(),
                s.getSubjectId(), page);

    }


    @Transactional
    @Override
    public Page<Attendance> justifiedAttendanceByClassGroup(Pageable page, Long classGroupId, Long subjectId)
            throws ClassGroupNotFoundException, SubjectNotFoundException {
        checkNotNull(classGroupId, "classGroupId");
        checkNotNull(subjectId, "subjectId");

        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        Subject s = subjectRepository.findSubjectBySubjectId(subjectId);

        return attendanceRepository.findAttendanceByClassGroupAndSubjectAndJustified(classGroup.getClassGroupId(),
                s.getSubjectId(), page);

    }

    /*
     * Task Operations
     */

    @Transactional
    @Override
    public Task findTask(String taskName, Long subjectId, LocalDate taskDate, Long classGroupId,
                         GlobalGrade.Evaluation evaluation) throws TaskNotFoundException{
        checkNotNull(taskName, "taskName");
        checkNotNull(taskDate, "taskDate");
        checkNotNull(evaluation,"evaluation");
        checkNotNull(subjectId,"subjectId");
        checkNotNull(classGroupId,"classGroupId");

        Task task = taskRepository.findTaskByTaskNameAndSubjectAndTaskDateAndClassGroupAndEvaluation(taskName,
                subjectId, taskDate, classGroupId, evaluation);
        if (task == null) {
            throw new TaskNotFoundException(taskName, "task");
        }
        else {
            return task;
        }
    }

    @Transactional
    @Override
    public Task findTaskById(Long taskId) throws TaskNotFoundException {
        checkNotNull(taskId, "taskId");

        Task task = taskRepository.findTaskByTaskId(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId, "taskId");
        }
        else {
            return task;
        }
    }

    @Transactional
    @Override
    public Task addTask(String taskName, LocalDate taskDate, Subject subject, ClassGroup classGroup,
                        GlobalGrade.Evaluation evaluation, Task.TaskType taskType) throws DuplicateTaskException {
        checkNotNull(taskName, "taskName");
        checkNotNull(taskDate, "taskDate");
        checkNotNull(taskType, "taskType");
        checkNotNull(subject,"subject");
        checkNotNull(classGroup,"classGroup");
        checkNotNull(evaluation, "evaluation");

        if (taskRepository.findTaskByTaskNameAndSubjectAndTaskDateAndClassGroupAndEvaluation(taskName,
                subject.getSubjectId(), taskDate, classGroup.getClassGroupId(), evaluation) != null)
            throw new DuplicateTaskException(taskName,"taskName");
        Task newTask = Task.builder().taskName(taskName).taskDate(taskDate).taskType(taskType).subject(subject)
                .classGroup(classGroup).evaluation(evaluation).build();
        return taskRepository.save(newTask);
    }

    @Transactional
    @Override
    public Page<Task> findTaskByClassGroup(Pageable page, Long classGroupId) {
        checkNotNull(page, "page");
        checkNotNull(classGroupId, "classGroup");

        return taskRepository.findTaskByClassGroup(page, classGroupId);
    }

    @Transactional
    @Override
    public Page<Task> findPendingTaskByClassGroup(Pageable page, Long classGroupId) {
        checkNotNull(page, "page");
        checkNotNull(classGroupId, "classGroup");

        return taskRepository.findPendingTaskByClassGroup(page, classGroupId);
    }

    @Transactional
    @Override
    public Page<Task> findOldTaskByClassGroup(Pageable page, Long classGroupId) {
        checkNotNull(page, "page");
        checkNotNull(classGroupId, "classGroup");

        return taskRepository.findOldTaskByClassGroup(page, classGroupId);
    }


    @Transactional
    @Override
    public Page<Task> findExams(Pageable page, Long classGroupId) {
        checkNotNull(page, "page");
        checkNotNull(classGroupId, "classGroup");
        return taskRepository.findExams(page, classGroupId);
    }

    @Transactional
    @Override
    public Page<Task> findProjectAndHomework(Pageable page, Long classGroupId) {
        checkNotNull(page, "page");
        checkNotNull(classGroupId, "classGroup");
        return taskRepository.findProjectAndHomework(page, classGroupId);
    }


    @Transactional
    @Override
    public Page<Task> findTaskByClassGroupAndSubject(Pageable page, Long classGroupId, Long subjectId) {
        checkNotNull(classGroupId, "classGroup");
        checkNotNull(subjectId, "subject");
        checkNotNull(page, "page");

        return taskRepository.findTaskByClassGroupAndSubject(page, classGroupId, subjectId);
    }

    @Transactional
    @Override
    public Page<Task> findOldTaskByClassGroupAndSubject(Pageable page, Long classGroupId, Long subjectId) {
        checkNotNull(classGroupId, "classGroup");
        checkNotNull(subjectId, "subject");
        checkNotNull(page, "page");

        return taskRepository.findOldTaskByClassGroupAndSubject(page, classGroupId,
                subjectId);
    }


    @Transactional
    @Override
    public Task updateTask(Long taskId, TaskCreateForm form) throws TaskNotFoundException {
        checkNotNull(taskId, "taskid");

        Task task = taskRepository.findTaskByTaskId(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId, "taskId");
        }
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(form.getTaskDate(), formatter);
            task.setSubject(form.getSubject());
            task.setClassGroup(form.getClassGroup());
            task.setEvaluation(form.getEvaluation());
            task.setTaskType(form.getTaskType());
            task.setTaskDate(date);
            task.setTaskName(form.getTaskName());
            return task;
        }
    }

    @Transactional
    @Override
    public void deleteTask(Long taskId) throws TaskNotFoundException{
        checkNotNull(taskId, "task");

        Task task = taskRepository.findTaskByTaskId(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId, "taskId");
        } else {
            taskRepository.delete(task);
        }
    }

    @Transactional
    @Override
    public List<StudentTaskGrade> findStudentTaskGradeByTask(Long taskId) throws TaskNotFoundException,
            ClassGroupNotFoundException {
        checkNotNull(taskId, "taskId");

        Task t = taskRepository.findTaskByTaskId(taskId);
        ClassGroup cg = t.getClassGroup();
        List<Student> studentList = studentService.findStudentByClassGroup(cg.getClassGroupId());
        List<Grade> gradeList = gradeRepository.findGradeByTask(t.getTaskId());
        List<StudentTaskGrade> studentTaskGradeList = new ArrayList<>();
        Iterator iter = studentList.iterator();
        Integer studentGrade;
        while (iter.hasNext()) {
            Student student = (Student)iter.next();
            studentGrade = -1;
            Iterator gradeIter = gradeList.iterator();
            while (gradeIter.hasNext()) {
                Grade grade = (Grade)gradeIter.next();
                if(student.equals(grade.getStudent())) {
                    studentGrade = grade.getGrade();
                }
            }
            StudentTaskGrade studentTaskGrade = new StudentTaskGrade(student.getStudentId(), student.getFirstName(),
                    student.getLastName(), t.getTaskId(), studentGrade.toString());
            studentTaskGradeList.add(studentTaskGrade);
        }
        return studentTaskGradeList;
    }

    /*
     * ViewTeacherTask Operations
     */

    @Transactional
    @Override
    public Page<ViewTeacherTask> findTeacherTask(Long teacherId) {
        checkNotNull(teacherId, "teacherId");
        return viewTeacherTaskRepository.findTaskByTeacher(new PageRequest(0,5), teacherId);

    }

    @Transactional
    @Override
    public Page<ViewTeacherTask> findTeacherExam(Long teacherId) {
        checkNotNull(teacherId, "teacherId");
        return viewTeacherTaskRepository.findTaskByTeacherAndTaskType(new PageRequest(0,5),
                teacherId, Task.TaskType.EXAM);
    }

    /*
     * Grade Operations
     */


    @Transactional
    @Override
    public Grade findGradebyId(Long gradeId) throws GradeNotFoundException {
        checkNotNull(gradeId, "grade");

        Grade g = gradeRepository.findGradeByGradeId(gradeId);
        if (g == null) {
            throw new GradeNotFoundException(gradeId, "gradeId not found");
        } else {
            return g;
        }
    }

    @Transactional
    @Override
    public Grade addGrade(Task task, Student student, Integer grade, String observation) throws DuplicateGradeException {
        checkNotNull(task, "task");
        checkNotNull(student, "student");
        checkNotNull(grade, "grade");
        checkNotNull(observation, "observation");

        if (gradeRepository.findGradeByTaskAndStudent(task.getTaskId(), student.getStudentId()) != null) {
            throw new DuplicateGradeException(task, "taskName");
        }
        Grade newGrade = Grade.builder().task(task).student(student).grade(grade).observation(observation).build();
        return gradeRepository.save(newGrade);
    }

    @Transactional
    @Override
    public List<Grade> findGradeByTask(Long taskId){
        checkNotNull(taskId, "task");

        return gradeRepository.findGradeByTask(taskId);
    }

    @Transactional
    @Override
    public Grade findGradeByTaskAndStudent(Long taskId, Long studentId) throws GradeNotFoundException {
        checkNotNull(taskId, "task");
        checkNotNull(studentId, "student");

        Grade gr = gradeRepository.findGradeByTaskAndStudent(taskId, studentId);
        if (gr == null) {
            throw new GradeNotFoundException(taskId,"grade");
        }
        else {
            return gr;
        }
    }

    @Transactional
    @Override
    public Page<Grade> findGradeByStudent(Pageable page, Long studentId){
        checkNotNull(page, "page");
        checkNotNull(studentId, "student");

        return gradeRepository.findGradeByStudent(page, studentId);
    }

    @Transactional
    @Override
    public Grade updateGrade(Long gradeId, Integer newGrade, String newObservation) throws GradeNotFoundException {
        checkNotNull(gradeId, "gradeId");
        checkNotNull(newGrade, "newGrade");
        checkNotNull(newObservation, "newObservation");

        Grade grade = gradeRepository.findGradeByGradeId(gradeId);
        if (grade == null) {
            throw new GradeNotFoundException(gradeId, "gradeId not found");
        } else {
            grade.setGrade(newGrade);
            grade.setObservation(newObservation);
            return grade;
        }
    }
}