package taboleiro.model.service;

import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import static com.google.common.base.Preconditions.checkNotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.controller.subject.GroupSubjectCreateForm;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.student.Student;
import taboleiro.model.exception.*;
import taboleiro.model.repository.course.*;
import taboleiro.model.repository.student.StudentRepository;
import taboleiro.model.exception.SchoolYearNotFoundException;
import taboleiro.model.exception.ClassGroupNotFoundException;
import taboleiro.controller.course.CourseCreateForm;
import taboleiro.model.exception.GroupSubjectNotFoundException;

import java.util.*;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private SchoolYearRepository schoolYearRepository;
    private ClassGroupRepository classGroupRepository;
    private ScheduleRepository scheduleRepository;
    private GroupSubjectRepository groupSubjectRepository;
    private ClassHourLevelRepository classHourLevelRepository;
    private ViewTeacherScheduleRepository viewTeacherScheduleRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, SchoolYearRepository schoolYearRepository,
                             ClassGroupRepository classGroupRepository, ScheduleRepository scheduleRepository,
                             GroupSubjectRepository groupSubjectRepository,
                             ClassHourLevelRepository classHourLevelRepository,
                             ViewTeacherScheduleRepository viewTeacherScheduleRepository) {

        this.courseRepository = courseRepository;
        this.schoolYearRepository = schoolYearRepository;
        this.classGroupRepository = classGroupRepository;
        this.scheduleRepository = scheduleRepository;
        this.classGroupRepository = classGroupRepository;
        this.groupSubjectRepository = groupSubjectRepository;
        this.classHourLevelRepository = classHourLevelRepository;
        this.viewTeacherScheduleRepository = viewTeacherScheduleRepository;
    }

    /* * *
     *  Course Operations
     * * */

    @Transactional
    @Override
    public Page<Course> listCourse(Pageable page) {
        return courseRepository.findAll(checkNotNull(page, "page"));
    }

    @Transactional
    @Override
    public List<Course> listCourse() {
        return courseRepository.findAll();
    }

    @Transactional
    @Override
    public Course findCourseById(Long courseId) throws CourseNotFoundException{

        Course c = courseRepository.findCourseByCourseId(courseId);
        if (c == null) {
            throw new CourseNotFoundException(courseId, Course.class.getName());
        }
        else {
            return c;
        }
    }

    @Transactional
    @Override
    public Course findCourseByCourseName(String courseName) throws CourseNotFoundException {

        Course c = courseRepository.findCourseByCourseName(checkNotNull(courseName, "courseName"));
        if (c == null) {
            throw new CourseNotFoundException(courseName, Course.class.getName());
        }
        else {
            return c;
        }
    }

    @Transactional
    @Override
    public Page<Course> findCourseByPartialCourseName(Pageable page, String courseName) {
        checkNotNull(page, "page");
        checkNotNull(courseName, "courseName");

        return courseRepository.findCourseByCourseNameContainingIgnoringCase(page, courseName);
    }

    @Transactional
    @Override
    public Course addCourse(String courseName, Course.CourseLevel courseLevel) throws DuplicateCourseException {
        checkNotNull(courseName, "courseName");
        checkNotNull(courseLevel, "courseLevel");
        if(courseRepository.findCourseByCourseName(courseName) != null) {
            throw new DuplicateCourseException(courseName, Course.class.getName());
        }
        Course c = Course.builder().courseName(courseName).courseLevel(courseLevel).build();
        return courseRepository.save(c);
    }

    @Transactional
    @Override
    public Course createCourse(CourseCreateForm courseForm) throws DuplicateCourseException {
        checkNotNull(courseForm, "courseForm");
        if(courseRepository.findCourseByCourseName(courseForm.getCourseName()) != null ){
            throw new DuplicateCourseException(courseForm.getCourseName(), Course.class.getName());
        }
        return courseRepository.save(Course.builder().courseName(courseForm.getCourseName())
                .courseLevel(courseForm.getCourseLevel()).build());
    }

    @Transactional
    @Override
    public Page<Course> findCourseByCourseLevel(Pageable page, Course.CourseLevel courseLevel) {
        checkNotNull(page, "page");
        checkNotNull(courseLevel, "courseLevel");

        return courseRepository.findCourseByCourseLevel(page, courseLevel);
    }

    @Transactional
    @Override
    public void deleteCourse(Long courseId) {
        Course c = courseRepository.findCourseByCourseId(courseId);
        Page<ClassGroup> page = classGroupRepository.findClassGroupByCourse(new PageRequest(0, 5), c.getCourseId());
        if(page.getNumberOfElements() != 0) {
            throw new DataIntegrityViolationException("Existen grupos con este curso en uso.");
        } else {
            courseRepository.delete(checkNotNull(courseId, "courseId"));
        }
    }

    @Transactional
    @Override
    public Course updateCourse(Long courseId, CourseCreateForm courseForm) throws CourseNotFoundException {
        checkNotNull(courseId, "courseId");

        Course c = courseRepository.findCourseByCourseId(courseId);
        if (c == null) {
            throw new CourseNotFoundException(courseId, Course.class.getName());
        }
        else {
            c.setCourseLevel(courseForm.getCourseLevel());
            c.setCourseName(courseForm.getCourseName());
            return c;
        }
    }

    /* * *
     *  SchoolYear Operations
     * * */

    @Transactional
    @Override
    public Page<SchoolYear> listSchoolYear(Pageable page) {
        return schoolYearRepository.findAll(checkNotNull(page, "page"));
    }

    @Transactional
    @Override
    public SchoolYear findSchoolYearBySchoolYearId(Long schoolYearId) throws SchoolYearNotFoundException {

        SchoolYear sy = schoolYearRepository.findSchoolYearBySchoolYearId(checkNotNull(schoolYearId, "schoolYearId"));
        if(sy == null) {
            throw new SchoolYearNotFoundException(schoolYearId, "schoolYearName");
        }
        else {
            return sy;
        }
    }

    @Transactional
    @Override
    public SchoolYear findSchoolYearBySchoolYearName(String schoolYearName) throws SchoolYearNotFoundException {
        SchoolYear sy = schoolYearRepository.findBySchoolYearName(checkNotNull(schoolYearName, "schoolYearName"));
        if(sy == null) {
            throw new SchoolYearNotFoundException(schoolYearName, "schoolYearName");
        }
        else {
            return sy;
        }
    }

    @Transactional
    @Override
    public SchoolYear addSchoolYear(String schoolYearName) throws DuplicateSchoolYearException {
        if(schoolYearRepository.findBySchoolYearName(checkNotNull(schoolYearName, "schoolYearName")) != null) {
            throw new DuplicateSchoolYearException(schoolYearName, SchoolYear.class.getName());
        }
        return schoolYearRepository.save(SchoolYear.builder().schoolYearName(schoolYearName).build());
    }

    @Transactional
    @Override
    public void deleteSchoolYear(Long schoolYearId) throws SchoolYearNotFoundException {
        checkNotNull(schoolYearId, "schoolYearId");

        SchoolYear sy = schoolYearRepository.findSchoolYearBySchoolYearId(checkNotNull(schoolYearId, "schoolYearId"));
        if(sy == null) {
            throw new SchoolYearNotFoundException(schoolYearId, "schoolYearName");
        }
        else {
                schoolYearRepository.delete(sy);
        }
    }

    @Transactional
    @Override
    public void setSchoolYearVisible(Long schoolYearId) throws SchoolYearNotFoundException {
        checkNotNull(schoolYearId, "schoolYearId");

        SchoolYear sy = schoolYearRepository.findSchoolYearBySchoolYearId(checkNotNull(schoolYearId, "schoolYearId"));
        if(sy == null) {
            throw new SchoolYearNotFoundException(schoolYearId, "schoolYearName");
        }
        else {
            sy.setVisible(true);
        }
    }


    @Transactional
    @Override
    public void hideSchoolYear(Long schoolYearId) throws SchoolYearNotFoundException {
        checkNotNull(schoolYearId, "schoolYearId");

        SchoolYear sy = schoolYearRepository.findSchoolYearBySchoolYearId(checkNotNull(schoolYearId, "schoolYearId"));
        if(sy == null) {
            throw new SchoolYearNotFoundException(schoolYearId, "schoolYearName");
        }
        else {
            sy.setVisible(false);
        }
    }

    @Transactional
    @Override
    public List<SchoolYear> findVisibleSchoolYear() {

        return schoolYearRepository.findSchoolYearByVisible(true);
    }

    /* * *
     *  ClassGroup Operations
     * * */


    @Transactional
    @Override
    public Page<ClassGroup> listClassGroup(Pageable page) {
        return classGroupRepository.findAll(checkNotNull(page, "page"));
    }

    @Transactional
    @Override
    public List<ClassGroup> listClassGroup() {
        return classGroupRepository.findAll();
    }


    @Transactional
    @Override
    public List<ClassGroup> listClassGroupByXade() {
        return classGroupRepository.findExportableClassGroup();
    }

    @Transactional
    @Override
    public ClassGroup findClassGroupById(Long classGroupId) throws ClassGroupNotFoundException {
        checkNotNull(classGroupId, "classGroupId");

        ClassGroup classGroup = classGroupRepository.findClassGroupByClassGroupId(classGroupId);
        if (classGroup == null) {
            throw new ClassGroupNotFoundException(classGroupId, ClassGroup.class.getName());
        }
        else {
            return classGroup;
        }
    }

    @Transactional
    @Override
    public ClassGroup findClassGroupByClassGroupName(String classGroupName) throws ClassGroupNotFoundException {
        ClassGroup cg = classGroupRepository.findClassGroupByClassGroupName(
                checkNotNull(classGroupName, "classGroupName"));
        if (cg == null) {
            throw new ClassGroupNotFoundException(classGroupName, ClassGroup.class.getName());
        }
        else {
            return cg;
        }
    }

    @Transactional
    @Override
    public ClassGroup addClassGroup(String classGroupName, Long courseId, User tutor, Long schoolYearId,
                                    String codigoGrupo)
            throws DuplicateClassGroupException {
        checkNotNull(classGroupName, "classGroupName");
        checkNotNull(courseId,"courseId");
        checkNotNull(tutor,"tutor");
        checkNotNull(schoolYearId,"schoolYearId");

        Course course = courseRepository.findCourseByCourseId(courseId);
        SchoolYear schoolYear = schoolYearRepository.findSchoolYearBySchoolYearId(schoolYearId);
        if(classGroupRepository.findClassGroupByClassGroupNameAndSchoolYear(classGroupName,
                schoolYearId) != null) {
            throw new DuplicateClassGroupException(classGroupName, ClassGroup.class.getName());
        }
        Set<Student> studentList = new HashSet<Student>();
        ClassGroup newClassGroup = ClassGroup.builder().classGroupName(classGroupName).course(course).tutor(tutor)
                .schoolYear(schoolYear).codigoGrupo(codigoGrupo).studentList(studentList).build();
        return classGroupRepository.save(newClassGroup);
    }

    @Transactional
    @Override
    public Page<ClassGroup> findClassGroupByCourse(Pageable page, Long courseId) {
        checkNotNull(page, "page");
        checkNotNull(courseId, "course");

        return classGroupRepository.findClassGroupByCourse(page, courseId);
    }

    @Transactional
    @Override
    public Page<ClassGroup> findClassGroupByTutor(Pageable page, Long userId) {
        return classGroupRepository.findClassGroupByTutor(checkNotNull(page, "page"),
                checkNotNull(userId, "userId"));
    }

    @Transactional
    @Override
    public void updateClassGroupTutor(Long classGroupId, User tutor) {
        checkNotNull(classGroupId, "classGroup");
        checkNotNull(tutor, "tutor");

        ClassGroup classGroup = classGroupRepository.findClassGroupByClassGroupId(classGroupId);
        classGroup.setTutor(tutor);
    }

    @Transactional
    @Override
    public ClassGroup updateClassGroup(Long classGroupId, String classGroupName, Course course, User tutor,
                                       SchoolYear schoolYear, String codigoGrupo) throws ClassGroupNotFoundException {
        checkNotNull(classGroupId, "ClassGroupId");
        checkNotNull(classGroupName, "classGroupName");
        checkNotNull(course, "course");
        checkNotNull(tutor, "tutor");
        checkNotNull(schoolYear, "schoolYear");

        ClassGroup cl = classGroupRepository.findClassGroupByClassGroupId(classGroupId);
        cl.setClassGroupName(classGroupName);
        cl.setCourse(course);
        cl.setTutor(tutor);
        cl.setSchoolYear(schoolYear);
        cl.setCodigoGrupo(codigoGrupo);
        return cl;
    }

    @Transactional
    @Override
    public void deleteClassGroup(Long classGroupId) throws ClassGroupNotFoundException {
        checkNotNull(classGroupId, "classGroup");

        ClassGroup classGroup = classGroupRepository.findClassGroupByClassGroupId(classGroupId);
        if (classGroup == null) {
            throw new ClassGroupNotFoundException(classGroupId, ClassGroup.class.getName());
        }
        else {
            classGroupRepository.delete(classGroup);
        }
    }

    /* * *
     *  Schedule Operations
     * * */

    @Transactional
    @Override
    public Page<Schedule> listSchedule(Pageable page){
        checkNotNull(page, "page");
        return scheduleRepository.findAll(page);
    }

    @Transactional
    @Override
    public Schedule findScheduleById(Long scheduleId) throws ScheduleNotFoundException {
        checkNotNull(scheduleId, "scheduleId");

        Schedule schedule = scheduleRepository.findScheduleByScheduleId(scheduleId);
        if (schedule == null) {
            throw new ScheduleNotFoundException(scheduleId, "scheduleId");
        } else {
            return schedule;
        }
    }

    @Transactional
    @Override
    public Schedule findSchedule(Long classGroupId, Long subjectId, Schedule.WeekDay day, ClassHourLevel hour)
            throws ScheduleNotFoundException {
        checkNotNull(classGroupId, "classGroup");
        checkNotNull(subjectId, "subject");
        checkNotNull(day, "day");
        checkNotNull(hour, "hour");

        Schedule s = scheduleRepository.findByClassGroupAndSubjectAndWeekDayAndClassHour(classGroupId,
                subjectId, day, hour);
        if (s == null) {
            throw new ScheduleNotFoundException(classGroupId,"Schedule");
        } else {
            return s;
        }
    }

    @Transactional
    @Override
    public Schedule addSchedule(ClassGroup classGroup, Subject subject, Schedule.WeekDay day, ClassHourLevel hour)
            throws DuplicateScheduleException {
        checkNotNull(classGroup, "classGroup");
        checkNotNull(subject, "subject");
        checkNotNull(day, "day");
        checkNotNull(hour, "hour");

        Schedule sch = scheduleRepository.findByClassGroupAndSubjectAndWeekDayAndClassHour(classGroup.getClassGroupId(),
                subject.getSubjectId(), day, hour);
        if( sch != null){
            throw new DuplicateScheduleException(sch,"Schedule");
        }
        Schedule newSchedule = Schedule.builder().classGroup(classGroup).subject(subject).weekDay(day).classHour(hour)
                .build();
        return scheduleRepository.save(newSchedule);
    }

    @Transactional
    @Override
    public List<Schedule> findScheduleByGroup(Long classGroupId){
        checkNotNull(classGroupId, "classGroup");

        return scheduleRepository.findByClassGroup(classGroupId);
    }

    @Transactional
    @Override
    public List<Schedule> findStudentScheduleByGroupAndWeekDay(Long classGroupId, Schedule.WeekDay weekDay) {
        checkNotNull(classGroupId, "classGroup");
        checkNotNull(weekDay, "weekDay");

        if(weekDay.equals(Schedule.WeekDay.SUNDAY) || weekDay.equals(Schedule.WeekDay.SATURDAY)) {
            return null;
        }
        return scheduleRepository.findScheduleByClassGroupAndWeekDay(classGroupId, weekDay);
    }

    @Transactional
    @Override
    public Page<Schedule> findScheduleBySubject(Pageable page, Long subjectId){
        checkNotNull(page, "page");
        checkNotNull(subjectId, "subject");
        return scheduleRepository.findBySubject(page, subjectId);
    }

    @Transactional
    @Override
    public void deleteSchedule(Long scheduleId) throws ScheduleNotFoundException {
        checkNotNull(scheduleId,"schedule");

        Schedule schedule = scheduleRepository.findScheduleByScheduleId(scheduleId);
        if (schedule == null) {
            throw new ScheduleNotFoundException(scheduleId, "scheduleId");
        } else {
            scheduleRepository.delete(schedule);
        }
    }

    /*
     * ViewTeacherSchedule VIEWS
     */

    @Transactional
    @Override
    public List<ViewTeacherSchedule> findTeacherSchedule(Long teacherId) {
        checkNotNull(teacherId,"teacherId");

        return viewTeacherScheduleRepository.findByTeacher(teacherId);
    }

    @Transactional
    @Override
    public List<ViewTeacherSchedule> findTeacherScheduleAndWeekDay(Long teacherId, Schedule.WeekDay day) {
        checkNotNull(teacherId,"teacherId");
        checkNotNull(day,"day");

        if(day.equals(Schedule.WeekDay.SUNDAY) || day.equals(Schedule.WeekDay.SATURDAY)) {
            return null;
        }
        return viewTeacherScheduleRepository.findByTeacherAndWeekDay(teacherId, day);
    }

    /*
     * GroupSubject Operations
     */

    @Transactional
    @Override
    public GroupSubject findGroupSubjectById(Long groupSubjectId) throws GroupSubjectNotFoundException {
        checkNotNull(groupSubjectId, "groupSubjectId");

        GroupSubject groupSubject = groupSubjectRepository.findByGroupSubjectId(groupSubjectId);
        if (groupSubject == null) {
            throw new GroupSubjectNotFoundException(groupSubjectId, "GroupSubjectId not found");
        } else {
            return groupSubject;
        }
    }

    @Transactional
    @Override
    public List<GroupSubject> findGroupSubjectByTeacher(Long teacherId){
        checkNotNull(teacherId, "teacher");

        return groupSubjectRepository.findByTeacher(teacherId);
    }

    @Transactional
    @Override
    public List<GroupSubject> findGroupSubjectByClassGroup(Long classGroupId) {
        checkNotNull(classGroupId, "classGroup");

        return groupSubjectRepository.findByClassGroup(classGroupId);
    }

    @Transactional
    @Override
    public GroupSubject addGroupSubject(ClassGroup classGroup, Subject subject, User teacher)
            throws DuplicateGroupSubjectException {
        checkNotNull(classGroup, "classGroup");
        checkNotNull(subject, "subject");
        checkNotNull(teacher, "teacher");

        GroupSubject gs = groupSubjectRepository.findByClassGroupAndSubject(classGroup.getClassGroupId(),
                subject.getSubjectId());
        if (gs != null){
            throw new DuplicateGroupSubjectException(subject, "Duplicate subject");
        }
        GroupSubject newGroupSubject = GroupSubject.builder().classGroup(classGroup).subject(subject).teacher(teacher)
                .build();
        return groupSubjectRepository.save(newGroupSubject);
    }

    @Transactional
    @Override
    public GroupSubject updateGroupSubject(Long groupSubjectId, GroupSubjectCreateForm form)
            throws GroupSubjectNotFoundException {
        checkNotNull(groupSubjectId, "groupSubjectId");

        GroupSubject gs = groupSubjectRepository.findByGroupSubjectId(groupSubjectId);
        if (gs == null){
            throw new GroupSubjectNotFoundException(groupSubjectId, "GroupSubject Not found");
        }
        else {
            gs.setSubject(form.getSubject());
            gs.setTeacher(form.getTeacher());
            return gs;
        }
    }

    @Transactional
    @Override
    public void deleteGroupSubject(Long groupSubjectId) throws GroupSubjectNotFoundException {
        checkNotNull(groupSubjectId, "groupSubjectId");
        GroupSubject gs = groupSubjectRepository.findByGroupSubjectId(groupSubjectId);
        if (gs == null) {
            throw new GroupSubjectNotFoundException(groupSubjectId, "GroupSubject Not Found");
        } else {
            groupSubjectRepository.delete(groupSubjectId);
        }
    }

    @Transactional
    @Override
    public GroupSubject findGroupSubjectByClassGroupAndSubject(Long classGroupId, Long subjectId)
            throws ClassGroupNotFoundException {
        checkNotNull(classGroupId, "classGroupId");
        checkNotNull(subjectId, "subjectId");

        ClassGroup cg = classGroupRepository.findClassGroupByClassGroupId(classGroupId);
        if (cg == null) {
            throw new ClassGroupNotFoundException(classGroupId, "ClassGroup Not Found");
        }
        else {
            return groupSubjectRepository.findByClassGroupAndSubject(cg.getClassGroupId(), subjectId);
        }
    }

    @Transactional
    @Override
    public Set<User> findTeacherByClassGroup(Long classGroupId) throws ClassGroupNotFoundException {
        checkNotNull(classGroupId, "classGroupId");

        ClassGroup cg = classGroupRepository.findClassGroupByClassGroupId(classGroupId);
        if (cg == null) {
            throw new ClassGroupNotFoundException(classGroupId, "ClassGroup Not Found");
        }
        else {
            List<GroupSubject> groupSubjectList = groupSubjectRepository.findByClassGroup(cg.getClassGroupId());
            Iterator groupSubjectIterator = groupSubjectList.iterator();
            Set<User> teacherList = new HashSet<>();
            while(groupSubjectIterator.hasNext()) {
                GroupSubject gs = (GroupSubject)groupSubjectIterator.next();
                teacherList.add(gs.getTeacher());
            }
            return teacherList;
        }
    }

    /*
     * ClassHourLevel Operations
     */

    @Transactional
    @Override
    public ClassHourLevel addClassHourLevel(Course.CourseLevel courseLevel, ClassHourLevel.ClassHour classHour,
                                            String classStart, String classEnd) throws DuplicateClassHourLevelException {
        checkNotNull(courseLevel, "courseLevel");
        checkNotNull(classHour, "classHour");
        checkNotNull(classStart, "classStart");
        checkNotNull(classEnd, "classEnd");

        ClassHourLevel clh = classHourLevelRepository.findByCourseLevelAndClassHour(courseLevel, classHour);
        if (clh != null) {
            throw new DuplicateClassHourLevelException(clh, "ClassHourLevel");
        }
        ClassHourLevel newCLH = ClassHourLevel.builder().courseLevel(courseLevel).classHour(classHour)
                .classStart(classStart).classEnd(classEnd).build();
        return classHourLevelRepository.save(newCLH);
    }

    @Transactional
    @Override
    public Page<ClassHourLevel> listClassHourLevel(Pageable page) {
        checkNotNull(page, "page");
        return classHourLevelRepository.findAll(page);
    }

    @Transactional
    @Override
    public Page<ClassHourLevel> findByCourseLevel(Pageable page, Course.CourseLevel courseLevel) {
        checkNotNull(page,"page");
        checkNotNull(courseLevel, "courseLevel");
        return classHourLevelRepository.findByCourseLevel(page, courseLevel);
    }

    @Transactional
    @Override
    public List<ClassHourLevel> findClassHourLevelByCourseLevel(Course.CourseLevel courseLevel) {
        checkNotNull(courseLevel, "courseLevel");

        return classHourLevelRepository.findClassHourByCourseLevel(courseLevel);
    }

    @Transactional
    @Override
    public void deleteClassHourLevel(Long classHourLevelId) {
        classHourLevelRepository.delete(classHourLevelId);
    }
}