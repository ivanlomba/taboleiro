package taboleiro.controller.subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.exception.*;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.user.UserService;
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.course.GroupSubject;
import taboleiro.model.domain.course.ClassGroup;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin/course")
public class SubjectController {

    private final SubjectService subjectService;
    private final CourseService courseService;
    private final UserService userService;
    private final SessionUtil sessionUtil;

    @Autowired
    public SubjectController(SubjectService subjectService, CourseService courseService, UserService userService,
                             SessionUtil sessionUtil) {
        this.subjectService = subjectService;
        this.courseService = courseService;
        this.userService = userService;
        this.sessionUtil = sessionUtil;
    }

    /*
     *   subject views
     */

    @RequestMapping("/selectCourseSubjectList")
    public String subjectCourseList(Model model, HttpServletRequest request) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        return "admin/course/selectCourseSubjectList";
    }

    @RequestMapping("/subjectList/{courseId}")
    public String subjectList(
            @PathVariable("courseId") long courseId,
            Model model, HttpServletRequest request) throws UserNotFoundException, CourseNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Course course = courseService.findCourseById(courseId);
        model.addAttribute("courseName", course.getCourseName());
        List<Subject> subjectList = subjectService.findSubjectByCourse(courseId);
        model.addAttribute("subjectList", subjectList);
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        return "admin/course/subjectList";
    }

    @RequestMapping(value = "/createSubject", method = RequestMethod.POST)
    public String processSubjectCreation(@Valid SubjectCreateForm subjectForm, BindingResult bindingResult)
            throws DuplicateSubjectException, CourseNotFoundException {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/selectCourseSubjectList";
        }
        else {
            Long courseId = subjectForm.getCourse().getCourseId();
            subjectService.addSubject(subjectForm.getSubjectName(), subjectForm.getCourse().getCourseId(),
                    subjectForm.getCodigoMateriaAvaliable());
            return "redirect:/admin/course/subjectList/" + courseId;
        }
    }

    @RequestMapping(value = "/deleteSubject/{subjectId}", method = RequestMethod.GET)
    public String deleteSubject(@PathVariable("subjectId") long subjectId, Model model, HttpServletRequest request)
            throws SubjectNotFoundException, UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Subject s = subjectService.findSubjectById(subjectId);
        Long courseId = s.getCourse().getCourseId();
        subjectService.deleteSubject(s.getSubjectId());
        return "redirect:/admin/course/subjectList/"+courseId;
    }

    @RequestMapping(value = "/updateSubject/{subjectId}", method = RequestMethod.GET)
    public String showUpdateSubjectList(
            @PathVariable("subjectId") Long subjectId,
            Model model, HttpServletRequest request) throws UserNotFoundException, SubjectNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Subject s = subjectService.findSubjectById(subjectId);
        model.addAttribute("course", s.getCourse());
        model.addAttribute("subject", s);
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        return "admin/course/subjectEdit";
    }

    @RequestMapping(value = "/updateSubject/{subjectId}", method = RequestMethod.POST)
    public String updateSubject(@PathVariable("subjectId") Long subjectId, @Valid SubjectCreateForm form,
                                BindingResult bindingResult)
            throws SubjectNotFoundException, UserNotFoundException {

        Subject s = subjectService.findSubjectById(subjectId);
        Long courseId = s.getCourse().getCourseId();
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/subjectList/"+courseId;
        }
        else {
            subjectService.updateSubject(subjectId, form);
            return "redirect:/admin/course/subjectList/"+courseId;
        }
    }

    /*
     *   GroupSubject Views
     */

    @RequestMapping("/selectGroupSubjectList")
    public String subjectClassGroupList(Model model, HttpServletRequest request) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        List<ClassGroup> classGroupList = courseService.listClassGroup();
        model.addAttribute("classGroupList", classGroupList);
        return "admin/course/selectGroupSubjectList";
    }

    @RequestMapping("/groupSubjectList/{classGroupId}")
    public String classGroupSubjectList(@PathVariable("classGroupId") long classGroupId, Model model,
                                        HttpServletRequest request)
            throws ClassGroupNotFoundException, CourseNotFoundException, UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        model.addAttribute("classGroup", classGroup);
        List<Subject> subjectList = subjectService.findSubjectByCourse(classGroup.getCourse().getCourseId());
        model.addAttribute("subjectList", subjectList);
        List<GroupSubject> GroupSubjectList = courseService.findGroupSubjectByClassGroup(classGroupId);
        model.addAttribute("GroupSubjectList", GroupSubjectList);
        List<User> teacherList = userService.findUserByRole(new PageRequest(0, 35), User.Role.TEACHER).getContent();
        model.addAttribute("teacherList", teacherList);
        return "admin/course/groupSubjectList";
    }

    @RequestMapping(value = "/addGroupSubject", method = RequestMethod.POST)
    public String addClassGroupSubject(@Valid GroupSubjectCreateForm form, BindingResult bindingResult) throws DuplicateGroupSubjectException {

        Long classGroupId = form.getClassGroup().getClassGroupId();
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/course/groupSubjectList/"+classGroupId;
        }
        else {
            courseService.addGroupSubject(form.getClassGroup(), form.getSubject(), form.getTeacher());
            return "redirect:/admin/course/groupSubjectList/"+classGroupId;
        }

    }

    @RequestMapping(value = "/groupSubjectEdit/{groupSubjectId}", method = RequestMethod.GET)
    public String showGroupSubjectEditForm(@PathVariable("groupSubjectId") Long groupSubjectId,  HttpServletRequest request,
                                    Model model) throws GroupSubjectNotFoundException, UserNotFoundException,
                                                        CourseNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        GroupSubject gs = courseService.findGroupSubjectById(groupSubjectId);
        List<Subject> subjectList = subjectService.findSubjectByCourse(gs.getClassGroup().getCourse().getCourseId());
        Page<User> userList = userService.findUserByRole(new PageRequest(0, 25), User.Role.TEACHER);
        model.addAttribute("groupSubject", gs);
        model.addAttribute("userList", userList.getContent());
        model.addAttribute("subjectList", subjectList);
        return "admin/course/groupSubjectEdit";
    }

    @RequestMapping(value = "/groupSubjectEdit/{groupSubjectId}", method = RequestMethod.POST)
    public String editClassGroupSubject(@PathVariable("groupSubjectId") Long groupSubjectId,
                                        GroupSubjectCreateForm form) throws GroupSubjectNotFoundException {

        GroupSubject gs = courseService.findGroupSubjectById(groupSubjectId);
        courseService.updateGroupSubject(groupSubjectId, form);
        return "redirect:/admin/course/groupSubjectList/"+gs.getClassGroup().getClassGroupId();
    }

    @RequestMapping(value = "/deleteGroupSubject/{groupSubjectId}", method = RequestMethod.GET)
    public String deleteClassGroupSubject(@PathVariable("groupSubjectId") long groupSubjectId, Model model,
                                          HttpServletRequest request)
            throws GroupSubjectNotFoundException, UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        GroupSubject groupSubject = courseService.findGroupSubjectById(groupSubjectId);
        Long classGroupId = groupSubject.getClassGroup().getClassGroupId();
        courseService.deleteGroupSubject(groupSubject.getGroupSubjectId());
        return "redirect:/admin/course/groupSubjectList/"+classGroupId;
    }

}
