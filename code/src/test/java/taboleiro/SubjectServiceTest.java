package taboleiro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Before;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import javax.transaction.Transactional;
import taboleiro.controller.subject.SubjectCreateForm;
import taboleiro.controller.subject.TaskCreateForm;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.subject.*;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.*;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.user.UserService;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TaboleiroApplication.class)
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
public class SubjectServiceTest {

    @Autowired
    SubjectService subjectService;

    @Autowired
    CourseService courseService;

    @Autowired
    StudentService studentService;

    @Autowired
    UserService userService;

    @Before
    public void setUpSubject() throws DuplicateClassGroupException, DuplicateCourseException, DuplicateUserException ,
            DuplicateSchoolYearException, ClassGroupNotFoundException, UserNotFoundException, DuplicateSubjectException,
            DuplicateAttendanceException, DuplicateStudentException, DuplicateTaskException, DuplicateGradeException,
            CourseNotFoundException {

        Course c1 = courseService.addCourse("1º EP", Course.CourseLevel.PRIMARIA);
        courseService.addCourse("2º EP", Course.CourseLevel.PRIMARIA);
        courseService.addCourse("1º ESO", Course.CourseLevel.SECUNDARIA);
        courseService.addCourse("2º ESO", Course.CourseLevel.SECUNDARIA);
        Course c2 = courseService.addCourse("4º ESO", Course.CourseLevel.SECUNDARIA);

        courseService.addSchoolYear("2013-2014");
        SchoolYear sy = courseService.addSchoolYear("2014-2015");
        courseService.addSchoolYear("2015-2016");

        User u = userService.createUser("mcervantes", "Miguel", "De Cervantes", "manco", "", "", User.Role.USER);
        User u2 = userService.createUser("ppicasso", "Pablo", "Picasso", "guernica", "", "", User.Role.USER);

        ClassGroup cg = courseService.addClassGroup("1º EP A", c1.getCourseId(), u, sy.getSchoolYearId(), "");
        ClassGroup cg2 = courseService.addClassGroup("1º EP B", c1.getCourseId(), u, sy.getSchoolYearId(), "");
        courseService.addClassGroup("4º ESO Ciencias", c2.getCourseId(), u, sy.getSchoolYearId(), "");
        courseService.addClassGroup("4º ESO Letras", c2.getCourseId(), u, sy.getSchoolYearId(), "");

        subjectService.addSubject("Matemáticas", c2.getCourseId(), "MA11111");
        subjectService.addSubject("Tecnología", c2.getCourseId(), "MA11112");
        subjectService.addSubject("Inglés", c2.getCourseId(), "MA111113");
        subjectService.addSubject("Lingua Galega",c2.getCourseId(), "MA111114");
        Subject s1 = subjectService.addSubject("Matemáticas", c1.getCourseId(), "MA11115");
        Subject s2 = subjectService.addSubject("Inglés",c1.getCourseId(), "MA11116");
        Subject s3 = subjectService.addSubject("Lingua Galega",c1.getCourseId(), "Ma11117");
        Student student1 = studentService.addStudent("Charles", "Darwin", "11101123C", LocalDate.of(2009, Month.MAY, 5),
                u2.getUserId(), cg2.getClassGroupId());
        Student student2 = studentService.addStudent("Isaac", "Newton", "46101123D", LocalDate.of(2007, Month.DECEMBER,
                        25),
                u2.getUserId(), cg.getClassGroupId());
        Student student3 = studentService.addStudent("John", "Newton", "46101113D", LocalDate.of(2009, Month.DECEMBER,
                        25),
                u2.getUserId(), cg.getClassGroupId());

        subjectService.addAttendance(student1, s1, LocalDate.of(2015, Month.FEBRUARY, 27), cg2,
                Attendance.FaultType.ATTENDANCE);
        subjectService.addAttendance(student1, s2, LocalDate.of(2015, Month.MAY, 8), cg,
                Attendance.FaultType.PUNCTUALITY);
        subjectService.addAttendance(student2, s2, LocalDate.of(2015, Month.JANUARY, 8), cg,
                Attendance.FaultType.PUNCTUALITY);
        subjectService.addAttendance(student2, s2, LocalDate.of(2015, Month.MARCH, 8), cg,
                Attendance.FaultType.PUNCTUALITY);
        subjectService.addAttendance(student2, s1, LocalDate.of(2015, Month.MARCH, 15), cg,
                Attendance.FaultType.PUNCTUALITY);
        subjectService.addAttendance(student2, s2, LocalDate.of(2015, Month.JUNE, 15), cg,
                Attendance.FaultType.PUNCTUALITY);

        subjectService.addTask("Examen Final: Matrices", LocalDate.of(2014, Month.DECEMBER, 8), s1, cg,
                GlobalGrade.Evaluation.FIRST, Task.TaskType.EXAM);
        subjectService.addTask("Examen Final: Derivadas", LocalDate.of(2015, Month.FEBRUARY, 20), s1, cg,
                GlobalGrade.Evaluation.SECOND, Task.TaskType.EXAM);
        Task t = subjectService.addTask("Examen Final: Integrales", LocalDate.of(2015, Month.MAY, 18),s1,cg,
                GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        subjectService.addGrade(t, student2,5,"");
        subjectService.addGrade(t, student3,5,"");
    }

    /*
     *  Subject Operations
     */

    @Test
    public void findSubjectById() throws DuplicateSubjectException, DuplicateCourseException, SubjectNotFoundException,
            CourseNotFoundException{
        Course c = courseService.addCourse("1º Bach", Course.CourseLevel.SECUNDARIA);
        Subject s = subjectService.addSubject("Física y química", c.getCourseId(), "MA22221");
        Long subjectId = s.getSubjectId();
        assertThat(subjectService.findSubjectById(subjectId)).isEqualTo(s);
    }

    @Test
    public void listSubjectTest() {
        Page<Subject> list = subjectService.listSubject(new PageRequest(0,10));
        assertThat(list).hasSize(7);
    }

    @Test
    public void addSubjectTest() throws DuplicateSubjectException, DuplicateCourseException, CourseNotFoundException {
        Course c = courseService.addCourse("1º Bach", Course.CourseLevel.SECUNDARIA);
        Subject s = subjectService.addSubject("Física y química", c.getCourseId(), "MA22222");
        Page<Subject> list = subjectService.listSubject(new PageRequest(0, 10));
        assertThat(list).hasSize(8).contains(s);
    }

    @Test(expected = DuplicateSubjectException.class)
    public void addDuplicateSubjectTest() throws DuplicateSubjectException, DuplicateCourseException,
            CourseNotFoundException {
        Course c = courseService.addCourse("1º Bach", Course.CourseLevel.SECUNDARIA);
        subjectService.addSubject("Física y química", c.getCourseId(), "MA22221");
        subjectService.addSubject("Física y química", c.getCourseId(), "MA22221");
    }

    @Test
    public void findSubjectBySubjectNameAndCourseTest() throws SubjectNotFoundException, DuplicateSubjectException,
            DuplicateCourseException, CourseNotFoundException {
        Course c = courseService.addCourse("2º Bach", Course.CourseLevel.SECUNDARIA);
        Subject s = subjectService.addSubject("Física y química", c.getCourseId(), "MA22221");
        assertThat(subjectService.findSubjectBySubjectNameAndCourseName("Física y química", "2º Bach")).isEqualTo(s);
    }

    @Test(expected = SubjectNotFoundException.class)
    public void findSubjectBySubjectNameAndCourseNotFoundTest() throws SubjectNotFoundException,
            DuplicateSubjectException,
            DuplicateCourseException, CourseNotFoundException {
        subjectService.findSubjectBySubjectNameAndCourseName("Latín", "4º ESO");
    }

    @Test(expected = SubjectNotFoundException.class)
    public void findSubjectBySubjectNameAndCourseNotFound2Test() throws SubjectNotFoundException,
            DuplicateSubjectException,
            DuplicateCourseException, CourseNotFoundException {
        subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º ESO");
    }

    @Test
    public void deleteSubjectTest() throws SubjectNotFoundException, CourseNotFoundException {
        assertThat(subjectService.listSubject(new PageRequest(0,10))).hasSize(7);
        subjectService.deleteSubject(
                subjectService.findSubjectBySubjectNameAndCourseName("Lingua Galega", "4º ESO").getSubjectId());
        assertThat(subjectService.listSubject(new PageRequest(0,10))).hasSize(6);
    }

    @Test
    public void updateSubjectTest() throws SubjectNotFoundException, CourseNotFoundException {
        assertThat(subjectService.listSubject(new PageRequest(0,10))).hasSize(7);
        Subject s = subjectService.findSubjectBySubjectNameAndCourseName("Lingua Galega", "4º ESO");
        SubjectCreateForm scf = SubjectCreateForm.builder().codigoMateriaAvaliable(s.getCodigoMateriaAvaliable())
                .course(s.getCourse()).subjectName("Lengua Gallega").build();
        subjectService.updateSubject(s.getSubjectId(), scf);
        assertThat(subjectService.findSubjectBySubjectNameAndCourseName("Lengua Gallega", "4º ESO"))
                .isEqualTo(s);
    }

    @Test
    public void findSubjectByCourseTest() throws CourseNotFoundException, SubjectNotFoundException {
        Course course = courseService.findCourseByCourseName("4º ESO");
        Subject s = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        assertThat(subjectService.findSubjectByCourse(course.getCourseId())).contains(s);
    }

    /*
     * Attendance Operations
     */

    @Test
    public void findAttendanceTest() throws AttendanceNotFoundException, StudentNotFoundException,
            SubjectNotFoundException, CourseNotFoundException {

        Student st1 = studentService.findStudentByDni("11101123C");
        Student st2 = studentService.findStudentByDni("46101123D");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Subject sb2 = subjectService.findSubjectBySubjectNameAndCourseName("Inglés", "1º EP");
        Attendance att = subjectService.findAttendance(st1.getStudentId(),sb1.getSubjectId(),
                LocalDate.of(2015, Month.FEBRUARY, 27));
        assertThat(att.getStudent()).isEqualTo(st1);
        assertThat(att.getSubject()).isEqualTo(sb1);
        assertThat(att.getFaultDate()).isEqualTo(LocalDate.of(2015, Month.FEBRUARY, 27));
        Attendance att2 = subjectService.findAttendance(st2.getStudentId(), sb2.getSubjectId(),
                LocalDate.of(2015, Month.MARCH, 8));
        assertThat(att2.getStudent()).isEqualTo(st2);
    }

    @Test
    public void findAttendanceById() throws AttendanceNotFoundException, StudentNotFoundException,
            SubjectNotFoundException, ClassGroupNotFoundException, DuplicateAttendanceException,
            CourseNotFoundException {

        Student st1 = studentService.findStudentByDni("11101123C");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP B");
        subjectService.addAttendance(st1, sb1, LocalDate.of(2015, Month.FEBRUARY, 10), cg,
                Attendance.FaultType.PUNCTUALITY);
        Attendance att = subjectService.findAttendance(st1.getStudentId(), sb1.getSubjectId(),
                LocalDate.of(2015, Month.FEBRUARY, 27));
        assertThat(subjectService.findAttendanceById(att.getAttendanceId())).isEqualTo(att);
    }

    @Test
    public void findAttendanceByStudent() throws StudentNotFoundException {
        Student st = studentService.findStudentByDni("46101123D");
        Page attList = subjectService.findAttendanceByStudent(new PageRequest(0, 5), st.getStudentId());
        assertThat(attList).hasSize(4);
    }

    @Test
    public void updateJustifiedTest() throws AttendanceNotFoundException, StudentNotFoundException,
            SubjectNotFoundException, CourseNotFoundException, DuplicateAttendanceException {
        Student st1 = studentService.findStudentByDni("11101123C");

        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Attendance att = subjectService.addAttendance(st1, sb1, LocalDate.of(2019, Month.JUNE, 15),
                st1.getCurrentClassGroup(), Attendance.FaultType.PUNCTUALITY);
        subjectService.updateJustified(att.getAttendanceId());
        assertThat(subjectService.findAttendanceById(att.getAttendanceId()).isJustified());
    }

    @Test
    public void findAttendanceByClassGroupTest() throws ClassGroupNotFoundException {
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP B");
        assertThat(subjectService.findAttendanceByClassGroup(new PageRequest(0, 10), cg.getClassGroupId())).hasSize(1);
    }

    @Test
    public void deleteAttendanceTest() throws AttendanceNotFoundException, StudentNotFoundException,
            SubjectNotFoundException, CourseNotFoundException {

        Student st1 = studentService.findStudentByDni("11101123C");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Attendance att = subjectService.findAttendance(st1.getStudentId(), sb1.getSubjectId(),
                LocalDate.of(2015, Month.FEBRUARY, 27));
        assertThat(subjectService.listAllAttendance(new PageRequest(0,10))).hasSize(6).contains(att);
        subjectService.deleteAttendance(att.getAttendanceId());
        assertThat(subjectService.listAllAttendance(new PageRequest(0,10))).hasSize(5);

    }

    @Test
    public void notJustifiedAttendanceTest() throws AttendanceNotFoundException, StudentNotFoundException,
    SubjectNotFoundException, CourseNotFoundException, DuplicateAttendanceException {
        Student st1 = studentService.findStudentByDni("11101123C");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Attendance att = subjectService.addAttendance(st1, sb1, LocalDate.of(2019, Month.JUNE, 15),
                st1.getCurrentClassGroup(), Attendance.FaultType.PUNCTUALITY);
        assertThat(subjectService.findNotJustifiedAttendanceByStudent(new PageRequest(0, 20), st1.getStudentId()))
                .contains(att);
    }

    @Test
    public void justifiedAttendanceTest() throws AttendanceNotFoundException, StudentNotFoundException,
            SubjectNotFoundException, CourseNotFoundException, DuplicateAttendanceException {
        Student st1 = studentService.findStudentByDni("11101123C");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Attendance att = subjectService.addAttendance(st1, sb1, LocalDate.of(2019, Month.JUNE, 15),
                st1.getCurrentClassGroup(), Attendance.FaultType.PUNCTUALITY);
        subjectService.updateJustified(att.getAttendanceId());
        assertThat(subjectService.findJustifiedAttendanceByStudent(new PageRequest(0, 20), st1.getStudentId()))
                .contains(att);
    }

    /*
     * Task Operations
     */

    @Test
    public void addTaskTest() throws DuplicateTaskException, TaskNotFoundException, ClassGroupNotFoundException,
            SubjectNotFoundException, CourseNotFoundException {
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
    }

    @Test
    public void findTaskTest() throws DuplicateTaskException, TaskNotFoundException, ClassGroupNotFoundException,
            SubjectNotFoundException, CourseNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Task t = subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        assertThat(subjectService.findTask("Examen Recuperacion: Ev.1", sb1.getSubjectId(),
                LocalDate.of(2015, Month.MAY, 25), cg.getClassGroupId(), GlobalGrade.Evaluation.THIRD)).isEqualTo(t);
    }

