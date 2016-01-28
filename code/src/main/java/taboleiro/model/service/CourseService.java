package taboleiro.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.controller.course.CourseCreateForm;
import taboleiro.controller.subject.GroupSubjectCreateForm;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.*;
import taboleiro.model.domain.course.Course.CourseLevel;
import java.util.Set;
import java.util.List;

public interface CourseService {

    /*
     * Course Operations
     */

    Page<Course> listCourse(Pageable page);

    List<Course> listCourse();

    Course findCourseById(Long courseId) throws CourseNotFoundException;

    Course findCourseByCourseName (String courseName) throws CourseNotFoundException;

    Page<Course> findCourseByPartialCourseName(Pageable page, String courseName);

    Page<Course> findCourseByCourseLevel(Pageable page, Course.CourseLevel courseLevel);

    Course addCourse(String courseName, Course.CourseLevel courseLevel)throws DuplicateCourseException;

    Course createCourse(CourseCreateForm courseForm) throws DuplicateCourseException;

    Course updateCourse(Long courseId, CourseCreateForm courseForm) throws CourseNotFoundException;

    void deleteCourse(Long courseId);

    /*
     * SchoolYear Operations
     */

    Page<SchoolYear> listSchoolYear(Pageable page);

    SchoolYear findSchoolYearBySchoolYearId(Long schoolYearId) throws SchoolYearNotFoundException;

    SchoolYear findSchoolYearBySchoolYearName(String schoolYearName) throws SchoolYearNotFoundException;

    SchoolYear addSchoolYear(String schoolYearName) throws DuplicateSchoolYearException;

    void deleteSchoolYear(Long schoolYearId) throws SchoolYearNotFoundException;

    void setSchoolYearVisible(Long schoolYearId) throws SchoolYearNotFoundException;

    void hideSchoolYear(Long schoolYearId) throws SchoolYearNotFoundException;

    List<SchoolYear> findVisibleSchoolYear();


    /*
     * ClassGroup Operations
     */

    Page<ClassGroup> listClassGroup(Pageable page);

    List<ClassGroup> listClassGroup();

    List<ClassGroup> listClassGroupByXade();

    ClassGroup findClassGroupById(Long classGroupId) throws ClassGroupNotFoundException;

    ClassGroup findClassGroupByClassGroupName(String classGroupName) throws ClassGroupNotFoundException;

    ClassGroup addClassGroup(String classGroupName, Long course, User tutor, Long schoolYear, String codigoGrupo)
            throws DuplicateClassGroupException;

    Page<ClassGroup> findClassGroupByCourse(Pageable page, Long courseId);

    Page<ClassGroup> findClassGroupByTutor(Pageable page, Long userId);

    void updateClassGroupTutor(Long classGroupId, User tutor) throws ClassGroupNotFoundException,
            UserNotFoundException;

    ClassGroup updateClassGroup(Long classGroupId, String ClassGroupName, Course course, User tutor, SchoolYear year,
                                String codigoGrupo) throws ClassGroupNotFoundException;

    void deleteClassGroup(Long classGroupId) throws ClassGroupNotFoundException;

    /* * *
     *  Schedule Operations
     * * */

    Page<Schedule> listSchedule(Pageable page);

    Schedule findScheduleById(Long scheduleId) throws ScheduleNotFoundException;

    Schedule addSchedule(ClassGroup classGroup, Subject subject, Schedule.WeekDay day, ClassHourLevel hour)
            throws DuplicateScheduleException;

    Schedule findSchedule(Long classGroupId, Long subjectId, Schedule.WeekDay day, ClassHourLevel hour)
            throws ScheduleNotFoundException;

    List<Schedule> findScheduleByGroup(Long classGroupId);

    List<Schedule> findStudentScheduleByGroupAndWeekDay(Long classGroupId, Schedule.WeekDay day);

    Page<Schedule> findScheduleBySubject(Pageable page, Long subjectId);

    void deleteSchedule(Long scheduleId) throws ScheduleNotFoundException;

    /*
     * ViewTeacherSchedule Views
     */

    List<ViewTeacherSchedule> findTeacherSchedule(Long teacherId);

    List<ViewTeacherSchedule> findTeacherScheduleAndWeekDay(Long teacherId, Schedule.WeekDay day);

    /*
     * GroupSubject Operations
     */

    GroupSubject findGroupSubjectById(Long groupSubject)  throws GroupSubjectNotFoundException;

    List<GroupSubject> findGroupSubjectByTeacher(Long teacherId);

    List<GroupSubject> findGroupSubjectByClassGroup(Long classGroupId);

    GroupSubject addGroupSubject(ClassGroup classGroup, Subject subject, User teacher)
            throws DuplicateGroupSubjectException;

    GroupSubject updateGroupSubject(Long groupSubjectId, GroupSubjectCreateForm form)
            throws GroupSubjectNotFoundException;

    void deleteGroupSubject(Long groupSubjectId) throws GroupSubjectNotFoundException;

    GroupSubject findGroupSubjectByClassGroupAndSubject(Long classGroupId, Long subjectId)
            throws ClassGroupNotFoundException;

    Set<User> findTeacherByClassGroup(Long classGroupId) throws ClassGroupNotFoundException;

    /*
     * ClassHourLevel Operations
     */

    ClassHourLevel addClassHourLevel(CourseLevel courseLevel, ClassHourLevel.ClassHour classHour,
                                     String classStart, String classEnd) throws DuplicateClassHourLevelException;

    Page<ClassHourLevel> listClassHourLevel(Pageable page);

    Page<ClassHourLevel> findByCourseLevel(Pageable page, CourseLevel courseLevel);

    List<ClassHourLevel> findClassHourLevelByCourseLevel(CourseLevel courseLevel);

    void deleteClassHourLevel(Long classHourLevelId);

}