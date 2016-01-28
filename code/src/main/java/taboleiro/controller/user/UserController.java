package taboleiro.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import taboleiro.controller.utils.PaginationUtil;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.student.Student;
import taboleiro.model.exception.DuplicateUserException;
import taboleiro.model.exception.IncorrectPasswordException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.service.user.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.validation.BindingResult;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/user")
public class UserController {

    private final UserService userService;
    private final SessionUtil sessionUtil;
    private final PaginationUtil paginationUtil;

    @Autowired
    public UserController(UserService userService, SessionUtil sessionUtil, PaginationUtil paginationUtil) {
        this.userService = userService;
        this.sessionUtil = sessionUtil;
        this.paginationUtil = paginationUtil;
    }

    @RequestMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "amount", defaultValue = "25") int amount,
            Model model, HttpServletRequest request) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Page<User> users = userService.listUser(new PageRequest(page, amount));
        model.addAttribute("userList", users.getContent());
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(users));
        model.addAttribute("nextPage", paginationUtil.getNextPage(users));

        return "admin/user/userList";
    }

    @RequestMapping(value = "/profile/{userId}", method = RequestMethod.GET)
    public String showUserProfile( @PathVariable("userId") long userId, Model model, HttpServletRequest request)
            throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        User u = userService.findUserByUserId(userId);
        model.addAttribute("user", u);
        return "admin/user/profile";
    }

    @RequestMapping(value = "/childrenList/{userId}", method = RequestMethod.GET)
    public String showChildrenList(@PathVariable("userId") long userId, Model model, HttpServletRequest request)
            throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        User user = userService.findUserByUserId(userId);
        model.addAttribute("user", user);
        Set<Student> childrenList = user.getChildren();
        model.addAttribute("childrenList", childrenList);
        return "admin/user/childrenList";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm(Model model, HttpServletRequest request) throws UserNotFoundException {
        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        return "admin/message/registerForm";
    }

    @RequestMapping(value = "/registerForm", method = RequestMethod.POST)
    public String processRegistrationForm(@Valid UserCreateForm userForm, BindingResult bindingResult)
            throws DuplicateUserException {
        if (bindingResult.hasErrors()) {
            return "/register";
        }
        else {
            userService.createUser(userForm.getLoginName(), userForm.getFirstName(), userForm.getLastName(),
                    userForm.getPassword(),userForm.getPhoneNumber(),userForm.getEmail(), userForm.getRole());
            return "redirect:/admin/user/list";
        }
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.GET)
    public String showUserEditForm(@PathVariable("userId") long userId, Model model, HttpServletRequest request)
            throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        model.addAttribute("user", userService.findUserByUserId(userId));
        return "admin/user/edit";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST)
    public String editUser( @PathVariable("userId") long userId, @Valid UserEditForm userForm, Model model)
            throws UserNotFoundException{

            model.addAttribute("user", userService.findUserByUserId(userId));
            model.addAttribute("form", userForm);
            userService.editUser(userId, userForm.getFirstName(), userForm.getLastName(), userForm.getEmail(),
                    userForm.getPhoneNumber(), userForm.getRole());
            return "redirect:/admin/user/list";

    }

    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.POST)
    public String deleteUser( @PathVariable("userId") long userId) throws UserNotFoundException {

        userService.deleteUser(userId);
        return "redirect:/admin/user/list";
    }

    @RequestMapping(value = "/password/{userId}", method = RequestMethod.GET)
    public String showChangePassword( @PathVariable("userId") long userId, Model model, HttpServletRequest request)
            throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        model.addAttribute("user", userService.findUserByUserId(userId));
        return "admin/user/password";
    }

    @RequestMapping(value = "/password/{userId}", method = RequestMethod.POST)
    public String changePassword(@PathVariable("userId") Long userId, UserPasswordForm psswdForm,
                                  Model model) throws UserNotFoundException, IncorrectPasswordException {

        model.addAttribute("user", userService.findUserByUserId(userId));
        userService.changePassword(userId, psswdForm.getPassword());
        return "redirect:/admin/user/list";
    }

    @RequestMapping(value = "/changeMyPassword/{userId}", method = RequestMethod.POST)
    public String changeMyPassword(@PathVariable("userId") Long userId, UserPasswordForm psswdForm)
            throws UserNotFoundException, IncorrectPasswordException {

        userService.changePassword(userId, psswdForm.getPassword());
        return "redirect:/admin/user/myprofile";
    }

    @RequestMapping(value = "/myprofile", method = RequestMethod.GET)
    public String myProfile(HttpServletRequest request, Model model) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        model.addAttribute("user", userService.findUserByLoginName(request.getRemoteUser()));
        return "admin/user/myprofile";
    }

    @RequestMapping(value = "/editMyProfile", method = RequestMethod.POST)
    public String editMyProfile(HttpServletRequest request, @Valid MyProfileForm form, Model model)
            throws UserNotFoundException {
        User u = userService.findUserByLoginName(request.getRemoteUser());
        model.addAttribute("user", u);
        model.addAttribute("form", form);
        userService.editUser(u.getUserId(), form.getFirstName(), form.getLastName(), form.getEmail(),
                form.getPhoneNumber());
        return "redirect:/admin/user/myprofile";
    }
}