package taboleiro.controller.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import taboleiro.controller.utils.PaginationUtil;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.student.GlobalGradeLevel;
import taboleiro.model.exception.*;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.MessageService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.service.user.UserService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.Set;

@Controller
@RequestMapping("/admin/course")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final MessageService messageService;
    private final PaginationUtil paginationUtil;

    @Autowired
    public CourseController(CourseService courseService, UserService userService, StudentService studentService,
                            SubjectService subjectService, MessageService messageService, PaginationUtil paginationUtil) {
        this.courseService = courseService;
        this.userService = userService;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.messageService = messageService;
        this.paginationUtil = paginationUtil;
    }

    /*
     *  Course Views
     */

    @RequestMapping("/list")
    public String courseList(Model model, HttpServletRequest request) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        return "admin/course/courseList";
    }

    @RequestMapping(value = "/addCourse", method = RequestMethod.POST)
    public String createCourse(@Valid CourseCreateForm form, BindingResult bindingResult) throws DuplicateCourseException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/list";
        }
        else {
            courseService.addCourse(form.getCourseName(), form.getCourseLevel());
            return "redirect:/admin/course/list";
        }
    }

    @RequestMapping(value = "/deleteCourse/{courseId}", method = RequestMethod.GET)
    public String deleteCourse(@PathVariable("courseId") Long courseId, HttpServletRequest request, Model model)
            throws CourseNotFoundException, UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        courseService.deleteCourse(courseService.findCourseById(courseId).getCourseId());
        return "redirect:/admin/course/list";
    }

    @RequestMapping(value = "/updateCourse/{courseId}", method = RequestMethod.GET)
    public String showUpdateCourse(@PathVariable("courseId") Long courseId, HttpServletRequest request, Model model)
            throws UserNotFoundException, CourseNotFoundException {

        Course c = courseService.findCourseById(courseId);
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        model.addAttribute("course", c);
        return "admin/course/courseEdit";
    }

    @RequestMapping(value = "/updateCourse/{courseId}", method = RequestMethod.POST)
    public String updateCourse(@PathVariable("courseId") Long courseId, @Valid CourseCreateForm form,
                               BindingResult bindingResult)
            throws CourseNotFoundException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/list";
        }
        else {
            courseService.updateCourse(courseId, form);
            return "redirect:/admin/course/list";
        }

    }

    /*
     * ClassGroup Views
     */

    @RequestMapping(value ="/createGroup", method = RequestMethod.GET)
    public String showCreateForm(Model model, HttpServletRequest request) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        Page<User> userList = userService.findUserByRole(new PageRequest(0,20), User.Role.TEACHER);
        model.addAttribute("userList", userList.getContent());
        Page<SchoolYear> schoolYearList = courseService.listSchoolYear(new PageRequest(0, 20));
        model.addAttribute("schoolYearList", schoolYearList.getContent());
        return "admin/course/createGroup";
    }

    @RequestMapping(value = "/createGroup", method = RequestMethod.POST)
    public String processCreateGroupForm(@Valid GroupForm groupForm, BindingResult bindingResult) throws DuplicateClassGroupException{

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/list";
        }
        else {
            courseService.addClassGroup(groupForm.getClassGroupName(), groupForm.getCourse().getCourseId(),
                    groupForm.getTutor(), groupForm.getSchoolYear().getSchoolYearId(), groupForm.getCodigoGrupo());
            return "redirect:/admin/course/group";
        }
    }

    @RequestMapping("/group/{courseId}")
    public String classGroupByCourseList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @PathVariable("courseId") long courseId, HttpServletRequest request,
            Model model) throws CourseNotFoundException, UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Course c = courseService.findCourseById(courseId);
        Page<ClassGroup> classGroupList = courseService.findClassGroupByCourse(new PageRequest(page, 10),
                c.getCourseId());
        model.addAttribute("classGroupList", classGroupList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(classGroupList));
        model.addAttribute("Page", paginationUtil.getNextPage(classGroupList));
        return "admin/course/groupList";
    }

    @RequestMapping("/group")
    public String classGroupList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model, HttpServletRequest request) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        Page<User> userList = userService.findUserByRole(new PageRequest(0,20), User.Role.TEACHER);
        model.addAttribute("userList", userList.getContent());
        Page<SchoolYear> schoolYearList = courseService.listSchoolYear(new PageRequest(0, 20));
        model.addAttribute("schoolYearList", schoolYearList.getContent());
        Page<ClassGroup> classGroupList = courseService.listClassGroup(new PageRequest(page, 10));
        model.addAttribute("classGroupList", classGroupList);
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(classGroupList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(classGroupList));

        return "admin/course/groupList";
    }

    @RequestMapping(value = "/groupEdit/{classGroupId}", method = RequestMethod.GET)
    public String showGroupEditForm(@PathVariable("classGroupId") long classGroupId,  HttpServletRequest request,
                                    Model model) throws ClassGroupNotFoundException, UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        Page<User> userList = userService.findUserByRole(new PageRequest(0, 20), User.Role.TEACHER);
        model.addAttribute("userList", userList.getContent());
        Page<SchoolYear> schoolYearList = courseService.listSchoolYear(new PageRequest(0, 20));
        model.addAttribute("schoolYearList", schoolYearList.getContent());
        model.addAttribute("classGroup", courseService.findClassGroupById(classGroupId));
        return "admin/course/groupEdit";
    }

    @RequestMapping(value = "/groupEdit/{classGroupId}", method = RequestMethod.POST)
    public String editClassGroup( @PathVariable("classGroupId") Long classGroupId, @Valid GroupForm groupForm,
                                    Model model, BindingResult bindingResult)
            throws ClassGroupNotFoundException, SchoolYearNotFoundException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/groupEdit/"+classGroupId;
        }
        else {
            model.addAttribute("classGroup", courseService.findClassGroupById(classGroupId));
            model.addAttribute("form", groupForm);
            SchoolYear sy = groupForm.getSchoolYear();
            courseService.updateClassGroup(classGroupId, groupForm.getClassGroupName(), groupForm.getCourse(),
                    groupForm.getTutor(), sy, groupForm.getCodigoGrupo());
            return "redirect:/admin/course/group";
        }
    }

    @RequestMapping(value = "/deleteGroup/{classGroupId}", method = RequestMethod.POST)
    public String deleteClassGroup(@PathVariable("classGroupId") long classGroupId) throws ClassGroupNotFoundException {

        courseService.deleteClassGroup(classGroupId);
        return "redirect:/admin/course/group";
    }

    @RequestMapping(value = "/studentList/{classGroupId}", method = RequestMethod.GET)
    public String subjectStudentList(@PathVariable("classGroupId") long classGroupId, HttpServletRequest request,
                                     Model model) throws UserNotFoundException, ClassGroupNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        ClassGroup cg = courseService.findClassGroupById(classGroupId);
        model.addAttribute("classGroup", cg);
        Set<Student> studentList = cg.getStudentList();
        model.addAttribute("studentList", studentList);
        return "admin/course/studentList";
    }

    @RequestMapping(value = "/addStudent", method = RequestMethod.POST)
    public String addStudentToGroup(AddStudentToGroupForm form) throws ClassGroupNotFoundException,
            StudentNotFoundException, CourseNotFoundException {

        Long classGroupId = form.getClassGroup().getClassGroupId();
        studentService.enrollStudent(classGroupId, form.getStudent().getStudentId());
        return "redirect:/admin/course/studentList/"+classGroupId;
    }

    @RequestMapping(value = "/unenroll/{classGroupId}/student/{studentId}", method = RequestMethod.POST)
    public String unenroll(@PathVariable("classGroupId") Long classGroupId,
                           @PathVariable("studentId") Long studentId) throws ClassGroupNotFoundException,
            StudentNotFoundException{

        Student s = studentService.findStudentById(studentId);
        ClassGroup cg = courseService.findClassGroupById(classGroupId);
        studentService.unenrollStudent(cg.getClassGroupId(), s.getStudentId());
        return "redirect:/admin/course/studentList/"+classGroupId;
    }

    /*
     *  SchoolYear Views
     */

    @RequestMapping("/schoolYearList")
    public String schoolYearList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "amount", defaultValue = "20") int amount,
            Model model, HttpServletRequest request) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<SchoolYear> schoolYearList = courseService.listSchoolYear(new PageRequest(page, amount));
        model.addAttribute("schoolYearList", schoolYearList.getContent());
        return "admin/course/schoolYearList";
    }

    @RequestMapping(value="/schoolYear/show/{schoolYearId}", method = RequestMethod.POST)
    public String setVisibleSchoolYear(@PathVariable("schoolYearId") long schoolYearId)
            throws SchoolYearNotFoundException {

        courseService.setSchoolYearVisible(schoolYearId);
        return "redirect:/admin/course/schoolYearList";
    }

    @RequestMapping(value="/schoolYear/hide/{schoolYearId}", method = RequestMethod.POST)
    public String hideSchoolYear(@PathVariable("schoolYearId") long schoolYearId)
            throws SchoolYearNotFoundException {

        courseService.hideSchoolYear(schoolYearId);
        return "redirect:/admin/course/schoolYearList";
    }

    @RequestMapping(value = "/createSchoolYearForm", method = RequestMethod.POST)
    public String processSchoolYearForm(@Valid SchoolYearCreateForm form, BindingResult bindingResult)
            throws DuplicateSchoolYearException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/schoolYearList";
        }
        else {
            courseService.addSchoolYear(form.getSchoolYearName());
            return "redirect:/admin/course/schoolYearList";
        }
    }

    @RequestMapping(value = "/deleteSchoolYear/{schoolYearId}", method = RequestMethod.POST)
    public String deleteSchoolYearForm(@PathVariable("schoolYearId") Long schoolYearId)
            throws SchoolYearNotFoundException {

        SchoolYear sh = courseService.findSchoolYearBySchoolYearId(schoolYearId);
        courseService.deleteSchoolYear(sh.getSchoolYearId());
        return "redirect:/admin/course/schoolYearList";
    }

    /*
     * Schedule Views
     */

    @RequestMapping(value = "/scheduleGroup")
    public String showScheduleGroup(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model, HttpServletRequest request) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<ClassGroup> classGroupList = courseService.listClassGroup(new PageRequest(page, 15));
        model.addAttribute("classGroupList", classGroupList);
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(classGroupList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(classGroupList));
        return "admin/course/scheduleGroupList";
    }

    @RequestMapping(value = "/schedule/{classGroupId}")
    public String showSchedule(
            @PathVariable("classGroupId") long classGroupId, Model model, HttpServletRequest request)
            throws ScheduleNotFoundException, ClassGroupNotFoundException, CourseNotFoundException,
            UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        ClassGroup cl = courseService.findClassGroupById(classGroupId);
        model.addAttribute("classGroup", cl);
        List<Subject> subjectList = subjectService.findSubjectByCourse(cl.getCourse().getCourseId());
        model.addAttribute("subjectList", subjectList);
        List <Schedule> scheduleList = courseService.findScheduleByGroup(cl.getClassGroupId());
        model.addAttribute("scheduleList", scheduleList);
        List<ClassHourLevel> classHourList = courseService.findClassHourLevelByCourseLevel(
                cl.getCourse().getCourseLevel());
        model.addAttribute("classHourList", classHourList);
        return "admin/course/schedule";
    }

    @RequestMapping(value = "/addSchedule", method = RequestMethod.POST)
    public String processScheduleForm(@Valid ScheduleForm form, BindingResult bindingResult) throws DuplicateScheduleException,
            SubjectNotFoundException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/schoolYearList";
        }
        else {
            Long classGroupId = form.getClassGroup().getClassGroupId();
            courseService.addSchedule(form.getClassGroup(), form.getSubject(),
                    form.getWeekDay(), form.getClassHour());
            return "redirect:/admin/course/schedule/" + classGroupId;
        }
    }

    @RequestMapping(value = "/deleteSchedule/{scheduleId}", method = RequestMethod.GET)
    public String deleteSchedule(@PathVariable("scheduleId") long scheduleId, HttpServletRequest request, Model model)
            throws ScheduleNotFoundException, UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Long classGroupId = courseService.findScheduleById(scheduleId).getClassGroup().getClassGroupId();
        courseService.deleteSchedule(scheduleId);
        return "redirect:/admin/course/schedule/"+classGroupId;
    }

    /*
     * ClassHourLevel Views
     */

    @RequestMapping(value = "/classHourLevelList", method = RequestMethod.GET)
    public String showClassHourLevelList(HttpServletRequest request, Model model) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));

        Page<ClassHourLevel> classHourLevelList = courseService.listClassHourLevel(new PageRequest(0,30));
        model.addAttribute(classHourLevelList.getContent());
        return "admin/course/classHourLevelList";
    }

    @RequestMapping(value = "/addClassHourLevel", method = RequestMethod.POST)
    public String addClassHourLevel(@Valid ClassHourLevelForm form, BindingResult bindingResult)
            throws DuplicateClassHourLevelException{

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/schoolYearList";
        }
        else {
            courseService.addClassHourLevel(form.getCourseLevel(), form.getClassHour(), form.getClassStart(),
                    form.getClassEnd());
            return "redirect:/admin/course/classHourLevelList";
        }
    }

    @RequestMapping(value = "/deleteClassHourLevel/{classHourLevelId}", method = RequestMethod.POST)
    public String deleteClassHourLevel(@PathVariable("classHourLevelId") Long classHourLevelId)
            throws DuplicateClassHourLevelException{
        courseService.deleteClassHourLevel(classHourLevelId);
        return "redirect:/admin/course/classHourLevelList";
    }

    /*
     * GlobalGrade Level
     */

    @RequestMapping("/globalGradeLevelList")
    public String showGlobalGradeLevel(@RequestParam(value = "page", defaultValue = "0") int page,
                                       Model model, HttpServletRequest request) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<GlobalGradeLevel> list = studentService.findAllGlobalGradeLevel(new PageRequest(page,20));
        model.addAttribute("globalGradeLevelList", list);
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(list));
        model.addAttribute("nextPage", paginationUtil.getNextPage(list));
        return "admin/course/globalGradeLevelList";
    }

    @RequestMapping(value = "/addGlobalGradeLevel", method = RequestMethod.POST)
    public String addGlobalGradeLevel(@Valid GlobalGradeLevelForm form, BindingResult bindingResult)
            throws DuplicateGlobalGradeLevelException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/schoolYearList";
        }
        else {
            studentService.addGlobalGradeLevel(form.getGrade(), form.getGradeName(), form.getXadeGrade(),
                    form.getCourseLevel());
            return "redirect:/admin/course/globalGradeLevelList";
        }
    }

    @RequestMapping(value = "/deleteGlobalGradeLevel/{globalGradeLevelId}", method = RequestMethod.POST)
    public String addGlobalGradeLevel(@PathVariable("globalGradeLevelId") Long globalGradeLevelId)
            throws GlobalGradeLevelNotFoundException {

        studentService.deleteGlobalGradeLevel(globalGradeLevelId);
        return "redirect:/admin/course/globalGradeLevelList";
    }
}