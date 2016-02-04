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
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.course.GroupSubject;
import taboleiro.model.domain.student.GlobalGradeLevel;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.exception.*;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.user.UserService;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.course.SchoolYear;
import taboleiro.model.domain.student.GlobalGrade.Evaluation;
import java.time.LocalDate;
import java.time.Month;
import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TaboleiroApplication.class)
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
public class StudentServiceTest {

    @Autowired
    StudentService studentService;

    @Autowired
    CourseService courseService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    UserService userService;

    @Before
    public void setUpStudent() throws StudentNotFoundException, DuplicateStudentException, DuplicateCourseException,
            DuplicateClassGroupException, DuplicateSchoolYearException, DuplicateSubjectException,
            CourseNotFoundException, ClassGroupNotFoundException, DuplicateUserException,
            DuplicateGroupSubjectException, UserNotFoundException {

        User u = userService.createUser("picasso", "Pablo", "Picasso", "guernica", "", "", User.Role.USER);
        User t = userService.createUser("mcervantes", "Miguel", "De Cervantes", "manco", "", "", User.Role.TEACHER);

        SchoolYear sy1 = courseService.addSchoolYear("2019-2020");

        // AddCourses
        Course c1 = courseService.addCourse("1º ESO", Course.CourseLevel.SECUNDARIA);
        Course c2 = courseService.addCourse("2º ESO", Course.CourseLevel.SECUNDARIA);

        // AddSubjects to de courses
        subjectService.addSubject("Matematicas", c1.getCourseId(), "MA00012");
        subjectService.addSubject("Ingles", c1.getCourseId(), "MA00013");
        subjectService.addSubject("Galego", c1.getCourseId(), "MA00015");

        // Add ClassGroups
        ClassGroup cg1 = courseService.addClassGroup("1º ESO - A", c1.getCourseId(), t, sy1.getSchoolYearId(), "");
        ClassGroup cg2 = courseService.addClassGroup("1º ESO - B", c1.getCourseId(), t, sy1.getSchoolYearId(), "");
        ClassGroup cg3 = courseService.addClassGroup("2º ESO - A", c2.getCourseId(), t, sy1.getSchoolYearId(), "");

        // Add Students
        studentService.addStudent("Albert", "Einstein", "56302123T", LocalDate.of(2010, Month.FEBRUARY, 10),
                u.getUserId(), cg1.getClassGroupId());
        studentService.addStudent("Leonardo","Davinci","16101123A", LocalDate.of(2008, Month.JULY, 2), u.getUserId(),
                cg1.getClassGroupId());
        studentService.addStudent("Isaac", "Newton", "46101123D", LocalDate.of(2007, Month.DECEMBER, 25), u.getUserId(),
                cg3.getClassGroupId());
        studentService.addStudent("Charles", "Darwin", "11101123C", LocalDate.of(2009, Month.MAY, 5), u.getUserId(),
                cg2.getClassGroupId());
    }

    /*
     * Student Operations
     */

    @Test
    public void findStudentByIdTest() throws StudentNotFoundException {

        Student s = studentService.findStudentByDni("56302123T");
        Long studentId = s.getStudentId();
        Student s2 = studentService.findStudentById(studentId);
        assertThat(s2.getDni()).isEqualTo("56302123T");
    }

    @Test(expected = StudentNotFoundException.class)
    public void findStudentByIdNotFoundTest() throws StudentNotFoundException {
        studentService.findStudentById(2222L);
    }

    @Test
    public void listStudentTest() throws StudentNotFoundException {
        Page<Student> list = studentService.listStudent(new PageRequest(0, 10));
        assertThat(list).hasSize(4)
                .contains(studentService.findStudentByDni("56302123T"))
                .contains(studentService.findStudentByDni("16101123A"))
                .contains(studentService.findStudentByDni("46101123D"))
                .contains(studentService.findStudentByDni("11101123C"));
    }

