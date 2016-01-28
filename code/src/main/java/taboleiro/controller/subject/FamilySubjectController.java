package taboleiro.controller.subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import taboleiro.controller.utils.PaginationUtil;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.domain.subject.Attendance;
import taboleiro.model.domain.subject.Task;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.course.Schedule;
import taboleiro.model.exception.*;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.user.UserService;
import taboleiro.model.service.MessageService;
import taboleiro.model.domain.subject.Grade;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/family/subject")
public class FamilySubjectController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final MessageService messageService;
    private final SessionUtil sessionUtil;
    private final PaginationUtil paginationUtil;

    @Autowired
    public FamilySubjectController(StudentService studentService, CourseService courseService, UserService userService,
                                   SubjectService subjectService, MessageService messageService,
                                   SessionUtil sessionUtil, PaginationUtil paginationUtil) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.messageService = messageService;
        this.sessionUtil = sessionUtil;
        this.paginationUtil = paginationUtil;
    }

    @RequestMapping("/pendingAttendanceList")
    public String showAttendanceList(HttpServletRequest request, Model model,
                                     @RequestParam(value = "page", defaultValue = "0") int page)
            throws StudentNotFoundException,
            ChildrenListNotFoundException, UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance count (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Message count (not viewed)
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        // Pending AttendanceList
        Page<Attendance> attendanceList = subjectService.findNotJustifiedAttendanceByStudent(new PageRequest(page, 10),
                student.getStudentId());
        model.addAttribute("attendanceList", attendanceList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(attendanceList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(attendanceList));
        return "family/subject/pendingAttendanceList";
    }

    @RequestMapping("/justifiedAttendanceList")
    public String showJustifyAttendanceList(HttpServletRequest request, Model model,
                                            @RequestParam(value = "page", defaultValue = "0") int page)
            throws StudentNotFoundException,
            ChildrenListNotFoundException, UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children session
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Message alert (not viewed)
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        // Justified attendanceList
        Page<Attendance> attendanceList = subjectService.findJustifiedAttendanceByStudent(new PageRequest(page, 10),
                student.getStudentId());
        model.addAttribute("attendanceList", attendanceList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(attendanceList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(attendanceList));
        return "family/subject/justifiedAttendanceList";
    }

    @RequestMapping(value = "/justify/{attendanceId}", method = RequestMethod.POST)
    public String justifyAttendance(@PathVariable("attendanceId") Long attendanceId)
            throws AttendanceNotFoundException {
        subjectService.updateJustified(attendanceId);
        return "redirect:/family/subject/pendingAttendanceList";
    }

    @RequestMapping("/taskList")
    public String showTaskList(HttpServletRequest request, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page) throws GroupSubjectNotFoundException,
            UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children session
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Message alert (not viewed)
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        // Task List
        Page<Task> taskList = subjectService.findPendingTaskByClassGroup(new PageRequest(page, 12),
                student.getCurrentClassGroup().getClassGroupId());
        model.addAttribute("taskList", taskList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(taskList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(taskList));
        return "family/subject/taskList";
    }

    @RequestMapping("/oldTask")
    public String showOldTaskList(HttpServletRequest request, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page)
            throws GroupSubjectNotFoundException, UserNotFoundException, ChildrenListNotFoundException,
            StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children session
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Message alert (not viewed)
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        // Task List
        Page<Task> taskList = subjectService.findOldTaskByClassGroup(new PageRequest(page, 12),
                student.getCurrentClassGroup().getClassGroupId());
        model.addAttribute("taskList", taskList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(taskList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(taskList));
        return "family/subject/oldTaskList";
    }

    /*
     *  Schedule Views
     */

    @RequestMapping("/schedule")
    public String showSchedule(HttpServletRequest request, Model model) throws UserNotFoundException,
            StudentNotFoundException, ChildrenListNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children session
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Message alert (not viewed)
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        // show Schedule
        List<Schedule> studentScheduleList = courseService.findScheduleByGroup(
                student.getCurrentClassGroup().getClassGroupId());
        model.addAttribute("studentScheduleList", studentScheduleList);
        return "family/subject/schedule";
    }

    /*
     * GlobalGrade Views
     */

    @RequestMapping("/globalGrade")
    public String showGlobalGrade(HttpServletRequest request, Model model) throws UserNotFoundException,
            StudentNotFoundException, ChildrenListNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children session
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance alert
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Message alert
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        // Show globalGrade
        List<GlobalGrade> globalGradeList = studentService.findGlobalGradeByStudentAndSchoolYear(student.getStudentId(),
                student.getCurrentClassGroup().getSchoolYear().getSchoolYearId());
        model.addAttribute("globalGradeList", globalGradeList);
        return "family/subject/globalGrade";
    }

    @RequestMapping("/gradeList")
    public String taskGrades(HttpServletRequest request, Model model,
                             @RequestParam(value = "page", defaultValue = "0") int page) throws UserNotFoundException,
            StudentNotFoundException, ChildrenListNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children session
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance alert
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Message alert
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        // Grades
        Page<Grade> gradeList = subjectService.findGradeByStudent(new PageRequest(page,10), student.getStudentId());
        model.addAttribute("gradeList", gradeList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(gradeList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(gradeList));
        return "family/subject/gradeList";
    }
}
