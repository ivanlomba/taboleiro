package taboleiro.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMethod;
import taboleiro.controller.student.SelectedStudent;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.course.Schedule;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.exception.ChildrenListNotFoundException;
import taboleiro.model.exception.ClassGroupNotFoundException;
import taboleiro.model.exception.StudentNotFoundException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.user.UserService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.subject.Task;
import taboleiro.model.domain.student.Student;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private final UserService userService;
    private final CourseService courseService;
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final SessionUtil sessionUtil;

    @Autowired
    public HomeController(UserService userService, CourseService courseService, SubjectService subjectService,
                          StudentService studentService, SessionUtil sessionUtil) {
        this.userService = userService;
        this.courseService = courseService;
        this.subjectService = subjectService;
        this.studentService = studentService;
        this.sessionUtil = sessionUtil;
    }

    @RequestMapping("/")
    public String home(HttpServletRequest request)
            throws UserNotFoundException, ChildrenListNotFoundException {
        if (request.isUserInRole("ADMIN")) {
            return "redirect:/admin/message/inbox";
        }else if (request.isUserInRole("TEACHER")){
            return "redirect:/teacher/home";
        }else if (request.isUserInRole("USER")){
            return "redirect:/family/home";
        }else
            return "login_error";
    }

    @Secured("ADMIN")
    @RequestMapping("/admin/home")
    public String adminHome(HttpServletRequest request, Model model)
            throws UserNotFoundException, ChildrenListNotFoundException {

        model.addAttribute("day", sessionUtil.getDay());
        // New mail alert
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        model.addAttribute("schoolYearList", courseService.findVisibleSchoolYear());
        return "redirect:admin/message/inbox";
    }

    @Secured("TEACHER")
    @RequestMapping("/teacher/home")
    public String teacherHome(HttpServletRequest request, Model model)
            throws UserNotFoundException, ChildrenListNotFoundException {

        Schedule.WeekDay today = sessionUtil.getWeekDay();
        model.addAttribute("day", sessionUtil.getDay());
        User u = userService.findUserByLoginName(request.getRemoteUser());
        // Schedule
        model.addAttribute("todaySchedule", courseService.findTeacherScheduleAndWeekDay(u.getUserId(), today));
        // New mail alert
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        // Task list
        model.addAttribute("listaTareas", subjectService.findTeacherTask(u.getUserId()).getContent());
        model.addAttribute("listaExamenes", subjectService.findTeacherExam(u.getUserId()).getContent());
        return "teacher/home";
    }

    @Secured("USER")
    @RequestMapping("/family/home")
    public String familyHome(HttpServletRequest request, Model model)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException,
            ClassGroupNotFoundException {

        Schedule.WeekDay today = sessionUtil.getWeekDay();
        model.addAttribute("day", sessionUtil.getDay());
        User u = userService.findUserByLoginName(request.getRemoteUser());
        String userName = u.getFirstName()+" "+u.getLastName();
        model.addAttribute("userName", userName);
        // New mail alert
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        // Children
        Student student = sessionUtil.getCurrentChild(request);
        model.addAttribute("student", student);
        model.addAttribute("childrenList", userService.listChildren(u));
        // Task list
        ClassGroup classGroup = courseService.findClassGroupById(student.getCurrentClassGroup().getClassGroupId());
        Page <Task> taskList = subjectService.findProjectAndHomework(new PageRequest(0, 5), classGroup.getClassGroupId());
        Page <Task> examList = subjectService.findExams(new PageRequest(0, 5), classGroup.getClassGroupId());
        model.addAttribute("taskList", taskList.getContent());
        model.addAttribute("examList", examList.getContent());
        // Attendance alert (not justified)
        model.addAttribute("NewAttendance", subjectService.countAttendanceByStudentNotJustified(student.getStudentId()));
        // Schedule of the day
        model.addAttribute("todaySchedule", courseService.findStudentScheduleByGroupAndWeekDay(
                classGroup.getClassGroupId(), today));
        return "family/home";
    }

    @Secured("USER")
    @RequestMapping(value = "/family/setStudent", method = RequestMethod.POST)
    public String setStudent(HttpServletRequest request, SelectedStudent form, Model model)
            throws ChildrenListNotFoundException,
            UserNotFoundException, StudentNotFoundException {

        Student student = studentService.findStudentById(form.getStudentId());
        request.getSession().setAttribute("child", student);
        return "redirect:/family/home";
    }

    @RequestMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout, Model model) {
        if (error != null) {
            model.addAttribute("error", "Error de autenticación");
        }
        return "login";
    }
}