    @Test
    public void findStudentByDniTest() throws StudentNotFoundException, DuplicateStudentException,
            DuplicateUserException, ClassGroupNotFoundException, UserNotFoundException {

        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º ESO - A");
        User u = userService.createUser("mondrian", "Piet", "Mondrian", "pintar", "", "", User.Role.USER);
        Student s1 = studentService.addStudent("Galileo", "Galilei", "21161123F", LocalDate.of(2009, Month.JANUARY, 28),
                u.getUserId(), cg.getClassGroupId());
        assertThat(studentService.findStudentByDni("21161123F")).isEqualTo(s1);
    }

    @Test (expected = DuplicateStudentException.class)
    public void addStudentDuplicatedTest() throws StudentNotFoundException, DuplicateStudentException,
            DuplicateUserException, ClassGroupNotFoundException, UserNotFoundException {
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º ESO - A");
        User u = userService.createUser("mondrian", "Piet", "Mondrian", "pintar", "", "", User.Role.USER);
        studentService.addStudent("Leonardo", "Davinci", "16101123A", LocalDate.of(2008, Month.JULY, 25), u.getUserId(),
                cg.getClassGroupId());
    }

    @Test
    public void updateStudentTest() throws StudentNotFoundException, DuplicateUserException,
            ClassGroupNotFoundException, UserNotFoundException {
        Student s = studentService.findStudentByDni("56302123T");
        User u = userService.createUser("jmiro", "Joan", "Miró", "pinturas", "", "", User.Role.USER);
        studentService.updateStudent(s.getStudentId(), "Alberto", s.getLastName(), s.getDni(), s.getBirthDate(),
                u.getUserId(), s.getCurrentClassGroup().getClassGroupId(), "002002202");
        assertThat(s.getFirstName()).isEqualTo("Alberto");
    }

    @Test
    public void setCourseTest() throws StudentNotFoundException, CourseNotFoundException {
        Student s = studentService.findStudentByDni("56302123T");
        Course c = courseService.findCourseByCourseName("1º ESO");
        studentService.updateCourse(s.getStudentId(), c.getCourseId());
        assertThat(studentService.findStudentByDni("56302123T").getCourse()).isEqualTo(c);
    }

    @Test
    public void findStudentByClassGroupTest() throws StudentNotFoundException, ClassGroupNotFoundException {
        ClassGroup cg = courseService.findClassGroupByClassGroupName("2º ESO - A");
        assertThat(studentService.findStudentByClassGroup(cg.getClassGroupId())).hasSize(1)
                .contains(studentService.findStudentByDni("46101123D"));
    }

    @Test
    public void findStudentByCourseTest() throws StudentNotFoundException, CourseNotFoundException {
        Course c = courseService.findCourseByCourseName("1º ESO");
        assertThat(studentService.findStudentByCourse(new PageRequest(0, 5), c.getCourseId())).hasSize(3)
                .contains(studentService.findStudentByDni("11101123C"))
                .contains(studentService.findStudentByDni("16101123A"));
    }

    @Test
    public void deleteStudentTest() throws StudentNotFoundException {
        Student student = studentService.findStudentByDni("11101123C");
        assertThat(studentService.listStudent(new PageRequest(0,5))).hasSize(4).contains(student);
        studentService.deleteStudent(student.getStudentId());
        assertThat(studentService.listStudent(new PageRequest(0, 5))).hasSize(3)
                .contains(studentService.findStudentByDni("56302123T"))
                .contains(studentService.findStudentByDni("16101123A"))
                .contains(studentService.findStudentByDni("46101123D"));
    }

    @Test
    public void findByGuardianTest() throws DuplicateStudentException, UserNotFoundException,
            StudentNotFoundException {
        Student s1 = studentService.findStudentByDni("56302123T");
        Student s2 = studentService.findStudentByDni("46101123D");
        User u = userService.findUserByLoginName("picasso");
        assertThat(studentService.findStudentByGuardian(u.getUserId())).contains(s2).contains(s1).hasSize(4);
    }

    /*
     * GlobalGrade Operations
     */

