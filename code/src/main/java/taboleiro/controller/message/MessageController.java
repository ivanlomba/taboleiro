package taboleiro.controller.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import taboleiro.controller.utils.PaginationUtil;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.message.Message;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.*;
import taboleiro.model.service.MessageService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Controller
public class MessageController {

    private final UserService userService;
    private final MessageService messageService;
    private final SubjectService subjectService;
    private final SessionUtil sessionUtil;
    private final StudentService studentService;
    private final PaginationUtil paginationUtil;
    private final CourseService courseService;

    @Autowired
    public MessageController(UserService userService, MessageService messageService, SubjectService subjectService,
                             SessionUtil sessionUtil, PaginationUtil paginationUtil, StudentService studentService,
                             CourseService courseService) {

        this.userService = userService;
        this.messageService = messageService;
        this.subjectService = subjectService;
        this.sessionUtil = sessionUtil;
        this.paginationUtil = paginationUtil;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    /*
     *  Message Views
     */

    @Secured("ADMIN")
    @RequestMapping("/admin/message/inbox")
    public String adminInbox(HttpServletRequest request, Model model,
                             @RequestParam(value = "page", defaultValue = "0") int page)
            throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<Message> messageList = messageService.findMessageByAddressee(u.getUserId(), new PageRequest(page,15));
        model.addAttribute("messageList", messageList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(messageList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(messageList));
        return "admin/message/inbox";
    }

    @Secured("ADMIN")
    @RequestMapping("/admin/message/read/{messageId}")
    public String adminReadMsg(HttpServletRequest request, @PathVariable Long messageId,
                               Model model) throws UserNotFoundException, MessageNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        Message message = messageService.readMessage(messageId, u.getUserId());
        model.addAttribute("message", message);
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        return "admin/message/read";
    }