    @Test
    public void findTaskByClassGroupTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException, SubjectNotFoundException, CourseNotFoundException {
        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        ClassGroup cg2 = courseService.findClassGroupByClassGroupName("4º ESO Letras");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        Subject sb2 = subjectService.findSubjectBySubjectNameAndCourseName("Lingua Galega", "4º ESO");
        Subject sb3 = subjectService.findSubjectBySubjectNameAndCourseName("Inglés", "4º ESO");
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        Task t2 = subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb2, cg2, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        Task t3 = subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb3, cg2, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        assertThat(subjectService.findTaskByClassGroup(new PageRequest(0, 10), cg2.getClassGroupId()))
                .hasSize(2).contains(t2).contains(t3);
    }

    @Test
    public void findTaskByClassGroupAndSubjectTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException, SubjectNotFoundException, CourseNotFoundException {
        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        ClassGroup cg2 = courseService.findClassGroupByClassGroupName("4º ESO Letras");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        Subject sb2 = subjectService.findSubjectBySubjectNameAndCourseName("Lingua Galega", "4º ESO");
        Subject sb3 = subjectService.findSubjectBySubjectNameAndCourseName("Inglés", "4º ESO");
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb2, cg2, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        Task t3 = subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb3, cg2, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        assertThat(subjectService.findTaskByClassGroupAndSubject(new PageRequest(0,20),cg2.getClassGroupId(),
                sb3.getSubjectId())).hasSize(1).contains(t3);
    }

