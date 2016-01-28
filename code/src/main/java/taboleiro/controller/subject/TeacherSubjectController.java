package taboleiro.controller.subject;

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
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.course.*;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.student.StudentAttendance;
import taboleiro.model.domain.student.GlobalGradeLevel;
import taboleiro.model.domain.subject.StudentTaskGrade;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.subject.Grade;
import taboleiro.model.domain.subject.Attendance;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.subject.Attendance.FaultType;
import taboleiro.model.exception.*;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.user.UserService;
import taboleiro.model.service.StudentService;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import taboleiro.model.domain.subject.Task;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/teacher/course")
public class TeacherSubjectController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final SessionUtil sessionUtil;
    private final PaginationUtil paginationUtil;

    @Autowired
    public TeacherSubjectController(StudentService studentService, CourseService courseService, UserService userService,
                                    SubjectService subjectService, SessionUtil sessionUtil,
                                    PaginationUtil paginationUtil) {

        this.studentService = studentService;
        this.courseService = courseService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.sessionUtil = sessionUtil;
        this.paginationUtil = paginationUtil;
    }

    /*
     *   subject views
     */

    @RequestMapping("/subjectList")
    public String subjectList(HttpServletRequest request, Model model) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        User u = userService.findUserByLoginName(request.getRemoteUser());
        List<GroupSubject> groupSubjectList = courseService.findGroupSubjectByTeacher(u.getUserId());
        model.addAttribute("groupSubjectList", groupSubjectList);
        return "teacher/course/subjectList";
    }

    @RequestMapping(value = "/student-list/{classGroupId}", method = RequestMethod.GET)
    public String subjectStudentList(@PathVariable("classGroupId") long classGroupId,
            Model model, HttpServletRequest request)
            throws UserNotFoundException, ClassGroupNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        ClassGroup cg = courseService.findClassGroupById(classGroupId);
        List<Student> studentList = studentService.findStudentByClassGroup(cg.getClassGroupId());
        model.addAttribute("classGroup", cg);
        model.addAttribute("studentList", studentList);
        return "teacher/course/student-list";
    }

    /*
     * GlobalGrade Views
     */

    @RequestMapping(value = "/selectSubjectGlobalGrade")
    public String selectCourseGlobalGrade(HttpServletRequest request, Model model) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        User u = userService.findUserByLoginName(request.getRemoteUser());
        List<GroupSubject> groupSubjectList = courseService.findGroupSubjectByTeacher(u.getUserId());
        model.addAttribute("groupSubjectList", groupSubjectList);
        return "teacher/course/selectSubjectGlobalGrade";
    }

    @RequestMapping(value = "/listSubjectGlobalGrade/{groupSubjectId}")
    public String listGlobalGrade(@PathVariable("groupSubjectId") long groupSubjectId, HttpServletRequest request,
                                  Model model) throws UserNotFoundException, GroupSubjectNotFoundException,
                                    ClassGroupNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        GroupSubject groupSubject = courseService.findGroupSubjectById(groupSubjectId);
        List<Student> studentList = studentService.findStudentByClassGroup(groupSubject.getClassGroup()
                .getClassGroupId());
        model.addAttribute("groupSubject", groupSubject);
        model.addAttribute("studentList", studentList);
        model.addAttribute("studentGlobalGradeList",
                studentService.findGlobalGradeEvByClassGroupSubject(groupSubjectId));
        return "teacher/course/listSubjectGlobalGrade";
    }

    @RequestMapping(value = "/addGlobalGrade/{studentId}/{groupSubjectId}", method = RequestMethod.GET)
    public String showGlobalGradeForm(HttpServletRequest request, @PathVariable("studentId") Long studentId,
            @RequestParam(value = "subject") Long subjectId,
            @PathVariable(value = "groupSubjectId") Long groupSubjectId, Model model)
            throws StudentNotFoundException, SubjectNotFoundException, SchoolYearNotFoundException,
            DuplicateGlobalGradeException, UserNotFoundException, GroupSubjectNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Student s = studentService.findStudentById(studentId);
        Subject sb = subjectService.findSubjectById(subjectId);
        GroupSubject gs = courseService.findGroupSubjectById(groupSubjectId);
        List<GlobalGradeLevel> gradeList = studentService.findGlobalGradeLevelByCourseLevel(sb.getCourse()
                .getCourseLevel());
        model.addAttribute("student", s);
        model.addAttribute("subject", sb);
        model.addAttribute("groupSubjectId", groupSubjectId);
        model.addAttribute("gradeList", gradeList);
        return "teacher/course/addGlobalGrade";
    }

    @RequestMapping(value = "/addGlobalGrade/{groupSubjectId}", method = RequestMethod.POST)
    public String processGlobalGradeForm(GlobalGradeCreateForm form, Model model,
            @PathVariable("groupSubjectId") Long groupSubjectId)
            throws StudentNotFoundException, SubjectNotFoundException, SchoolYearNotFoundException,
            DuplicateGlobalGradeException, ClassGroupNotFoundException {

        Student student = studentService.findStudentById(form.getStudent());
        Subject subject = subjectService.findSubjectById(form.getSubject());
        SchoolYear sy = courseService.findSchoolYearBySchoolYearName("2015-2016");
        GlobalGradeLevel ggl = studentService.findGlobalGradeLevelById(form.getGrade());
        model.addAttribute("student", student);
        model.addAttribute("subject", subject);
        studentService.addGlobalGrade(student.getStudentId(), subject, sy, ggl, form.getEvaluation(),
                form.getObservation());

        return "redirect:/teacher/course/listSubjectGlobalGrade/"+groupSubjectId;
    }

    /*
     * Attendance Views
     */

    @RequestMapping("/selectAttendanceSubject")
    public String selectAttendanceSubject(HttpServletRequest request, Model model) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        List<GroupSubject> groupSubjectList = courseService.findGroupSubjectByTeacher(u.getUserId());
        model.addAttribute("groupSubjectList", groupSubjectList);
        return "teacher/course/selectAttendanceSubject";
    }

    @RequestMapping("/attendanceList/{groupSubjectId}")
    public String attendanceList(@PathVariable("groupSubjectId") Long groupSubjectId, HttpServletRequest request,
                                 Model model) throws UserNotFoundException, GroupSubjectNotFoundException,
                                    ClassGroupNotFoundException, SubjectNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        GroupSubject group = courseService.findGroupSubjectById(groupSubjectId);
        Long classGroupId = group.getClassGroup().getClassGroupId();
        Subject subject = group.getSubject();
        model.addAttribute("subject", subject);
        model.addAttribute("classGroup", classGroupId);
        model.addAttribute("groupSubjectId", groupSubjectId);
        FaultType attendanceFault = FaultType.ATTENDANCE;
        FaultType punctualityFault = FaultType.PUNCTUALITY;
        model.addAttribute("attendanceFault", attendanceFault);
        model.addAttribute("punctualityFault", punctualityFault);
        LocalDate date = LocalDate.now(ZoneId.of("GMT+1"));
        List<StudentAttendance> studentAttendanceList = subjectService.AttendanceOfTheDay(classGroupId,
                subject.getSubjectId(), date);
        model.addAttribute("studentAttendanceList", studentAttendanceList);
        return "teacher/course/attendance";
    }

    @RequestMapping("/notJustifiedAttendanceList/{groupSubjectId}")
    public String notJustifiedAttendanceList(@PathVariable("groupSubjectId") Long groupSubjectId, Model model,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
              HttpServletRequest request) throws UserNotFoundException, GroupSubjectNotFoundException,
            ClassGroupNotFoundException, SubjectNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        GroupSubject group = courseService.findGroupSubjectById(groupSubjectId);
        Long classGroupId = group.getClassGroup().getClassGroupId();
        Subject subject = group.getSubject();
        model.addAttribute("subject", subject);
        model.addAttribute("classGroup", classGroupId);
        model.addAttribute("groupSubjectId", groupSubjectId);
        FaultType attendanceFault = FaultType.ATTENDANCE;
        FaultType punctualityFault = FaultType.PUNCTUALITY;
        model.addAttribute("attendanceFault", attendanceFault);
        model.addAttribute("punctualityFault", punctualityFault);
        Page<Attendance> studentAttendanceList = subjectService.notJustifiedAttendanceByClassGroup(
                new PageRequest(page, 12), classGroupId, subject.getSubjectId());
        model.addAttribute("studentAttendanceList", studentAttendanceList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(studentAttendanceList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(studentAttendanceList));
        return "teacher/course/notJustifiedAttendance";
    }

    @RequestMapping("/justifiedAttendanceList/{groupSubjectId}")
    public String justifiedAttendanceList(@PathVariable("groupSubjectId") Long groupSubjectId, Model model,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          HttpServletRequest request) throws UserNotFoundException,
            GroupSubjectNotFoundException, ClassGroupNotFoundException, SubjectNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        GroupSubject group = courseService.findGroupSubjectById(groupSubjectId);
        Long classGroupId = group.getClassGroup().getClassGroupId();
        Subject subject = group.getSubject();
        model.addAttribute("subject", subject);
        model.addAttribute("classGroup", classGroupId);
        model.addAttribute("groupSubjectId", groupSubjectId);
        FaultType attendanceFault = FaultType.ATTENDANCE;
        FaultType punctualityFault = FaultType.PUNCTUALITY;
        model.addAttribute("attendanceFault", attendanceFault);
        model.addAttribute("punctualityFault", punctualityFault);
        Page<Attendance> studentAttendanceList = subjectService.justifiedAttendanceByClassGroup(
                new PageRequest(page, 12), classGroupId, subject.getSubjectId());
        model.addAttribute("studentAttendanceList", studentAttendanceList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(studentAttendanceList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(studentAttendanceList));
        return "teacher/course/justifiedAttendance";
    }

    @RequestMapping(value = "/addAttendance/{groupSubjectId}", method = RequestMethod.POST)
    public String addAttendanceList(@PathVariable("groupSubjectId") Long groupSubjectId,
                                    AddAttendanceForm form)
            throws GroupSubjectNotFoundException, StudentNotFoundException, SubjectNotFoundException,
                ClassGroupNotFoundException, DuplicateAttendanceException {

         subjectService.addAttendance(
                 studentService.findStudentById(form.getStudent()),
                 subjectService.findSubjectById(form.getSubject()),
                 courseService.findClassGroupById(form.getClassGroup()), form.getFaultType());

        return "redirect:/teacher/course/attendanceList/"+groupSubjectId;
    }

    @RequestMapping(value= "/deleteAttendance/{attendanceId}", method = RequestMethod.POST)
    public String deleteAttendance(@PathVariable("attendanceId") Long attendanceId) throws AttendanceNotFoundException {

        Attendance att = subjectService.findAttendanceById(attendanceId);
        subjectService.deleteAttendance(attendanceId);

        return "redirect:/teacher/course/notJustifiedAttendanceList/"+att.getSubject().getSubjectId();
    }

    /*
     *  Schedule Views
     */

    @RequestMapping("/schedule")
    public String showTeacherSchedule(HttpServletRequest request, Model model) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        List<ViewTeacherSchedule> teacherScheduleList = courseService.findTeacherSchedule(u.getUserId());
        model.addAttribute("teacherScheduleList", teacherScheduleList);

        return "teacher/course/schedule";
    }

    /*
     * Task Views
     */

    @RequestMapping("/selectTaskSubject")
    public String selectTaskGroup(HttpServletRequest request, Model model) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        List<GroupSubject> groupSubjectList = courseService.findGroupSubjectByTeacher(u.getUserId());
        model.addAttribute("groupSubjectList", groupSubjectList);
        return "teacher/course/selectTaskSubject";
    }

    @RequestMapping("/taskList/{groupSubjectId}")
    public String showTaskList(@PathVariable("groupSubjectId") Long groupSubjectId, HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = "0") int page,
           Model model, TaskCreateForm form) throws GroupSubjectNotFoundException, UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        model.addAttribute("form", form);
        model.addAttribute("groupSubjectId", groupSubjectId);
        GroupSubject groupSubject = courseService.findGroupSubjectById(groupSubjectId);
        Page<Task> taskList = subjectService.findTaskByClassGroupAndSubject(new PageRequest(page, 10),
                groupSubject.getClassGroup().getClassGroupId(),
                groupSubject.getSubject().getSubjectId());
        model.addAttribute("taskList", taskList.getContent());
        model.addAttribute("groupSubject", groupSubject);
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(taskList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(taskList));
        return "teacher/course/taskList";
    }


    @RequestMapping("/oldTask/{groupSubjectId}")
    public String showOldTaskList(@PathVariable("groupSubjectId") Long groupSubjectId, HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               Model model, TaskCreateForm form) throws GroupSubjectNotFoundException, UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        model.addAttribute("form", form);
        model.addAttribute("groupSubjectId", groupSubjectId);
        GroupSubject groupSubject = courseService.findGroupSubjectById(groupSubjectId);
        Page<Task> taskList = subjectService.findOldTaskByClassGroupAndSubject(new PageRequest(page, 10),
                groupSubject.getClassGroup().getClassGroupId(),
                groupSubject.getSubject().getSubjectId());
        model.addAttribute("taskList", taskList.getContent());
        model.addAttribute("groupSubject", groupSubject);
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(taskList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(taskList));
        return "teacher/course/oldTaskList";
    }

    @RequestMapping(value = "/addTask", method = RequestMethod.POST)
    public String addTask(TaskCreateForm form)
            throws DuplicateTaskException, SubjectNotFoundException, ClassGroupNotFoundException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(form.getTaskDate(), formatter);
        subjectService.addTask(form.getTaskName(), date, form.getSubject(), form.getClassGroup(),
                form.getEvaluation(), form.getTaskType());
        GroupSubject gs = courseService.findGroupSubjectByClassGroupAndSubject(form.getClassGroup().getClassGroupId(),
                form.getSubject().getSubjectId());
        return "redirect:/teacher/course/taskList/"+gs.getGroupSubjectId();
    }

    @RequestMapping(value = "/editTask/{taskId}", method = RequestMethod.GET)
    public String showEditTask(@PathVariable("taskId") Long taskId, Model model, HttpServletRequest request)
            throws TaskNotFoundException, ClassGroupNotFoundException, UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Task t = subjectService.findTaskById(taskId);
        model.addAttribute("task", t);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = t.getTaskDate().format(formatter);
        model.addAttribute("date", date);
        return "teacher/course/editTask";
    }

    @RequestMapping(value = "/editTask/{taskId}", method = RequestMethod.POST)
    public String editTask(@Valid TaskCreateForm form, BindingResult result,
                           @PathVariable("taskId") Long taskId)
            throws TaskNotFoundException, ClassGroupNotFoundException {

        if (result.hasErrors()) {
            return "redirect:/selectTaskSubject";
        }
        else {
            subjectService.updateTask(taskId, form);
            GroupSubject gs = courseService.findGroupSubjectByClassGroupAndSubject(form.getClassGroup().getClassGroupId(),
                    form.getSubject().getSubjectId());
            return "redirect:/teacher/course/taskList/"+gs.getGroupSubjectId();
        }
    }

    @RequestMapping(value= "/deleteTask/{taskId}", method = RequestMethod.POST)
    public String deleteTask(@PathVariable("taskId") Long taskId) throws TaskNotFoundException,
            SubjectNotFoundException, ClassGroupNotFoundException {

        Task task = subjectService.findTaskById(taskId);
        GroupSubject gs = courseService.findGroupSubjectByClassGroupAndSubject(task.getClassGroup().getClassGroupId(),
                task.getSubject().getSubjectId());
        subjectService.deleteTask(task.getTaskId());
        return "redirect:/teacher/course/taskList/"+gs.getGroupSubjectId();
    }

    /*
     * Grades Views
     */

    @RequestMapping("/selectGradeSubject")
    public String selectGradeSubject(HttpServletRequest request, Model model) throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        List<GroupSubject> groupSubjectList = courseService.findGroupSubjectByTeacher(u.getUserId());
        model.addAttribute("groupSubjectList", groupSubjectList);
        return "teacher/course/selectGradeSubject";
    }

    @RequestMapping("/gradeTaskList/{groupSubjectId}")
    public String selectGradeTask(HttpServletRequest request, @PathVariable("groupSubjectId") Long groupSubjectId,
                                  @RequestParam(value = "page", defaultValue = "0") int page, Model model)
            throws UserNotFoundException, GroupSubjectNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        GroupSubject groupSubject = courseService.findGroupSubjectById(groupSubjectId);
        Page<Task> taskList = subjectService.findTaskByClassGroupAndSubject(new PageRequest(page, 20),
                groupSubject.getClassGroup().getClassGroupId(),
                groupSubject.getSubject().getSubjectId());
        model.addAttribute("taskList", taskList.getContent());
        model.addAttribute("groupSubject", groupSubject);

        return "teacher/course/selectGradeTask";
    }

    @RequestMapping("/taskGrade/{taskId}")
    public String taskGrade(HttpServletRequest request, @PathVariable("taskId") Long taskId, Model model)
            throws TaskNotFoundException, UserNotFoundException, ClassGroupNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Task t = subjectService.findTaskById(taskId);
        List<StudentTaskGrade> studentTaskGradeList = subjectService.findStudentTaskGradeByTask(taskId);
        model.addAttribute("task", t);
        model.addAttribute(studentTaskGradeList);

        return "teacher/course/taskGrade";
    }

    @RequestMapping(value = "/addTaskGrade/{taskId}/{studentId}", method = RequestMethod.GET)
    public String showTaskGradeForm(@PathVariable("taskId") Long taskId, @PathVariable("studentId") Long studentId,
            Model model, HttpServletRequest request) throws TaskNotFoundException, StudentNotFoundException,
            UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Task t = subjectService.findTaskById(taskId);
        Student s = studentService.findStudentById(studentId);
        model.addAttribute("task", t);
        model.addAttribute("student", s);
        return "teacher/course/addTaskGrade";
    }

    @RequestMapping(value = "/addTaskGrade/", method = RequestMethod.POST)
    public String processTaskGradeForm( AddTaskGradeForm form) throws TaskNotFoundException,
                StudentNotFoundException, DuplicateGradeException {

        Task t = subjectService.findTaskById(form.getTask());
        Student s = studentService.findStudentById(form.getStudent());
        subjectService.addGrade(t, s, form.getGrade(), form.getObservation());
        return "redirect:/teacher/course/taskGrade/"+t.getTaskId();
    }

    @RequestMapping(value = "/editTaskGrade/{taskId}/{studentId}", method = RequestMethod.GET)
    public String showEditTaskGrade(@PathVariable("taskId") Long taskId, @PathVariable("studentId") Long studentId,
                                    Model model, HttpServletRequest request)
            throws TaskNotFoundException, ClassGroupNotFoundException, UserNotFoundException, GradeNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Task t = subjectService.findTaskById(taskId);
        model.addAttribute("task", t);
        Grade g = subjectService.findGradeByTaskAndStudent(taskId, studentId);
        model.addAttribute("grade", g);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = t.getTaskDate().format(formatter);
        model.addAttribute("date", date);
        return "teacher/course/editTaskGrade";
    }

    @RequestMapping(value = "/editTaskGrade/{taskId}/{studentId}", method = RequestMethod.POST)
    public String editTaskGrade(@Valid AddTaskGradeForm form, BindingResult result,
                           @PathVariable("taskId") Long taskId,  @PathVariable("studentId") Long studentId)
            throws TaskNotFoundException, ClassGroupNotFoundException, GradeNotFoundException {

        if (result.hasErrors()) {
            return "redirect:/selectTaskSubject";
        }
        else {
            Task t = subjectService.findTaskById(taskId);
            Grade g = subjectService.findGradeByTaskAndStudent(taskId, studentId);
            subjectService.updateGrade(g.getGradeId(), form.getGrade(), form.getObservation());
            return "redirect:/teacher/course/taskGrade/"+t.getTaskId();
        }
    }

}
