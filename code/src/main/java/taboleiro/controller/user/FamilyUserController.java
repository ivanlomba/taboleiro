package taboleiro.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.ChildrenListNotFoundException;
import taboleiro.model.exception.IncorrectPasswordException;
import taboleiro.model.exception.StudentNotFoundException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.user.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Set;
import taboleiro.model.service.MessageService;

@Controller
@RequestMapping("/family/user")
public class FamilyUserController {

    private final UserService userService;
    private final MessageService messageService;
    private final SubjectService subjectService;
    private final SessionUtil sessionUtil;

    @Autowired
    public FamilyUserController(UserService userService, MessageService messageService,
                                SubjectService subjectService, SessionUtil sessionUtil) {

        this.userService = userService;
        this.messageService = messageService;
        this.subjectService = subjectService;
        this.sessionUtil = sessionUtil;
    }

    @RequestMapping(value = "/myprofile", method = RequestMethod.GET)
    public String myProfile(HttpServletRequest request, Model model)
            throws UserNotFoundException, ChildrenListNotFoundException, StudentNotFoundException {
        User u = userService.findUserByLoginName(request.getRemoteUser());
        Student student = sessionUtil.getCurrentChild(request);
        Set<Student> childrenList = userService.listChildren(u);
        model.addAttribute("childrenList", childrenList);
        model.addAttribute("user", u);
        //faltas sin justificar
        Integer attendanceNumber = subjectService.countAttendanceByStudentNotJustified(student.getStudentId());
        model.addAttribute("NewAttendance", attendanceNumber);
        //mensajes sin leer
        Integer newMail = messageService.countNewMail(u.getUserId());
        model.addAttribute("newMail", newMail);
        return "family/user/myprofile";
    }

    @RequestMapping(value = "/editMyProfile", method = RequestMethod.POST)
    public String editMyProfile(HttpServletRequest request, @Valid MyProfileForm form, Model model) throws UserNotFoundException {
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        model.addAttribute("form", form);
        userService.editUser(u.getUserId(), form.getFirstName(), form.getLastName(), form.getEmail(), form.getPhoneNumber());
        return "redirect:/family/user/myprofile";
    }

    @RequestMapping(value = "/changeMyPassword/{userId}", method = RequestMethod.POST)
    public String changeMyPassword(@PathVariable("userId") Long userId, UserPasswordForm psswdForm)
            throws UserNotFoundException, IncorrectPasswordException {

        userService.changePassword(userId, psswdForm.getPassword());
        return "redirect:/family/user/myprofile";
    }

}