    @Test
    public void findGlobalGradeTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {
        Student s1 = studentService.findStudentByDni("56302123T");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        Subject subject2 = subjectService.findSubjectBySubjectNameAndCourseName("Ingles", "1º ESO");
        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl2 = studentService.addGlobalGradeLevel("7","Notable", "c-7", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl2, Evaluation.FIRST, "Bien, sigue así");
        GlobalGrade gg = studentService.addGlobalGrade(s1.getStudentId(), subject2, sy1, ggl, Evaluation.SECOND, "");
        assertThat(studentService.findGlobalGrade(s1.getStudentId(), subject2.getSubjectId(), sy1.getSchoolYearId(),
                Evaluation.SECOND)).isEqualTo(gg);
    }

    @Test (expected = GlobalGradeNotFoundException.class)
    public void findGlobalGradeNotFoundTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {
        Student s1 = studentService.findStudentByDni("56302123T");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        Subject subject2 = subjectService.findSubjectBySubjectNameAndCourseName("Ingles", "1º ESO");
        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl, Evaluation.FIRST, "Bien");
        studentService.findGlobalGrade(s1.getStudentId(), subject2.getSubjectId(), sy1.getSchoolYearId(),
                Evaluation.SECOND);

    }

    @Test (expected = DuplicateGlobalGradeLevelException.class)
    public void addDuplicateGlobalGradeLevelTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);

    }


    @Test
    public void findBySubjectAndSchoolYearTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        Student s1 = studentService.findStudentByDni("56302123T");
        Student s2 = studentService.findStudentByDni("46101123D");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        Subject subject3 = subjectService.findSubjectBySubjectNameAndCourseName("Galego", "1º ESO");
        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl2 = studentService.addGlobalGradeLevel("7","Notable", "c-7", Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl3 = studentService.addGlobalGradeLevel("10","Sobresaliente", "c-10",
                Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl2, Evaluation.FIRST, "Bien");
        studentService.addGlobalGrade(s1.getStudentId(), subject3, sy1, ggl3, Evaluation.FIRST, "Muy bien, sigue así");
        studentService.addGlobalGrade(s2.getStudentId(), subject3, sy1, ggl, Evaluation.FIRST, "");
        assertThat(studentService.listGlobalGrades(new PageRequest(0,5))).hasSize(3);
        assertThat(studentService.findGlobalGradeBySubjectAndSchoolYear(new PageRequest(0,5), subject1.getSubjectId(),
                sy1.getSchoolYearId())).hasSize(1);
        assertThat(studentService.findGlobalGradeBySubjectAndSchoolYear(new PageRequest(0,5), subject3.getSubjectId(),
                sy1.getSchoolYearId())).hasSize(2);
    }

    @Test
    public void listGlobalGradesTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        Student s1 = studentService.findStudentByDni("56302123T");
        Student s2 = studentService.findStudentByDni("46101123D");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        Subject subject2 = subjectService.findSubjectBySubjectNameAndCourseName("Ingles","1º ESO");
        Subject subject3 = subjectService.findSubjectBySubjectNameAndCourseName("Galego", "1º ESO");
        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl2 = studentService.addGlobalGradeLevel("7","Notable", "c-7", Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl3 = studentService.addGlobalGradeLevel("10","Sobresaliente",
                "c-10", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl2, Evaluation.FIRST, "Bien");
        studentService.addGlobalGrade(s1.getStudentId(), subject3, sy1, ggl3, Evaluation.FIRST, "Bien");
        studentService.addGlobalGrade(s2.getStudentId(), subject3, sy1, ggl, Evaluation.FIRST, "No se esfuerza");
        studentService.addGlobalGrade(s2.getStudentId(), subject2, sy1, ggl3, Evaluation.FIRST, "Bien");
        studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl, Evaluation.SECOND, "Se merecía menos");
        studentService.addGlobalGrade(s1.getStudentId(), subject3, sy1, ggl, Evaluation.SECOND, "Bien");
        studentService.addGlobalGrade(s2.getStudentId(), subject3, sy1, ggl, Evaluation.SECOND, "Muy mal");
        studentService.addGlobalGrade(s2.getStudentId(), subject2, sy1, ggl3, Evaluation.SECOND, "Bien");
        assertThat(studentService.listGlobalGrades(new PageRequest(0, 10))).hasSize(8);
    }

    @Test
    public void findByGlobalGradeIdTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        Student s1 = studentService.findStudentByDni("56302123T");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        GlobalGrade gg = studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl, Evaluation.FIRST, "Mal");
        assertThat(studentService.findGlobalGradeById(gg.getGlobalGradeId()).getGrade()).isEqualTo(ggl);

    }

    @Test
    public void updateGradeTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        Student s1 = studentService.findStudentByDni("56302123T");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl2 = studentService.addGlobalGradeLevel("7", "Notable", "c-7",
                Course.CourseLevel.SECUNDARIA);
        GlobalGrade gg = studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl, Evaluation.FIRST, "Mal");
        assertThat(studentService.findGlobalGrade(s1.getStudentId(), subject1.getSubjectId(), sy1.getSchoolYearId(),
                Evaluation.FIRST).getGrade()).isEqualTo(ggl);
        studentService.updateGlobalGrade(gg.getGlobalGradeId(), ggl2);
        assertThat(studentService.findGlobalGrade(s1.getStudentId(), subject1.getSubjectId(), sy1.getSchoolYearId(),
                Evaluation.FIRST).getGrade()).isEqualTo(ggl2);
    }

    @Test (expected = GlobalGradeNotFoundException.class)
    public void updateGradeNotFoundTest() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        studentService.updateGlobalGrade(12L, ggl);
    }

    @Test
    public void findGlobalGradeByStudent() throws StudentNotFoundException, SchoolYearNotFoundException,
            CourseNotFoundException, SubjectNotFoundException, DuplicateGlobalGradeException,
            GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        Student s1 = studentService.findStudentByDni("56302123T");
        Student s2 = studentService.findStudentByDni("46101123D");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        Subject subject3 = subjectService.findSubjectBySubjectNameAndCourseName("Galego", "1º ESO");

        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl2 = studentService.addGlobalGradeLevel("7","Notable", "c-7", Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl3 = studentService.addGlobalGradeLevel("10","Sobresaliente", "c-10",
                Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl, Evaluation.FIRST, "");
        studentService.addGlobalGrade(s1.getStudentId(), subject3, sy1, ggl3, Evaluation.FIRST, "");
        studentService.addGlobalGrade(s2.getStudentId(), subject3, sy1, ggl2, Evaluation.FIRST, "");
        assertThat(studentService.listGlobalGrades(new PageRequest(0,5))).hasSize(3);
        assertThat(studentService.findGlobalGradeByStudentAndSchoolYear(s1.getStudentId(), sy1.getSchoolYearId()))
                .hasSize(2);
        assertThat(studentService.findGlobalGradeByStudentAndSchoolYear(s2.getStudentId(), sy1.getSchoolYearId()))
                .hasSize(1);
    }

    @Test
    public void findGlobalGradeByClassGroupAndEvaluataionTest() throws StudentNotFoundException,
            SchoolYearNotFoundException, CourseNotFoundException, SubjectNotFoundException,
            DuplicateGlobalGradeException, GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException,
            ClassGroupNotFoundException, DuplicateGroupSubjectException, GroupSubjectNotFoundException {

        Student s1 = studentService.findStudentByDni("56302123T");
        Student s2 = studentService.findStudentByDni("46101123D");
        SchoolYear sy1 = courseService.findSchoolYearBySchoolYearName("2019-2020");
        Subject subject1 = subjectService.findSubjectBySubjectNameAndCourseName("Matematicas", "1º ESO");
        Subject subject3 = subjectService.findSubjectBySubjectNameAndCourseName("Galego", "1º ESO");
        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º ESO - A");
        User t = cg.getTutor();
        courseService.addGroupSubject(cg, subject1, t);
        GroupSubject groupSubject = courseService.addGroupSubject(cg, subject3, t);
        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("5", "Suficiente", "c-5",
                Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl2 = studentService.addGlobalGradeLevel("7","Notable", "c-7",
                Course.CourseLevel.SECUNDARIA);
        GlobalGradeLevel ggl3 = studentService.addGlobalGradeLevel("10","Sobresaliente", "c-10",
                Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGrade(s1.getStudentId(), subject1, sy1, ggl, Evaluation.FIRST, "");
        studentService.addGlobalGrade(s1.getStudentId(), subject3, sy1, ggl3, Evaluation.FIRST, "");
        studentService.addGlobalGrade(s2.getStudentId(), subject3, sy1, ggl2, Evaluation.FIRST, "");
        studentService.addGlobalGrade(s2.getStudentId(), subject3, sy1, ggl2, Evaluation.SECOND, "");
        studentService.addGlobalGrade(s2.getStudentId(), subject3, sy1, ggl2, Evaluation.THIRD, "");
        assertThat(studentService.findGlobalGradeByGroupSubjectAndEvaluation(groupSubject.getGroupSubjectId(),
                Evaluation.FIRST)).hasSize(2);
    }

    /*
     * GlobalGradeLevel Operations
     */

    @Test
    public void findGlobalGradeLevelByCourseLevelTest() throws DuplicateGlobalGradeLevelException {

        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("3","Insuficiente", "c-3",
                Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("5","Suficiente", "c-5", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("7","Notable", "c-7", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("10","Sobresaliente", "c-10", Course.CourseLevel.SECUNDARIA);
        assertThat(studentService.findGlobalGradeLevelByCourseLevel(Course.CourseLevel.SECUNDARIA)).hasSize(4);
        assertThat(studentService.findGlobalGradeLevelByCourseLevel(Course.CourseLevel.SECUNDARIA)).contains(ggl);
    }

    @Test
    public void findGlobalGradeByIdTest() throws GlobalGradeNotFoundException, DuplicateGlobalGradeLevelException {

        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("3","Insuficiente", "c-3",
                Course.CourseLevel.SECUNDARIA);
        assertThat(studentService.findGlobalGradeLevelById(ggl.getGlobalGradeLevelId())).isEqualTo(ggl);
    }

    @Test(expected = DuplicateGlobalGradeLevelException.class)
    public void findGlobalGradeLevelByCourseLevelTestDuplicate() throws DuplicateGlobalGradeLevelException {

        studentService.addGlobalGradeLevel("5", "Suficiente", "c-5", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("5", "Suficiente", "c-5", Course.CourseLevel.SECUNDARIA);

    }

    @Test
    public void findAllGlobalGradeLevelTest() throws DuplicateGlobalGradeLevelException {

        studentService.addGlobalGradeLevel("3","Insuficiente", "c-3", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("5","Suficiente", "c-5", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("7","Notable", "c-7", Course.CourseLevel.SECUNDARIA);
        assertThat(studentService.findAllGlobalGradeLevel(new PageRequest(0,5))).hasSize(3);
    }

    @Test
    public void deleteGlobalGradeLevelTest()
            throws GlobalGradeLevelNotFoundException, DuplicateGlobalGradeLevelException {

        GlobalGradeLevel ggl = studentService.addGlobalGradeLevel("3","Insuficiente", "c-3", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("5","Suficiente", "c-5", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("7","Notable", "c-7", Course.CourseLevel.SECUNDARIA);
        studentService.addGlobalGradeLevel("10","Sobresaliente", "c-10", Course.CourseLevel.SECUNDARIA);
        assertThat(studentService.findGlobalGradeLevelByCourseLevel(Course.CourseLevel.SECUNDARIA)).hasSize(4);
        studentService.deleteGlobalGradeLevel(ggl.getGlobalGradeLevelId());
        assertThat(studentService.findGlobalGradeLevelByCourseLevel(Course.CourseLevel.SECUNDARIA)).hasSize(3);
    }
}