    @Secured("ADMIN")
    @RequestMapping("/admin/message/readoutbox/{messageId}")
    public String adminReadSendedMsg(HttpServletRequest request, @PathVariable Long messageId,
                               Model model) throws UserNotFoundException, MessageNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Message message = messageService.readMessage(messageId, u.getUserId());
        model.addAttribute("message", message);
        return "admin/message/readoutbox";
    }

    @Secured("ADMIN")
    @RequestMapping(value = "/admin/message/write", method = RequestMethod.GET)
    public String adminWriteMsg(HttpServletRequest request, Model model)
            throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("senderId",u.getUserId());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Set<User> addressee = messageService.findStaffAddressee();
        model.addAttribute("addressee", addressee);
        return "admin/message/write";
    }

    @Secured("ADMIN")
    @RequestMapping(value = "/admin/message/write/{studentId}", method = RequestMethod.GET)
    public String adminWriteStudentMsg(HttpServletRequest request, @PathVariable Long studentId, Model model)
            throws UserNotFoundException, StudentNotFoundException {

        Student student = studentService.findStudentById(studentId);
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("senderId",u.getUserId());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        model.addAttribute("student", student);
        return "admin/message/writeStudent";
    }

    @Secured("ADMIN")
    @RequestMapping(value = "/admin/message/write/user/{userId}", method = RequestMethod.GET)
    public String adminWriteUserMsg(HttpServletRequest request, @PathVariable Long userId, Model model)
            throws UserNotFoundException {

        User addressee = userService.findUserByUserId(userId);
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("senderId",u.getUserId());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        model.addAttribute("addressee", addressee);
        return "admin/message/writeUser";
    }

    @Secured("ADMIN")
    @RequestMapping(value = "/admin/message/send", method = RequestMethod.POST)
    public String adminSendMsg(HttpServletRequest request, @Valid MessageForm form, BindingResult bindingResult,
                               Model model) throws UserNotFoundException, MessageNotFoundException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/message/write";
        }
        else {
            messageService.sendMessage(form);
            User u = userService.findUserByLoginName(request.getRemoteUser());
            Page<Message> messageList = messageService.findMessageByAddressee(u.getUserId(), new PageRequest(0, 15));
            model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
            model.addAttribute("messageList", messageList);
            return "redirect:/admin/message/inbox";
        }
    }

    @Secured("ADMIN")
    @RequestMapping(value = "/admin/message/delete/{messageId}", method = RequestMethod.POST)
    public String adminDeleteMsg(@PathVariable Long messageId) throws MessageNotFoundException {

        messageService.deleteMessage(messageId);
        return "redirect:/admin/message/inbox";
    }

    @Secured("ADMIN")
    @RequestMapping(value = "/admin/message/unread/{messageId}", method = RequestMethod.POST)
    public String adminUnreadMsg(@PathVariable Long messageId) throws MessageNotFoundException {

        messageService.markUnread(messageId);
        return "redirect:/admin/message/inbox";
    }

    @Secured("ADMIN")
    @RequestMapping("/admin/message/outbox")
    public String adminOutbox(HttpServletRequest request, Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<Message> messageList = messageService.findMessageBySender(u.getUserId(), new PageRequest(page, 15));
        model.addAttribute("messageList", messageList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(messageList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(messageList));
        return "admin/message/outbox";
    }

    @Secured("TEACHER")
    @RequestMapping("/teacher/message/inbox")
    public String teacherInbox(HttpServletRequest request, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<Message> messageList = messageService.findMessageByAddressee(u.getUserId(), new PageRequest(page, 15));
        model.addAttribute("messageList", messageList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(messageList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(messageList));
        return "teacher/message/inbox";
    }

    @Secured("TEACHER")
    @RequestMapping("/teacher/message/outbox")
    public String teacherSend(HttpServletRequest request, Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<Message> messageList = messageService.findMessageBySender(u.getUserId(), new PageRequest(page, 15));
        model.addAttribute("messageList", messageList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(messageList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(messageList));
        return "teacher/message/outbox";
    }

    @Secured("TEACHER")
    @RequestMapping("/teacher/message/read/{messageId}")
    public String teacherReadMsg(HttpServletRequest request, @PathVariable Long messageId,
                               Model model) throws UserNotFoundException, MessageNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        Message message = messageService.readMessage(messageId, u.getUserId());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        model.addAttribute("message", message);
        return "teacher/message/read";
    }

    @Secured("TEACHER")
    @RequestMapping("/teacher/message/readoutbox/{messageId}")
    public String teacherReadSendMsg(HttpServletRequest request, @PathVariable Long messageId,
                                 Model model) throws UserNotFoundException, MessageNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        Message message = messageService.readMessage(messageId, u.getUserId());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        model.addAttribute("message", message);
        return "teacher/message/readoutbox";
    }

    @Secured("TEACHER")
    @RequestMapping(value = "/teacher/message/write", method = RequestMethod.GET)
    public String teacherWriteMsg(HttpServletRequest request, Model model)
            throws UserNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("senderId",u.getUserId());
        // Mail count (not viewed)
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Set<User> addressee = messageService.findStaffAddressee();
        model.addAttribute("addressee", addressee);
        return "teacher/message/write";
    }

    @Secured("TEACHER")
    @RequestMapping(value = "/teacher/message/write/{studentId}", method = RequestMethod.GET)
    public String teacherWriteStudentMsg(HttpServletRequest request, @PathVariable Long studentId, Model model)
            throws UserNotFoundException, StudentNotFoundException {

        Student student = studentService.findStudentById(studentId);
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("senderId",u.getUserId());
        // Mail count (not viewed)
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        model.addAttribute("student", student);
        return "teacher/message/writeStudent";
    }

    @Secured("TEACHER")
    @RequestMapping(value = "/teacher/message/group/{classGroupId}", method = RequestMethod.GET)
    public String teacherGroupMsg(HttpServletRequest request, @PathVariable Long classGroupId, Model model)
            throws UserNotFoundException, StudentNotFoundException, ClassGroupNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("senderId",u.getUserId());
        // Mail count (not viewed)
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        ClassGroup cg = courseService.findClassGroupById(classGroupId);
        model.addAttribute("classGroup", cg);
        return "teacher/message/writeGroup";
    }

    @Secured("TEACHER")
    @RequestMapping(value = "/teacher/message/send", method = RequestMethod.POST)
    public String teacherSendMsg(HttpServletRequest request, @Valid MessageForm form, BindingResult bindingResult,
                               Model model) throws UserNotFoundException, MessageNotFoundException {

        if (bindingResult.hasErrors()) {
            return "redirect:/teacher/message/write";
        }
        else {
            messageService.sendMessage(form);
            User u = userService.findUserByLoginName(request.getRemoteUser());
            Page<Message> messageList = messageService.findMessageByAddressee(u.getUserId(), new PageRequest(0, 15));
            model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
            model.addAttribute("messageList", messageList);
            return "redirect:/teacher/message/inbox";
        }
    }

    @Secured("TEACHER")
    @RequestMapping(value = "/teacher/message/send/group", method = RequestMethod.POST)
    public String teacherSendGroupMsg(HttpServletRequest request, GroupMessageForm form,
                         Model model) throws UserNotFoundException, MessageNotFoundException,
                            ClassGroupNotFoundException {

        messageService.sendGroupMessage(form);
        User u = userService.findUserByLoginName(request.getRemoteUser());
        Page<Message> messageList = messageService.findMessageByAddressee(u.getUserId(), new PageRequest(0, 15));
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        model.addAttribute("messageList", messageList);
        return "redirect:/teacher/message/inbox";
    }

    @Secured("TEACHER")
    @RequestMapping(value = "/teacher/message/delete/{messageId}", method = RequestMethod.POST)
    public String teacherDeleteMsg(@PathVariable Long messageId) throws MessageNotFoundException {

        messageService.deleteMessage(messageId);
        return "redirect:/teacher/message/inbox";
    }

    @Secured("TEACHER")
    @RequestMapping(value = "/teacher/message/unread/{messageId}", method = RequestMethod.POST)
    public String teacherUnreadMsg(@PathVariable Long messageId) throws MessageNotFoundException {

        messageService.markUnread(messageId);
        return "redirect:/teacher/message/inbox";
    }

    @Secured("USER")
    @RequestMapping("/family/message/inbox")
    public String familyInbox(HttpServletRequest request, Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        // Attendance count (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        Page<Message> messageList = messageService.findMessageByAddressee(u.getUserId(), new PageRequest(page, 15));
        model.addAttribute("messageList", messageList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(messageList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(messageList));

        return "family/message/inbox";
    }

    @Secured("USER")
    @RequestMapping("/family/message/outbox")
    public String familyOutbox(HttpServletRequest request, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        // Children
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        Page<Message> messageList = messageService.findMessageBySender(u.getUserId(), new PageRequest(page, 15));
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        model.addAttribute("messageList", messageList.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(messageList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(messageList));
        return "family/message/outbox";
    }

    @Secured("USER")
    @RequestMapping("/family/message/read/{messageId}")
    public String familyReadMsg(HttpServletRequest request, @PathVariable Long messageId,
                                 Model model) throws UserNotFoundException, MessageNotFoundException,
            ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        // Children
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        Message message = messageService.readMessage(messageId, u.getUserId());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        model.addAttribute("message", message);
        return "family/message/read";
    }

    @Secured("USER")
    @RequestMapping("/family/message/readoutbox/{messageId}")
    public String familyReadSendedMsg(HttpServletRequest request, @PathVariable Long messageId,
                                     Model model) throws UserNotFoundException, MessageNotFoundException,
            ChildrenListNotFoundException, StudentNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        // Children
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        Message message = messageService.readMessage(messageId, u.getUserId());
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        model.addAttribute("message", message);
        return "family/message/readoutbox";
    }

    @Secured("USER")
    @RequestMapping(value = "/family/message/write", method = RequestMethod.GET)
    public String familyWriteMsg(HttpServletRequest request, Model model)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException,
            ClassGroupNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        // Children
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        model.addAttribute("senderId",u.getUserId());
        // Mail count (not viewed)
        model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
        // Attendance alert (not justified)
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        // Load teachers
        Set<User> teacherList = messageService.findStudentAddressee(student);
        model.addAttribute("teacherList", teacherList);
        return "family/message/write";
    }

    @Secured("USER")
    @RequestMapping(value = "/family/message/send", method = RequestMethod.POST)
    public String familySendMsg(HttpServletRequest request, @Valid MessageForm form, BindingResult bindingResult,
                                 Model model) throws UserNotFoundException, MessageNotFoundException {

        if (bindingResult.hasErrors()) {
            return "redirect:/family/message/write";
        }
        else {
            messageService.sendMessage(form);
            User u = userService.findUserByLoginName(request.getRemoteUser());
            Page<Message> messageList = messageService.findMessageByAddressee(u.getUserId(), new PageRequest(0, 15));
            model.addAttribute("newMail", messageService.countNewMail(u.getUserId()));
            model.addAttribute("messageList", messageList);
            return "redirect:/family/message/inbox";
        }
    }

    @Secured("USER")
    @RequestMapping(value = "/family/message/delete/{messageId}", method = RequestMethod.POST)
    public String familyDeleteMsg(@PathVariable Long messageId) throws MessageNotFoundException {

        messageService.deleteMessage(messageId);
        return "redirect:/family/message/inbox";
    }

    @Secured("USER")
    @RequestMapping(value = "/family/message/unread/{messageId}", method = RequestMethod.POST)
    public String familyUnreadMsg(@PathVariable Long messageId) throws MessageNotFoundException {

        messageService.markUnread(messageId);
        return "redirect:/family/message/inbox";
    }
}