    @Test
    public void updateTaskTest() throws DuplicateTaskException, TaskNotFoundException, ClassGroupNotFoundException,
            SubjectNotFoundException, CourseNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        Task t = subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        assertThat(t.getTaskDate()).isEqualTo(LocalDate.of(2015, Month.MAY, 25));
        TaskCreateForm form = TaskCreateForm.builder().classGroup(cg).subject(sb1)
                .evaluation(GlobalGrade.Evaluation.SECOND).taskType(Task.TaskType.HOMEWORK)
                .taskDate("05-02-2015").build();
        subjectService.updateTask(t.getTaskId(), form);
        assertThat(subjectService.findTaskById(t.getTaskId()).getEvaluation()).isEqualTo(GlobalGrade.Evaluation.SECOND);
    }

    @Test
    public void deleteTaskDateTest() throws DuplicateTaskException, TaskNotFoundException, ClassGroupNotFoundException,
            SubjectNotFoundException, CourseNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        Task t = subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2015, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        assertThat(subjectService.findTaskByClassGroupAndSubject(new PageRequest(0,20), cg.getClassGroupId(),
                sb1.getSubjectId())).hasSize(1);
        subjectService.deleteTask(t.getTaskId());
        assertThat(subjectService.findTaskByClassGroupAndSubject(new PageRequest(0,20), cg.getClassGroupId(),
                sb1.getSubjectId())).hasSize(0);
    }

    @Test
    public void findPendingTaskByClassGroupTest() throws DuplicateTaskException, TaskNotFoundException,
    ClassGroupNotFoundException,  SubjectNotFoundException, CourseNotFoundException {
        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2020, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2010, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        assertThat(subjectService.findPendingTaskByClassGroup(new PageRequest(0,20), cg.getClassGroupId())).hasSize(1);
    }

    @Test
    public void findOldTaskByClassGroupTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException,  SubjectNotFoundException, CourseNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2010, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        assertThat(subjectService.findOldTaskByClassGroup(new PageRequest(0, 20), cg.getClassGroupId())).hasSize(1);
    }

    @Test
    public void findExamsByClassGroupTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException,  SubjectNotFoundException, CourseNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2020, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2020, Month.JANUARY, 25),
                sb1, cg, GlobalGrade.Evaluation.SECOND, Task.TaskType.EXAM);
        assertThat(subjectService.findExams(new PageRequest(0, 20), cg.getClassGroupId())).hasSize(2);
    }

    @Test
    public void findProjectAndHomeworkByClassGroupTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException,  SubjectNotFoundException, CourseNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("4º ESO Ciencias");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "4º ESO");
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2010, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.EXAM);
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2010, Month.JANUARY, 25),
                sb1, cg, GlobalGrade.Evaluation.SECOND, Task.TaskType.EXAM);
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2020, Month.MAY, 25),
                sb1, cg, GlobalGrade.Evaluation.THIRD, Task.TaskType.HOMEWORK);
        subjectService.addTask("Examen Recuperacion: Ev.1", LocalDate.of(2020, Month.FEBRUARY, 15),
                sb1, cg, GlobalGrade.Evaluation.SECOND, Task.TaskType.PROJECT);
        assertThat(subjectService.findProjectAndHomework(new PageRequest(0, 20), cg.getClassGroupId())).hasSize(2);
    }

    /*
     * Grade Operations
     */

    @Test
    public void findGradeByIdTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException, SubjectNotFoundException, StudentNotFoundException,
            DuplicateGradeException, CourseNotFoundException, GradeNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Task t = subjectService.findTask("Examen Final: Matrices", sb1.getSubjectId(),
                LocalDate.of(2014, Month.DECEMBER, 8), cg.getClassGroupId(), GlobalGrade.Evaluation.FIRST);
        Student student1 = studentService.findStudentByDni("11101123C");
        Grade g1 = subjectService.addGrade(t, student1, 6, "Muchas faltas de ortografía");
        assertThat(subjectService.findGradebyId(g1.getGradeId()).getGrade()).isEqualTo(6);
    }

    @Test
    public void findGradesByTaskTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException, SubjectNotFoundException, StudentNotFoundException,
            DuplicateGradeException, CourseNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        Task t = subjectService.findTask("Examen Final: Matrices", sb1.getSubjectId(),
                LocalDate.of(2014, Month.DECEMBER, 8), cg.getClassGroupId(), GlobalGrade.Evaluation.FIRST);
        Task t2 = subjectService.findTask("Examen Final: Derivadas", sb1.getSubjectId(),
                LocalDate.of(2015, Month.FEBRUARY, 20), cg.getClassGroupId(), GlobalGrade.Evaluation.SECOND);
        Student student1 = studentService.findStudentByDni("11101123C");
        Student student2 = studentService.findStudentByDni("46101123D");
        Grade g1 = subjectService.addGrade(t, student1, 6, "Muchas faltas de ortografía");
        Grade g2 = subjectService.addGrade(t, student2, 8, "");
        subjectService.addGrade(t2, student2, 5, "");
        assertThat(subjectService.findGradeByTask(t.getTaskId())).hasSize(2).contains(g1).contains(g2);
    }

    @Test
    public void findGradeByTaskAndStudentTest() throws DuplicateTaskException, TaskNotFoundException,
    ClassGroupNotFoundException, SubjectNotFoundException, StudentNotFoundException,
    DuplicateGradeException, GradeNotFoundException, CourseNotFoundException {

        Student student1 = studentService.findStudentByDni("46101123D");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Task t = subjectService.findTask("Examen Final: Integrales", sb1.getSubjectId(),
                LocalDate.of(2015, Month.MAY, 18), cg.getClassGroupId(), GlobalGrade.Evaluation.THIRD);
        assertThat(subjectService.findGradeByTaskAndStudent(t.getTaskId(), student1.getStudentId()).getGrade())
                .isEqualTo(5);

    }

    @Test
    public void findGradeByStudentTest() throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException, SubjectNotFoundException, StudentNotFoundException,
            DuplicateGradeException, GradeNotFoundException, CourseNotFoundException {

        Student student1 = studentService.findStudentByDni("46101123D");
        Student student2 = studentService.findStudentByDni("46101113D");
        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Task t = subjectService.findTask("Examen Final: Matrices", sb1.getSubjectId(),
                LocalDate.of(2014, Month.DECEMBER, 8), cg.getClassGroupId(), GlobalGrade.Evaluation.FIRST);
        Task t2 = subjectService.findTask("Examen Final: Derivadas", sb1.getSubjectId(),
                LocalDate.of(2015, Month.FEBRUARY, 20), cg.getClassGroupId(), GlobalGrade.Evaluation.SECOND);
        Grade g = subjectService.addGrade(t, student1, 7, "");
        subjectService.addGrade(t, student2,9,"");
        Grade g2 = subjectService.addGrade(t2, student1, 4, "");
        assertThat(subjectService.findGradeByStudent(new PageRequest(0,10), student1.getStudentId())).contains(g)
                .contains(g2);

    }

    @Test
    public void updateGradeTest()  throws DuplicateTaskException, TaskNotFoundException,
            ClassGroupNotFoundException, SubjectNotFoundException, StudentNotFoundException,
            DuplicateGradeException, GradeNotFoundException, CourseNotFoundException {

        Subject sb1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Task t = subjectService.findTask("Examen Final: Integrales", sb1.getSubjectId(),
                LocalDate.of(2015, Month.MAY, 18), cg.getClassGroupId(), GlobalGrade.Evaluation.THIRD);
        Student student1 = studentService.findStudentByDni("46101113D");
        Grade g = subjectService.findGradeByTaskAndStudent(t.getTaskId(), student1.getStudentId());
        assertThat(g.getGrade()).isEqualTo(5);
        subjectService.updateGrade(g.getGradeId(), 10, "Nota actualizada. Habia corregido el examen mal, lo siento.");
        assertThat(subjectService.findGradeByTaskAndStudent(t.getTaskId(), student1.getStudentId())
                .getGrade()).isEqualTo(10);
    }

    @Test
    public void findStudentTaskGradeByTaskTest() throws TaskNotFoundException, ClassGroupNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        List<Task> taskList =  subjectService.findTaskByClassGroup(new PageRequest(0,20), cg.getClassGroupId())
                .getContent();
        List<StudentTaskGrade> stg = subjectService.findStudentTaskGradeByTask(taskList.get(0).getTaskId());
        assertThat(stg).hasSize(2);
    }

}
