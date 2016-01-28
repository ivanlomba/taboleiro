package taboleiro.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.IncorrectPasswordException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.service.user.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/teacher/user")
public class TeacherUserController {

    private final UserService userService;
    private final SessionUtil sessionUtil;

    @Autowired
    public TeacherUserController(UserService userService, SessionUtil sessionUtil) {
        this.userService = userService;
        this.sessionUtil = sessionUtil;
    }

    @RequestMapping(value = "/myprofile", method = RequestMethod.GET)
    public String myProfile(HttpServletRequest request, Model model) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        return "teacher/user/myprofile";
    }

    @RequestMapping(value = "/editMyProfile", method = RequestMethod.POST)
    public String editMyProfile(HttpServletRequest request, @Valid MyProfileForm form, Model model)
            throws UserNotFoundException {
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        model.addAttribute("form", form);
        userService.editUser(u.getUserId(), form.getFirstName(), form.getLastName(), form.getEmail(),
                form.getPhoneNumber());
        return "redirect:/teacher/user/myprofile";
    }

    @RequestMapping(value = "/changeMyPassword/{userId}", method = RequestMethod.POST)
    public String changeMyPassword(@PathVariable("userId") Long userId, UserPasswordForm psswdForm)
            throws UserNotFoundException, IncorrectPasswordException {

        userService.changePassword(userId, psswdForm.getPassword());
        return "redirect:/teacher/user/myprofile";
    }
}