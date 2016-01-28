package taboleiro.model.service;

import taboleiro.model.domain.course.*;
import taboleiro.model.domain.student.*;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.student.GlobalGrade.Evaluation;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.*;
import taboleiro.model.repository.student.StudentRepository;
import taboleiro.model.repository.student.GlobalGradeLevelRepository;
import taboleiro.model.service.user.UserService;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import static com.google.common.base.Preconditions.checkNotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.model.repository.student.GlobalGradeRepository;
import java.time.LocalDate;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;
    private GlobalGradeRepository globalGradeRepository;
    private GlobalGradeLevelRepository globalGradeLevelRepository;
    private CourseService courseService;
    private UserService userService;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, GlobalGradeRepository globalGradeRepository,
                              CourseService courseService, UserService userService,
                              GlobalGradeLevelRepository globalGradeLevelRepository) {
        this.studentRepository = studentRepository;
        this.globalGradeRepository = globalGradeRepository;
        this.globalGradeLevelRepository = globalGradeLevelRepository;
        this.userService = userService;
        this.courseService = courseService;
    }

    /* * *
     *  Student Operations
     * * */

    @Transactional
    @Override
    public Student findStudentById(Long studentId) throws StudentNotFoundException {
        checkNotNull(studentId, "studentId");
        Student s = studentRepository.findStudentByStudentId(studentId);
        if (s == null) {
            throw new StudentNotFoundException(studentId, Student.class.getName());
        } else {
            return s;
        }
    }

    @Transactional
    @Override
    public Student findStudentByDni(String dni) throws StudentNotFoundException {
        checkNotNull(dni, "dni");
        Student s = studentRepository.findStudentByDni(dni);
        if (s == null) {
            throw new StudentNotFoundException(dni, Student.class.getName());
        } else {
            return s;
        }
    }

    @Transactional
    @Override
    public Page<Student> listStudent(Pageable page) {
        checkNotNull(page, "page");
        return studentRepository.findAll(page);
    }

    @Transactional
    @Override
    public Student addStudent(String firstName, String lastName, String dni, LocalDate birthDate, Long guardianId,
                              Long classGroupId) throws DuplicateStudentException, ClassGroupNotFoundException,
            UserNotFoundException {
        checkNotNull(firstName, "firstName");
        checkNotNull(lastName, "lastName");
        checkNotNull(dni, "dni");
        checkNotNull(birthDate, "birthDate");
        checkNotNull(guardianId, "guardianId");
        checkNotNull(classGroupId, "classGroupId");

        if (studentRepository.findStudentByDni(dni) != null) {
            throw new DuplicateStudentException(dni, Student.class.getName());
        }
        User user = userService.findUserByUserId(guardianId);

        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        Set<ClassGroup> classGroupsList = new HashSet<ClassGroup>();
        classGroupsList.add(classGroup);
        Student newStudent = Student.builder().firstName(firstName).lastName(lastName).dni(dni)
                .birthDate(birthDate).guardian(user).course(classGroup.getCourse()).currentClassGroup(classGroup)
                .classGroups(classGroupsList).build();
        return studentRepository.save(newStudent);
    }

    @Transactional
    @Override
    public List<Student> findStudentByClassGroup(Long classGroupId) throws ClassGroupNotFoundException {
        checkNotNull(classGroupId, "classGroup");

        return studentRepository.findStudentByCurrentClassGroup(classGroupId);
    }


    @Transactional
    @Override
    public Page<Student> findStudentByCourse(Pageable page, Long courseId) {
        checkNotNull(page, "page");
        checkNotNull(courseId, "course");

        return studentRepository.findStudentByCourse(page, courseId);
    }

    @Transactional
    @Override
    public Student updateStudent(Long studentId, String firstName, String lastName, String dni, LocalDate birthDate,
                          Long guardianId, Long classGroupId, String codigoAlumno) throws StudentNotFoundException,
                            ClassGroupNotFoundException, UserNotFoundException {
        checkNotNull(studentId, "studentId");
        checkNotNull(firstName, "firstName");
        checkNotNull(lastName, "lastName");
        checkNotNull(birthDate, "birthDate");
        checkNotNull(classGroupId, "classGroup");
        checkNotNull(guardianId, "guardianId");

        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        User guardian = userService.findUserByUserId(guardianId);
        Student student = findStudentById(studentId);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDni(dni);
        student.setBirthDate(birthDate);
        student.setGuardian(guardian);
        ClassGroup cg = student.getCurrentClassGroup();
        student.setCurrentClassGroup(classGroup);
        student.getClassGroups().remove(cg);
        student.getClassGroups().add(classGroup);
        student.setCourse(classGroup.getCourse());
        student.setCodigoAlumno(codigoAlumno);
        return studentRepository.save(student);
    }


    @Transactional
    @Override
    public Student updateCourse(Long studentId, Long courseId) throws StudentNotFoundException, CourseNotFoundException {
        checkNotNull(studentId, "student");
        checkNotNull(courseId, "course");

        Student student = studentRepository.findStudentByStudentId(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId, Student.class.getName());
        } else {
            student.setCourse(courseService.findCourseById(courseId));
            return studentRepository.save(student);
        }
    }

    @Transactional
    @Override
    public Student enrollStudent(Long classGroupId, Long studentId) throws StudentNotFoundException,
            ClassGroupNotFoundException {

        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        Student student = studentRepository.findStudentByStudentId(studentId);
        student.setCurrentClassGroup(classGroup);
        student.setCourse(classGroup.getCourse());
        Set<ClassGroup> classGroupList = student.getClassGroups();
        classGroupList.add(classGroup);
        return studentRepository.save(student);
    }

    @Transactional
    @Override
    public Student unenrollStudent(Long classGroupId, Long studentId) throws StudentNotFoundException,
            ClassGroupNotFoundException {
        checkNotNull(studentId, "student");
        checkNotNull(classGroupId, "classGroupId");

        Student student = studentRepository.findStudentByStudentId(studentId);
        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        ClassGroup currentClassGroup = student.getCurrentClassGroup();
        if (!classGroup.equals(currentClassGroup)) {
            Set<ClassGroup> classGroupList = student.getClassGroups();
            classGroupList.remove(classGroup);
        }

        return studentRepository.save(student);
    }

    @Transactional
    @Override
    public void deleteStudent(Long student) {
        checkNotNull(student, "student");

        studentRepository.delete(student);
    }

    @Transactional
    @Override
    public List<Student> findStudentByGuardian(Long guardianId) {
        checkNotNull(guardianId, "guardian");

        return studentRepository.findStudentByGuardian(guardianId);
    }

    /* * *
     *  GlobalGrade Operations
     * * */

    @Transactional
    @Override
    public GlobalGrade findGlobalGrade(Long studentId, Long subjectId, Long schoolYearId, Evaluation evaluation)
            throws GlobalGradeNotFoundException {
        checkNotNull(studentId, "student");
        checkNotNull(subjectId, "subject");
        checkNotNull(schoolYearId, "schoolYear");
        checkNotNull(evaluation, "evaluation");

        GlobalGrade gg = globalGradeRepository.findByStudentAndSubjectAndSchoolYearAndEvaluation(studentId,
                subjectId, schoolYearId, evaluation);
        if (gg == null) {
            throw new GlobalGradeNotFoundException(studentId, "GlobalGrade");
        } else {
            return gg;
        }
    }

    @Transactional
    @Override
    public GlobalGrade addGlobalGrade(Long studentId, Subject subject, SchoolYear schoolYear, GlobalGradeLevel grade,
                                      Evaluation evaluation, String observation)
            throws DuplicateGlobalGradeException {
        checkNotNull(studentId, "student");
        checkNotNull(subject, "subject");
        checkNotNull(schoolYear, "schoolYear");
        checkNotNull(grade, "grade");
        checkNotNull(evaluation, "evaluation");

        Student student = studentRepository.findStudentByStudentId(studentId);
        GlobalGrade gg = globalGradeRepository.findByStudentAndSubjectAndSchoolYearAndEvaluation(studentId,
                subject.getSubjectId(), schoolYear.getSchoolYearId(), evaluation);
        if (gg != null) {
            throw new DuplicateGlobalGradeException(studentId, GlobalGrade.class.getName());
        }
        GlobalGrade newGlobalGrade = GlobalGrade.builder().student(student).subject(subject).schoolYear(schoolYear)
                .grade(grade).evaluation(evaluation).build();
        return globalGradeRepository.save(newGlobalGrade);
    }

    @Transactional
    @Override
    public Page<GlobalGrade> findGlobalGradeBySubjectAndSchoolYear(Pageable page, Long subjectId, Long schoolYearId) {
        checkNotNull(page, "page");
        checkNotNull(subjectId, "subject");
        checkNotNull(schoolYearId, "schoolYear");

        return globalGradeRepository.findGlobalGradeBySubjectAndSchoolYear(page, subjectId,
                schoolYearId);
    }

    @Transactional
    @Override
    public List<GlobalGrade> findGlobalGradeByStudent(Long studentId) {
        checkNotNull(studentId, "student");

        return globalGradeRepository.findGlobalGradeByStudent(studentId);
    }

    @Transactional
    @Override
    public Page<GlobalGrade> listGlobalGrades(Pageable page) {
        return globalGradeRepository.findAll(page);
    }

    @Transactional
    @Override
    public List<GlobalGrade> findGlobalGradeByStudentAndSchoolYear(Long studentId, Long schoolYearId) {
        checkNotNull(studentId, "student");
        checkNotNull(schoolYearId, "schoolYear");

        return globalGradeRepository.findGlobalGradeByStudentAndSchoolYearOrderByEvaluationDesc(studentId,
                schoolYearId);
    }

    @Transactional
    @Override
    public GlobalGrade findGlobalGradeById(Long globalGradeId) throws GlobalGradeNotFoundException {
        checkNotNull(globalGradeId, "globalGradeId");

        GlobalGrade gg = globalGradeRepository.findByGlobalGradeId(globalGradeId);
        if (gg == null) {
            throw new GlobalGradeNotFoundException(globalGradeId, "GlobalGradeId Not Found");
        } else {
            return gg;
        }
    }

    @Transactional
    @Override
    public GlobalGrade updateGlobalGrade(Long globalGradeId, GlobalGradeLevel grade)
            throws GlobalGradeNotFoundException {
        checkNotNull(globalGradeId, "globalGradeId");
        checkNotNull(grade, "grade");

        GlobalGrade gg = globalGradeRepository.findByGlobalGradeId(globalGradeId);
        if (gg == null) {
            throw new GlobalGradeNotFoundException(globalGradeId, "GlobalGrade");
        } else {
            gg.setGrade(grade);
            return globalGradeRepository.save(gg);
        }
    }

    @Transactional
    @Override
    public List<GlobalGrade> findGlobalGradeByGroupSubjectAndEvaluation(Long groupSubjectId, Evaluation evaluation)
            throws GroupSubjectNotFoundException {
        checkNotNull(groupSubjectId, "groupSubject");
        checkNotNull(evaluation, "evaluation");

        GroupSubject groupSubject = courseService.findGroupSubjectById(groupSubjectId);

        return globalGradeRepository.findGlobalGradeBySubjectAndSchoolYearAndEvaluation(groupSubject.getSubject()
                        .getSubjectId(), groupSubject.getClassGroup().getSchoolYear().getSchoolYearId(), evaluation);
    }

    @Transactional
    @Override
    public List<StudentGlobalGradeCO> findGlobalGradeEvByClassGroupSubject(Long groupSubjectId)
            throws GroupSubjectNotFoundException {
        checkNotNull(groupSubjectId, "classGroupId");

        GroupSubject gs = courseService.findGroupSubjectById(groupSubjectId);
        List<StudentGlobalGradeCO> studentGlobalGradeList = new ArrayList<>();
        List<Student> studentList = studentRepository.findStudentByCurrentClassGroup(gs.getClassGroup()
                .getClassGroupId());
        List<GlobalGrade> globalGradeList = globalGradeRepository.findGlobalGradeBySubjectAndSchoolYear(
                gs.getSubject().getSubjectId(), gs.getClassGroup().getSchoolYear().getSchoolYearId());
        Iterator studentIterator = studentList.iterator();
        while(studentIterator.hasNext()) {
            Iterator globalGradeIterator = globalGradeList.iterator();
            Student student = (Student)studentIterator.next();
            String globalGradeEv1 = "";
            String globalGradeEv2 = "";
            String globalGradeEv3 = "";
            while(globalGradeIterator.hasNext()) {
                GlobalGrade globalGrade = (GlobalGrade)globalGradeIterator.next();
                if(globalGrade.getStudent().getStudentId().equals(student.getStudentId())) {
                    if(globalGrade.getEvaluation().equals(Evaluation.FIRST)){
                        globalGradeEv1 = globalGrade.getGrade().getGrade();
                    }
                    if(globalGrade.getEvaluation().equals(Evaluation.SECOND)) {
                        globalGradeEv2 = globalGrade.getGrade().getGrade();
                    }
                    if(globalGrade.getEvaluation().equals(Evaluation.THIRD)) {
                        globalGradeEv3 = globalGrade.getGrade().getGrade();
                    }
                }
            }
            StudentGlobalGradeCO studentGlobalGradeCO = new StudentGlobalGradeCO(student.getStudentId(),
                    student.getFirstName(), student.getLastName(), globalGradeEv1, globalGradeEv2, globalGradeEv3);
            studentGlobalGradeList.add(studentGlobalGradeCO);
        }
        return studentGlobalGradeList;
    }

    /* * *
     *  GlobalGradeLevel Operations
     * * */

    @Transactional
    @Override
    public Page<GlobalGradeLevel> findAllGlobalGradeLevel(Pageable page) {
        checkNotNull(page, "page");

        return globalGradeLevelRepository.findAll(page);
    }

    @Transactional
    @Override
    public GlobalGradeLevel findGlobalGradeLevelById(Long globalGradeLevelId) {
        checkNotNull(globalGradeLevelId, "courseLevel");

        return globalGradeLevelRepository.findGlobalGradeLevelByGlobalGradeLevelId(globalGradeLevelId);

    }

    @Transactional
    @Override
    public List<GlobalGradeLevel> findGlobalGradeLevelByCourseLevel(Course.CourseLevel courseLevel) {
        checkNotNull(courseLevel, "courseLevel");

        return globalGradeLevelRepository.findGlobalGradeLevelByCourseLevel(courseLevel);
    }

    @Transactional
    @Override
    public GlobalGradeLevel addGlobalGradeLevel(String grade, String gradeName, String xadeGrade,
                    Course.CourseLevel courseLevel) throws DuplicateGlobalGradeLevelException {

        GlobalGradeLevel ggl = globalGradeLevelRepository.findGlobalGradeLevelByGradeAndCourseLevel(grade, courseLevel);
        if (ggl != null) {
            throw new DuplicateGlobalGradeLevelException(ggl, "GlobalGradeLevel");
        }
        else {
            GlobalGradeLevel newGlobalGradeLevel = GlobalGradeLevel.builder().grade(grade).gradeName(gradeName)
                    .courseLevel(courseLevel).xadeGrade(xadeGrade).courseLevel(courseLevel).build();
            return globalGradeLevelRepository.save(newGlobalGradeLevel);
        }
    }

    @Transactional
    @Override
    public void deleteGlobalGradeLevel(Long globalGradeLevelId) throws GlobalGradeLevelNotFoundException {
        checkNotNull(globalGradeLevelId, "globalGradeLevelId");

        GlobalGradeLevel ggl = globalGradeLevelRepository.findGlobalGradeLevelByGlobalGradeLevelId(globalGradeLevelId);

        if (ggl == null) {
            throw new GlobalGradeLevelNotFoundException(ggl, "GlobalGradeLevel Not Found");
        }
        else {
            globalGradeLevelRepository.delete(ggl.getGlobalGradeLevelId());
        }
    }
}
