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
import taboleiro.controller.subject.GroupSubjectCreateForm;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.exception.*;
import taboleiro.model.service.CourseService;
import taboleiro.model.domain.user.User;
import taboleiro.model.service.user.UserService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.SubjectService;
import taboleiro.controller.course.CourseCreateForm;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TaboleiroApplication.class)
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
public class CourseServiceTest {

    @Autowired
    CourseService courseService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    UserService userService;

    @Autowired
    StudentService studentService;

    @Before
    public void setUpCourse() throws DuplicateClassGroupException, DuplicateCourseException, DuplicateUserException,
            DuplicateSchoolYearException, ClassGroupNotFoundException, UserNotFoundException, DuplicateSubjectException,
            DuplicateScheduleException, DuplicateClassHourLevelException, StudentNotFoundException,
            DuplicateStudentException, DuplicateGroupSubjectException, CourseNotFoundException {

        // Courses PRIMARIA
        Course p1 = courseService.addCourse("1º EP", Course.CourseLevel.PRIMARIA);
        Course p2 = courseService.addCourse("2º EP", Course.CourseLevel.PRIMARIA);

        // Courses SECUNDARIA
        Course s1 = courseService.addCourse("1º ESO", Course.CourseLevel.SECUNDARIA);
        Course s2 = courseService.addCourse("2º ESO", Course.CourseLevel.SECUNDARIA);
        Course s4 = courseService.addCourse("4º ESO", Course.CourseLevel.SECUNDARIA);

        courseService.addSchoolYear("2013-2014");
        SchoolYear sy = courseService.addSchoolYear("2014-2015");
        courseService.addSchoolYear("2015-2016");
        User u = userService.createUser("mcervantes", "Miguel", "De Cervantes", "manco", "", "", User.Role.TEACHER);
        User u2 = userService.createUser("pheller", "Peter", "Heller", "dogconstellation", "", "", User.Role.TEACHER);
        User father = userService.createUser("picasso", "Pablo", "Picasso", "guernica", "", "", User.Role.USER);

        // ClassGroups PRIMARIA
        ClassGroup gp1a = courseService.addClassGroup("1º EP A", p1.getCourseId(), u, sy.getSchoolYearId(), "g00001");
        ClassGroup gp1b = courseService.addClassGroup("1º EP B", p1.getCourseId(), u, sy.getSchoolYearId(), "g00002");

        // ClassGroups SECUDARIA
        ClassGroup gs4c = courseService.addClassGroup("4º ESO Ciencias", s4.getCourseId(), u2, sy.getSchoolYearId(), "g00003");
        ClassGroup gs4l = courseService.addClassGroup("4º ESO Letras", s4.getCourseId(), u2, sy.getSchoolYearId(), "g00004");

        // Subjects PRIMARIA
        Subject sbp1 = subjectService.addSubject("Matemáticas", p1.getCourseId(), "MA001");
        Subject sbp2 = subjectService.addSubject("Inglés", p1.getCourseId(), "MA002");
        Subject sbp3 = subjectService.addSubject("Lingua Galega", p1.getCourseId(), "MA003");

        // Subjects SECUNDARIA
        Subject sbs1 = subjectService.addSubject("Matemáticas", s4.getCourseId(), "MA004");
        Subject sbs2 = subjectService.addSubject("Tecnología", s4.getCourseId(), "MA005");
        Subject sbs3 = subjectService.addSubject("Inglés", s4.getCourseId(), "MA006");
        Subject sbs4 = subjectService.addSubject("Lingua Galega",s4.getCourseId(), "MA007");

        ClassHourLevel cHPrimaria1 = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.FIRST_HOUR,
                "10:00", "11:00");
        ClassHourLevel cHPrimaria2 = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.SECOND_HOUR,
                "11:00", "12:00");
        ClassHourLevel cHPrimaria3 = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.THIRD_HOUR,
                "12:30", "13:30");
        ClassHourLevel cHPrimaria4 = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.FOURTH_HOUR,
                "15:30", "16:30");

        // SECUNDARIA Class Hour
        ClassHourLevel cHSecundaria1 = courseService.addClassHourLevel(Course.CourseLevel.SECUNDARIA,
                ClassHourLevel.ClassHour.FIRST_HOUR,
                "9, 00", "10, 00");
        ClassHourLevel cHSecundaria2 = courseService.addClassHourLevel(Course.CourseLevel.SECUNDARIA,
                ClassHourLevel.ClassHour.SECOND_HOUR,
                "10, 00", "10, 00");
        ClassHourLevel cHSecundaria3 = courseService.addClassHourLevel(Course.CourseLevel.SECUNDARIA,
                ClassHourLevel.ClassHour.THIRD_HOUR,
                "11, 30", "12, 30");
        ClassHourLevel cHSecundaria4 = courseService.addClassHourLevel(Course.CourseLevel.SECUNDARIA,
                ClassHourLevel.ClassHour.FOURTH_HOUR,
                "12, 30", "13, 30");

        courseService.addGroupSubject(gs4c, sbs1, u);
        courseService.addGroupSubject(gs4c, sbs2, u2);
        courseService.addGroupSubject(gs4c, sbs3, u2);
        courseService.addGroupSubject(gs4c, sbs4, u);

        // SECUNDARIA Schedule
        courseService.addSchedule(gs4c, sbs1, Schedule.WeekDay.MONDAY, cHSecundaria1);
        courseService.addSchedule(gs4c, sbs2, Schedule.WeekDay.MONDAY, cHSecundaria2);
        courseService.addSchedule(gs4c, sbs3, Schedule.WeekDay.MONDAY, cHSecundaria3);
        courseService.addSchedule(gs4c, sbs4, Schedule.WeekDay.WEDNESDAY, cHSecundaria1);
        courseService.addSchedule(gs4c, sbs4, Schedule.WeekDay.WEDNESDAY, cHSecundaria3);
        courseService.addSchedule(gs4c, sbs1, Schedule.WeekDay.FRIDAY, cHSecundaria1);
        courseService.addSchedule(gs4c, sbs1, Schedule.WeekDay.FRIDAY, cHSecundaria4);

        studentService.addStudent("Claude", "Picasso", "555444W", LocalDate.of(2001, Month.MAY, 5), father.getUserId(),
                gs4c.getClassGroupId());
    }

    /*
     *  Course Operations
     */

    @Test
    public void findCourseByIdTest() throws CourseNotFoundException, DuplicateCourseException {
        Course c = courseService.addCourse("1º Bachillerato", Course.CourseLevel.SECUNDARIA);
        assertThat(courseService.findCourseById(c.getCourseId())).isEqualTo(c);
    }

    @Test(expected = CourseNotFoundException.class)
    public void findCourseByIdTestException() throws CourseNotFoundException, DuplicateCourseException {
        courseService.findCourseById(8L);
    }

    @Test
    public void listCourseServiceTest() throws CourseNotFoundException {
        Page<Course> courseList = courseService.listCourse(new PageRequest(0, 10));
        assertThat(courseList).hasSize(5).contains(courseService.findCourseByCourseName("1º EP"));
    }

    @Test
    public void createCourseTest() throws DuplicateCourseException{

        CourseCreateForm courseForm = CourseCreateForm.builder().courseName("1º Ed. Infantil")
                .courseLevel(Course.CourseLevel.INFANTIL).build();
        Course c = courseService.createCourse(courseForm);
        Page<Course> courseList = courseService.listCourse(new PageRequest(0, 10));
        assertThat(courseList).contains(c);
    }

    @Test
    public void findCourseByCourseNameTest() throws DuplicateCourseException, CourseNotFoundException {
        Course c3 = courseService.addCourse("3º ESO", Course.CourseLevel.SECUNDARIA);
        assertThat(courseService.findCourseByCourseName("3º ESO")).isEqualTo(c3);
    }

    @Test(expected = CourseNotFoundException.class)
    public void findCourseByCourseNameNotFoundTest() throws CourseNotFoundException {
        courseService.findCourseByCourseName("3º ESO");
    }

    @Test
    public void findCourseByPartialCourseNameTest() throws CourseNotFoundException {

        Page<Course> courseList = courseService.findCourseByPartialCourseName(new PageRequest(0, 10), "2º");
        assertThat(courseList).hasSize(2).contains(courseService.findCourseByCourseName("2º EP"));
    }

    @Test(expected = DuplicateCourseException.class)
    public void addCourseDuplicatedTest() throws DuplicateCourseException {
        courseService.addCourse("1º EP", Course.CourseLevel.PRIMARIA);
    }

    @Test
    public void findCourseByCourseLevelTest() {
        Page<Course> courseList = courseService.findCourseByCourseLevel(new PageRequest(0, 10),
                Course.CourseLevel.SECUNDARIA);
        assertThat(courseList).hasSize(3);
    }

    @Test
    public void deleteCourseTest() throws CourseNotFoundException {
        assertThat(courseService.listCourse(new PageRequest(0,5))).hasSize(5);
        Course c = courseService.findCourseByCourseName("2º EP");
        courseService.deleteCourse(c.getCourseId());
        assertThat(courseService.listCourse(new PageRequest(0,5))).hasSize(4)
                .contains(courseService.findCourseByCourseName("1º EP"))
                .contains(courseService.findCourseByCourseName("1º ESO"))
                .contains(courseService.findCourseByCourseName("2º ESO"))
                .contains(courseService.findCourseByCourseName("4º ESO"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void deleteCourseInUseTest() throws CourseNotFoundException, ClassGroupNotFoundException {
        Course course = courseService.findCourseByCourseName("1º EP");
        Long courseId = course.getCourseId();
        courseService.deleteCourse(courseId);
        courseService.findClassGroupByClassGroupName("1º EP A");
    }

    @Test
    public void updateCourseTest() throws DuplicateCourseException, CourseNotFoundException {
        Course c3 = courseService.addCourse("3º ESO", Course.CourseLevel.SECUNDARIA);
        CourseCreateForm ccf = CourseCreateForm.builder().courseName("3º Educacion Secundaria Obligatoria")
                .courseLevel(Course.CourseLevel.SECUNDARIA).build();
        courseService.updateCourse(c3.getCourseId(), ccf);
        assertThat(courseService.findCourseByCourseName("3º Educacion Secundaria Obligatoria")).isEqualTo(c3);
    }

    /* * *
     *  SchoolYear Operations
     * * */

    @Test
    public void listSchoolYearTest() throws DuplicateSchoolYearException {
        Page<SchoolYear> schoolYearList = courseService.listSchoolYear(new PageRequest(0, 10));
        assertThat(schoolYearList).hasSize(3);
    }

    @Test
    public void findSchoolYearByIdTest() throws DuplicateSchoolYearException, SchoolYearNotFoundException {
        SchoolYear sy = courseService.addSchoolYear("2019-2020");
        assertThat(courseService.findSchoolYearBySchoolYearId(sy.getSchoolYearId())).isEqualTo(sy);
    }

    @Test
    public void findBySchoolYearNameTest() throws SchoolYearNotFoundException, DuplicateSchoolYearException {
        SchoolYear schoolYear = courseService.addSchoolYear("2012-2013");
        assertThat(courseService.findSchoolYearBySchoolYearName("2012-2013")).isEqualTo(schoolYear);
    }

    @Test(expected = SchoolYearNotFoundException.class)
    public void findBySchoolYearNameNotFoundTest() throws SchoolYearNotFoundException {
        courseService.findSchoolYearBySchoolYearName("2001-2002");
    }

    @Test(expected = DuplicateSchoolYearException.class)
    public void addSchoolYearDuplicateTest() throws DuplicateSchoolYearException {
        courseService.addSchoolYear("2014-2015");
    }

    @Test
    public void deleteSchoolYearTest()  throws SchoolYearNotFoundException {
        courseService.deleteSchoolYear(courseService.findSchoolYearBySchoolYearName("2013-2014").getSchoolYearId());
        assertThat(courseService.listSchoolYear(new PageRequest(0, 5))).hasSize(2);
        courseService.deleteSchoolYear(courseService.findSchoolYearBySchoolYearName("2015-2016").getSchoolYearId());
        assertThat(courseService.listSchoolYear(new PageRequest(0, 5))).hasSize(1)
                .containsExactly(courseService.findSchoolYearBySchoolYearName("2014-2015"));
    }

    @Test
    public void setVisibleSchoolYearTest() throws DuplicateSchoolYearException, SchoolYearNotFoundException {
        SchoolYear schoolYear = courseService.addSchoolYear("2018-2019");
        SchoolYear schoolYear2 = courseService.addSchoolYear("2020-2021");
        assertThat(courseService.findVisibleSchoolYear()).hasSize(0);
        courseService.setSchoolYearVisible(schoolYear.getSchoolYearId());
        assertThat(courseService.findVisibleSchoolYear()).hasSize(1).contains(schoolYear);
        courseService.setSchoolYearVisible(schoolYear2.getSchoolYearId());
        assertThat(courseService.findVisibleSchoolYear()).hasSize(2).contains(schoolYear).contains(schoolYear2);
    }

    @Test(expected = SchoolYearNotFoundException.class)
    public void setVisibleSchoolYearException() throws SchoolYearNotFoundException {
        courseService.setSchoolYearVisible(8L);
    }

    @Test
    public void hideSchoolYearTest() throws DuplicateSchoolYearException, SchoolYearNotFoundException {
        SchoolYear schoolYear = courseService.addSchoolYear("2018-2019");
        SchoolYear schoolYear2 = courseService.addSchoolYear("2020-2021");
        courseService.setSchoolYearVisible(schoolYear.getSchoolYearId());
        courseService.setSchoolYearVisible(schoolYear2.getSchoolYearId());
        assertThat(courseService.findVisibleSchoolYear()).hasSize(2).contains(schoolYear).contains(schoolYear2);
        courseService.hideSchoolYear(schoolYear.getSchoolYearId());
        assertThat(courseService.findVisibleSchoolYear()).hasSize(1).contains(schoolYear2);
    }

    @Test(expected = SchoolYearNotFoundException.class)
    public void hideSchoolYearException() throws SchoolYearNotFoundException {
        courseService.hideSchoolYear(3L);
    }

    /*
     *  ClassGroup Operations
     */

    @Test
    public void listClassGroupTest() {
        List<ClassGroup> groupList = courseService.listClassGroup();
        assertThat(groupList).hasSize(4);
    }

    @Test
    public void findClassGroupByIdTest() throws DuplicateClassGroupException, ClassGroupNotFoundException,
            DuplicateCourseException, SchoolYearNotFoundException, DuplicateUserException {

        User u = userService.createUser("melville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        SchoolYear sy = courseService.findSchoolYearBySchoolYearName("2014-2015");
        Course c2 = courseService.addCourse("2º Bach", Course.CourseLevel.SECUNDARIA);
        ClassGroup cg = courseService.addClassGroup("2º Bach letras", c2.getCourseId(), u, sy.getSchoolYearId(),
                "g2b001");
        Long id = cg.getClassGroupId();
        assertThat(courseService.findClassGroupById(id)).isEqualTo(cg);
    }

    @Test
    public void findByClassGroupNameTest() throws DuplicateClassGroupException, ClassGroupNotFoundException,
            DuplicateCourseException, SchoolYearNotFoundException, DuplicateUserException {

        User u = userService.createUser("melville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        SchoolYear sy = courseService.findSchoolYearBySchoolYearName("2014-2015");
        Course c2 = courseService.addCourse("2º Bach", Course.CourseLevel.SECUNDARIA);
        ClassGroup cg = courseService.addClassGroup("2º Bach Letras", c2.getCourseId(), u, sy.getSchoolYearId(),
                "gr01010");
        assertThat(courseService.findClassGroupByClassGroupName("2º Bach Letras")).isEqualTo(cg);
    }

    @Test(expected = ClassGroupNotFoundException.class)
    public void findByClassGroupNameNotFoundTest() throws ClassGroupNotFoundException {
        courseService.findClassGroupByClassGroupName("3º Bach Letras");
    }

    @Test(expected = DuplicateClassGroupException.class)
    public void addClassGroupDuplicateTest() throws CourseNotFoundException,
            SchoolYearNotFoundException, DuplicateClassGroupException, DuplicateUserException {

        User u = userService.createUser("melville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        SchoolYear sy = courseService.findSchoolYearBySchoolYearName("2014-2015");
        courseService.addClassGroup("1º EP A", courseService.findCourseByCourseName("1º EP").getCourseId(), u,
                sy.getSchoolYearId(), "");
    }

    @Test
    public void findClassGroupByCourseTest() throws CourseNotFoundException {

        Page<ClassGroup> page = courseService.findClassGroupByCourse(new PageRequest(0, 10),
                courseService.findCourseByCourseName("1º EP").getCourseId());
        assertThat(page).hasSize(2);
    }

    @Test
    public void findClassGroupByTutor() throws DuplicateClassGroupException, DuplicateCourseException,
            DuplicateUserException, DuplicateSchoolYearException, SchoolYearNotFoundException, UserNotFoundException {

        Course c1 = courseService.addCourse("1º Bach", Course.CourseLevel.PRIMARIA);
        Course c2 = courseService.addCourse("2º Bach", Course.CourseLevel.SECUNDARIA);
        SchoolYear sy = courseService.findSchoolYearBySchoolYearName("2013-2014");
        User t1 = userService.findUserByLoginName("mcervantes");
        User t2 = userService.findUserByLoginName("pheller");
        courseService.addClassGroup("1º Bach Ciencias", c1.getCourseId(), t1, sy.getSchoolYearId(), "12344");
        ClassGroup cg1 = courseService.addClassGroup("1º Bach Letras", c1.getCourseId(), t2, sy.getSchoolYearId(), "12345");
        ClassGroup cg2 = courseService.addClassGroup("2º Bach Letras", c2.getCourseId(), t2, sy.getSchoolYearId(), "12347");
        courseService.addClassGroup("2º Bach Ciencias", c2.getCourseId(), t1, sy.getSchoolYearId(), "12349");
        assertThat(courseService.findClassGroupByTutor(new PageRequest(0,5), t2.getUserId()))
                .hasSize(4).contains(cg1).contains(cg2);
    }

    @Test
    public void updateClassGroupTutor() throws DuplicateClassGroupException, DuplicateCourseException,
            DuplicateUserException, DuplicateSchoolYearException, ClassGroupNotFoundException, UserNotFoundException {
        SchoolYear sy = courseService.addSchoolYear("2016-2017");
        User t1 = userService.findUserByLoginName("mcervantes");
        User t2 = userService.findUserByLoginName("pheller");
        Course c1 = courseService.addCourse("1º Bach", Course.CourseLevel.SECUNDARIA);
        ClassGroup cg1 = courseService.addClassGroup("1B - A Ciencias", c1.getCourseId(), t1, sy.getSchoolYearId(), "12376");
        assertThat(courseService.findClassGroupByClassGroupName("1B - A Ciencias").getTutor()).isEqualTo(t1);
        courseService.updateClassGroupTutor(cg1.getClassGroupId(), t2);
        assertThat(courseService.findClassGroupByClassGroupName("1B - A Ciencias").getTutor()).isEqualTo(t2);
    }

    @Test
    public void updateClassGroup() throws DuplicateClassGroupException, DuplicateCourseException,
            DuplicateUserException, DuplicateSchoolYearException, ClassGroupNotFoundException, UserNotFoundException{
        SchoolYear sy = courseService.addSchoolYear("2016-2017");
        User t1 = userService.findUserByLoginName("mcervantes");
        User t2 = userService.findUserByLoginName("pheller");
        Course c1 = courseService.addCourse("1º Bach", Course.CourseLevel.SECUNDARIA);
        Course c2 = courseService.addCourse("2º Bach", Course.CourseLevel.SECUNDARIA);
        ClassGroup cg1 = courseService.addClassGroup("1B - A Ciencias", c1.getCourseId(), t1, sy.getSchoolYearId(), "23462");
        courseService.updateClassGroup(cg1.getClassGroupId(), "1B - A Ciencias", c2, t1, sy, "32344");
        assertThat(courseService.findClassGroupByClassGroupName("1B - A Ciencias").getCourse()).isEqualTo(c2);
        courseService.updateClassGroup(cg1.getClassGroupId(), "1B - A Ciencias", c2, t2, sy, "92344");
        assertThat(courseService.findClassGroupByClassGroupName("1B - A Ciencias").getTutor()).isEqualTo(t2);
    }

    @Test
    public void deleteClassGroup() throws ClassGroupNotFoundException {

        assertThat(courseService.listClassGroup()).hasSize(4);
        courseService.deleteClassGroup(courseService.findClassGroupByClassGroupName("4º ESO Letras").getClassGroupId());
        assertThat(courseService.listClassGroup()).hasSize(3);
    }

    @Test(expected = ClassGroupNotFoundException.class)
    public void deleteInexistentClassGroup() throws ClassGroupNotFoundException {

        courseService.deleteClassGroup(courseService.findClassGroupByClassGroupName("4º ESO Tecnologico")
                .getClassGroupId());
    }

    public void enrollStudentTest() throws StudentNotFoundException, DuplicateStudentException,
            DuplicateCourseException, DuplicateClassGroupException, DuplicateSchoolYearException,
            ClassGroupNotFoundException, CourseNotFoundException, SchoolYearNotFoundException,
            DuplicateUserException, UserNotFoundException {

        User u = userService.createUser("melville", "Herman", "Melville", "moby", "", "", User.Role.USER);
        User u2 = userService.createUser("bates", "William", "Bates", "deaf", "", "", User.Role.TEACHER);
        Course c5 = courseService.addCourse("5º ESO", Course.CourseLevel.SECUNDARIA);
        SchoolYear sy = courseService.findSchoolYearBySchoolYearName("2014-2015");
        ClassGroup cg5 = courseService.addClassGroup("5º ESO", c5.getCourseId(), u2 , sy.getSchoolYearId(), "4556677");
        Student st2 = studentService.addStudent("Leonardo","Davinci","16101123A", LocalDate.of(2008, Month.JULY, 2),
                u.getUserId(), cg5.getClassGroupId());
        Student st3 = studentService.addStudent("Isaac", "Newton", "46101123D", LocalDate.of(2007, Month.DECEMBER, 25),
                u.getUserId() , cg5.getClassGroupId());
        ClassGroup cg1 = courseService.findClassGroupByClassGroupName("4º ESO Letras");
        studentService.enrollStudent(cg1.getClassGroupId(), st3.getStudentId());
        studentService.enrollStudent(cg1.getClassGroupId(), st2.getStudentId());
        assertThat(cg1.getStudentList()).contains(st3);
        assertThat(cg1.getStudentList()).contains(st2);
        Course c = courseService.findCourseByCourseName("4º ESO");
        assertThat(c).isEqualTo(cg1.getCourse());
        assertThat(st2.getCourse()).isEqualTo(c);
        assertThat(st3.getCourse()).isEqualTo(c);
        assertThat(courseService.findClassGroupByClassGroupName("4º ESO Letras").getStudentList()).contains(st3);
        assertThat(courseService.findClassGroupByClassGroupName("4º ESO Letras").getStudentList()).contains(st2);
    }

    public void unenrollStudentTest() throws StudentNotFoundException, DuplicateStudentException,
            DuplicateCourseException, DuplicateClassGroupException, DuplicateSchoolYearException,
            ClassGroupNotFoundException, CourseNotFoundException, SchoolYearNotFoundException,
            DuplicateUserException, UserNotFoundException {

        User u = userService.createUser("melville", "Herman", "Melville", "moby", "", "", User.Role.USER);
        User u2 = userService.createUser("bates", "William", "Bates", "deaf", "", "", User.Role.TEACHER);
        Course c5 = courseService.addCourse("5º ESO", Course.CourseLevel.SECUNDARIA);
        SchoolYear sy = courseService.findSchoolYearBySchoolYearName("2014-2015");
        ClassGroup cg5 = courseService.addClassGroup("5º ESO", c5.getCourseId(), u2 , sy.getSchoolYearId(), "4556677");
        Student st2 = studentService.addStudent("Leonardo","Davinci","16101123A", LocalDate.of(2008, Month.JULY, 2),
                u.getUserId(), cg5.getClassGroupId());
        Student st3 = studentService.addStudent("Isaac", "Newton", "46101123D", LocalDate.of(2007, Month.DECEMBER, 25),
                u.getUserId() , cg5.getClassGroupId());
        ClassGroup cg1 = courseService.findClassGroupByClassGroupName("4º ESO Letras");
        studentService.enrollStudent(cg1.getClassGroupId(), st3.getStudentId());
        assertThat(cg1.getStudentList()).contains(st3);
        assertThat(cg1.getStudentList()).hasSize(1);
        studentService.unenrollStudent(cg1.getClassGroupId(), st3.getStudentId());
        assertThat(cg1.getStudentList()).hasSize(0);

    }

    /*
     *  Schedule Operations
     */

    @Test
    public void listScheduleTest() throws DuplicateClassGroupException, DuplicateCourseException, DuplicateUserException,
            DuplicateSchoolYearException, ClassGroupNotFoundException, UserNotFoundException, DuplicateSubjectException,
            DuplicateScheduleException {

        Page<Schedule> horario = courseService.listSchedule(new PageRequest(0, 10));
        assertThat(horario).hasSize(7);
    }

    @Test
    public void findScheduleByIdTest() throws ScheduleNotFoundException, DuplicateScheduleException,
            DuplicateClassGroupException, DuplicateCourseException, DuplicateSchoolYearException,
            ClassGroupNotFoundException, DuplicateSubjectException, DuplicateClassHourLevelException,
            UserNotFoundException, CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("2º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr00302");
        Subject s1 = subjectService.addSubject("Matemáticas", c1.getCourseId(), "MA222111");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.SIXTH_HOUR, "15:30", "16:30");
        Schedule sch = courseService.addSchedule(cg, s1, Schedule.WeekDay.THURSDAY, ch);
        Schedule s = courseService.findScheduleById(sch.getScheduleId());
        assertThat(s).isEqualTo(sch);
    }

    @Test
    public void findScheduleTest() throws ScheduleNotFoundException, DuplicateScheduleException,
            DuplicateClassGroupException, DuplicateCourseException, DuplicateSchoolYearException,
            ClassGroupNotFoundException, DuplicateSubjectException, DuplicateClassHourLevelException,
            UserNotFoundException, CourseNotFoundException{

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("2º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr32834");
        Subject s1 = subjectService.addSubject("Matemáticas", c1.getCourseId(), "MA3333222");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.FIFTH_HOUR, "15:30", "16:30");
        Schedule sch = courseService.addSchedule(cg, s1, Schedule.WeekDay.MONDAY, ch);
        assertThat(courseService.findSchedule(cg.getClassGroupId(), s1.getSubjectId(), Schedule.WeekDay.MONDAY, ch))
                .isEqualTo(sch);
    }

    @Test (expected = ScheduleNotFoundException.class)
    public void findInexistentScheduleTest() throws ScheduleNotFoundException, DuplicateScheduleException,
            DuplicateClassGroupException, DuplicateCourseException, DuplicateSchoolYearException,
            ClassGroupNotFoundException, DuplicateSubjectException, DuplicateClassHourLevelException,
            UserNotFoundException, CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("2º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr42344");
        Subject s1 = subjectService.addSubject("Matemáticas", c1.getCourseId(), "MA223344");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.FIFTH_HOUR, "15:30", "16:30");
        courseService.findSchedule(cg.getClassGroupId(), s1.getSubjectId(), Schedule.WeekDay.MONDAY, ch);
    }

    @Test (expected = DuplicateScheduleException.class)
    public void addScheduleDuplicateTest() throws DuplicateScheduleException, DuplicateClassGroupException,
            DuplicateCourseException, DuplicateSchoolYearException, ClassGroupNotFoundException,
            DuplicateSubjectException, DuplicateClassHourLevelException, UserNotFoundException,
            CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("6º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr88244");
        Subject s1 = subjectService.addSubject("Matemáticas", c1.getCourseId(), "MA3322221");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.FIFTH_HOUR, "15:30", "16:30");
        courseService.addSchedule(cg, s1, Schedule.WeekDay.MONDAY, ch);
        courseService.addSchedule(cg, s1, Schedule.WeekDay.MONDAY, ch);
    }

    @Test
    public void findScheduleByGroupTest() throws DuplicateScheduleException, DuplicateClassGroupException,
            DuplicateCourseException, DuplicateSchoolYearException, ClassGroupNotFoundException,
            DuplicateSubjectException, DuplicateClassHourLevelException, UserNotFoundException,
            CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg2 = courseService.addClassGroup("6º EP B", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr34999");
        Subject s1 = subjectService.addSubject("Matemáticas", c1.getCourseId(), "MA3423423");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.FIFTH_HOUR, "15:30", "16:30");
        ClassHourLevel ch2 = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.SIXTH_HOUR, "15:30", "16:30");
        courseService.addSchedule(cg2, s1, Schedule.WeekDay.MONDAY, ch);
        courseService.addSchedule(cg2, s1, Schedule.WeekDay.MONDAY, ch2);
        assertThat(courseService.findScheduleByGroup(cg2.getClassGroupId())).hasSize(2);
    }

    @Test
    public void findStudentScheduleByGroupAndWeekDayTest()  throws DuplicateScheduleException,
            DuplicateClassGroupException, DuplicateCourseException, DuplicateSchoolYearException,
            ClassGroupNotFoundException, DuplicateSubjectException, DuplicateClassHourLevelException,
            UserNotFoundException, CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg2 = courseService.addClassGroup("6º EP B", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr34999");
        Subject s1 = subjectService.addSubject("Matemáticas", c1.getCourseId(), "MA3423423");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.FIFTH_HOUR, "15:30", "16:30");
        ClassHourLevel ch2 = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.SIXTH_HOUR, "15:30", "16:30");
        courseService.addSchedule(cg2, s1, Schedule.WeekDay.MONDAY, ch);
        courseService.addSchedule(cg2, s1, Schedule.WeekDay.MONDAY, ch2);
        courseService.addSchedule(cg2, s1, Schedule.WeekDay.TUESDAY, ch);
        courseService.addSchedule(cg2, s1, Schedule.WeekDay.WEDNESDAY, ch2);
        assertThat(courseService.findStudentScheduleByGroupAndWeekDay(cg2.getClassGroupId(),
                Schedule.WeekDay.MONDAY)).hasSize(2);
        assertThat(courseService.findStudentScheduleByGroupAndWeekDay(cg2.getClassGroupId(),
                Schedule.WeekDay.TUESDAY)).hasSize(1);
    }

    @Test
    public void findScheduleBySubjectTest() throws DuplicateScheduleException, DuplicateClassGroupException,
            DuplicateCourseException, DuplicateSchoolYearException, ClassGroupNotFoundException,
            DuplicateSubjectException, DuplicateClassHourLevelException, UserNotFoundException,
            CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("6º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr8877");
        ClassGroup cg2 = courseService.addClassGroup("6º EP B", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr55555");
        Subject s2 = subjectService.addSubject("Ingles", c1.getCourseId(), "MA342423");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.SIXTH_HOUR, "15, 30", "16:30");
        courseService.addSchedule(cg2, s2, Schedule.WeekDay.MONDAY, ch);
        courseService.addSchedule(cg, s2, Schedule.WeekDay.TUESDAY, ch);
        courseService.addSchedule(cg2, s2, Schedule.WeekDay.THURSDAY, ch);
        courseService.addSchedule(cg, s2, Schedule.WeekDay.FRIDAY, ch);
        assertThat(courseService.findScheduleBySubject(new PageRequest(0, 10), s2.getSubjectId())).hasSize(4);
    }

    @Test
    public void deleteScheduleTest() throws ClassGroupNotFoundException, SubjectNotFoundException,
            ScheduleNotFoundException, DuplicateClassHourLevelException, DuplicateScheduleException,
            CourseNotFoundException{


        ClassGroup cg = courseService.findClassGroupByClassGroupName("1º EP A");
        Subject s1 = subjectService.findSubjectBySubjectNameAndCourseName("Matemáticas", "1º EP");
        ClassHourLevel ch = courseService.addClassHourLevel(Course.CourseLevel.PRIMARIA,
                ClassHourLevel.ClassHour.SIXTH_HOUR, "15:30", "16:30");
        Schedule schedule = courseService.addSchedule(cg, s1, Schedule.WeekDay.TUESDAY, ch);
        assertThat(courseService.listSchedule(new PageRequest(0, 10))).hasSize(8).contains(schedule);
        courseService.deleteSchedule(schedule.getScheduleId());
        assertThat(courseService.listSchedule(new PageRequest(0,10))).hasSize(7).doesNotContain(schedule);
    }

    /*
     * GroupSubject Operations
     */

    @Test
    public void findGroupSubjectByTeacherTest() throws DuplicateGroupSubjectException, DuplicateClassGroupException,
            DuplicateCourseException, DuplicateSchoolYearException, ClassGroupNotFoundException,
            DuplicateSubjectException, DuplicateUserException, SchoolYearNotFoundException, CourseNotFoundException {

        User u = userService.createUser("melville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        courseService.setSchoolYearVisible(sy.getSchoolYearId());
        ClassGroup cg = courseService.addClassGroup("6º EP - A", c1.getCourseId(), u, sy.getSchoolYearId(), "gr01010");
        ClassGroup cg2 = courseService.addClassGroup("6º EP - B", c1.getCourseId(), u, sy.getSchoolYearId(), "gr01011");
        Subject s1 = subjectService.addSubject("Química", c1.getCourseId(), "MA34134");
        Subject s2 = subjectService.addSubject("Física", c1.getCourseId(), "MA23422");
        Subject s3 = subjectService.addSubject("Tecnología", c1.getCourseId(), "MA33322");
        courseService.addGroupSubject(cg, s1, u);
        courseService.addGroupSubject(cg2, s1, u);
        GroupSubject gs = courseService.addGroupSubject(cg, s2, u);
        assertThat(gs.getTeacher()).isEqualTo(u);
        courseService.addGroupSubject(cg, s3, u);
        assertThat(courseService.findGroupSubjectByTeacher(u.getUserId())).hasSize(4);
        assertThat(courseService.findGroupSubjectByClassGroup(cg.getClassGroupId())).hasSize(3);
    }

    @Test
    public void findGroupSubjectByClassGroupTest() throws DuplicateGroupSubjectException, DuplicateClassGroupException,
            DuplicateCourseException, DuplicateSchoolYearException, ClassGroupNotFoundException,
            DuplicateSubjectException, DuplicateUserException, UserNotFoundException, CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("6º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr9977");
        ClassGroup cg2 = courseService.addClassGroup("6º EP B", c1.getCourseId(), u1, sy.getSchoolYearId(), "gr8833");
        Subject s1 = subjectService.addSubject("Química", c1.getCourseId(), "MA12312G");
        Subject s2 = subjectService.addSubject("Física", c1.getCourseId(), "MA33333G");
        Subject s3 = subjectService.addSubject("Tecnología", c1.getCourseId(), "MA33221d");
        User u = userService.createUser("melville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        User u2 = userService.createUser("steinbeck", "John", "Steinbeck", "perla", "", "", User.Role.TEACHER);
        courseService.addGroupSubject(cg,s1,u);
        courseService.addGroupSubject(cg2,s1,u2);
        courseService.addGroupSubject(cg,s2,u);
        courseService.addGroupSubject(cg,s3,u2);
        assertThat(courseService.findGroupSubjectByClassGroup(cg.getClassGroupId())).hasSize(3);
    }

    @Test
    public void deleteGroupSubjectTest() throws DuplicateGroupSubjectException, DuplicateClassGroupException,
            DuplicateCourseException, DuplicateSchoolYearException, ClassGroupNotFoundException,
            DuplicateSubjectException, DuplicateUserException, GroupSubjectNotFoundException, UserNotFoundException,
            CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("6º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "");
        ClassGroup cg2 = courseService.addClassGroup("6º EP B", c1.getCourseId(), u1, sy.getSchoolYearId(), "");
        Subject s1 = subjectService.addSubject("Química", c1.getCourseId(), "MA211S1");
        Subject s2 = subjectService.addSubject("Física", c1.getCourseId(), "MA2211W");
        Subject s3 = subjectService.addSubject("Tecnología", c1.getCourseId(), "MA32323R");
        User u = userService.createUser("melville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        User u2 = userService.createUser("steinbeck", "John", "Steinbeck", "perla", "", "", User.Role.TEACHER);
        courseService.addGroupSubject(cg,s1,u);
        GroupSubject gs = courseService.addGroupSubject(cg2,s1,u2);
        courseService.addGroupSubject(cg,s2,u);
        GroupSubject gs2 = courseService.addGroupSubject(cg,s3,u2);
        assertThat(courseService.findGroupSubjectByClassGroup(cg.getClassGroupId())).hasSize(3);
        courseService.deleteGroupSubject(gs.getGroupSubjectId());
        courseService.deleteGroupSubject(gs2.getGroupSubjectId());
        assertThat(courseService.findGroupSubjectByClassGroup(cg.getClassGroupId())).hasSize(2);

    }

    @Test
    public void editGroupSubjectTest() throws DuplicateGroupSubjectException, DuplicateClassGroupException,
            DuplicateCourseException, DuplicateSchoolYearException, ClassGroupNotFoundException,
            DuplicateSubjectException, DuplicateUserException, GroupSubjectNotFoundException, UserNotFoundException,
            CourseNotFoundException {

        Course c1 = courseService.addCourse("6º EP", Course.CourseLevel.PRIMARIA);
        SchoolYear sy = courseService.addSchoolYear("2018-2019");
        User u1 = userService.findUserByLoginName("mcervantes");
        ClassGroup cg = courseService.addClassGroup("6º EP A", c1.getCourseId(), u1, sy.getSchoolYearId(), "");
        ClassGroup cg2 = courseService.addClassGroup("6º EP B", c1.getCourseId(), u1, sy.getSchoolYearId(), "");
        Subject s1 = subjectService.addSubject("Química", c1.getCourseId(), "MA211S1");
        Subject s2 = subjectService.addSubject("Física", c1.getCourseId(), "MA2211W");
        Subject s3 = subjectService.addSubject("Tecnología", c1.getCourseId(), "MA32323R");
        User u = userService.createUser("melville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        User u2 = userService.createUser("steinbeck", "John", "Steinbeck", "perla", "", "", User.Role.TEACHER);
        GroupSubject gs = courseService.addGroupSubject(cg,s1,u);
        GroupSubjectCreateForm gscf = GroupSubjectCreateForm.builder().classGroup(gs.getClassGroup())
                .subject(gs.getSubject()).teacher(u2).build();
        courseService.updateGroupSubject(gs.getGroupSubjectId(), gscf);
        assertThat(courseService.findGroupSubjectById(gs.getGroupSubjectId()).getTeacher()).isEqualTo(u2);
    }

    /*
     * ClassHourLevel Operations
     */

    @Test
    public void listClassHourLevelTest() {
        assertThat(courseService.listClassHourLevel(new PageRequest(0, 10))).hasSize(8);
    }

    @Test
    public void findClassHourByCourseTest() {
        assertThat(courseService.findByCourseLevel(new PageRequest(0, 10), Course.CourseLevel.PRIMARIA)).hasSize(4);
    }

    @Test
    public void findClassHourLevelByCourseLevelTest() throws CourseNotFoundException {
        Course c = courseService.findCourseByCourseName("4º ESO");
        assertThat(courseService.findClassHourLevelByCourseLevel(c.getCourseLevel())).hasSize(4);
    }


}