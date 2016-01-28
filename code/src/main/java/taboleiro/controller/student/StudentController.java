package taboleiro.controller.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import taboleiro.controller.utils.PaginationUtil;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.exception.*;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.user.UserService;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/student")
public class StudentController {

    private final StudentService studentService;
    private final UserService userService;
    private final CourseService courseService;
    private final SessionUtil sessionUtil;
    private final PaginationUtil paginationUtil;

    @Autowired
    public StudentController(StudentService studentService, UserService userService, CourseService courseService,
                             SessionUtil sessionUtil, PaginationUtil paginationUtil) {
        this.studentService = studentService;
        this.userService = userService;
        this.courseService = courseService;
        this.sessionUtil = sessionUtil;
        this.paginationUtil = paginationUtil;
    }

    @RequestMapping(value = "/list")
    public String listStudents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "amount", defaultValue = "25") int amount,
            Model model, HttpServletRequest request) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Page<Student> studentList = studentService.listStudent(new PageRequest(page, amount));
        model.addAttribute("studentList", studentList.getContent());
        Page<User> userList = userService.findUserByRole(new PageRequest(0, 25), User.Role.USER);
        model.addAttribute("userList", userList.getContent());
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        List<ClassGroup> classGroupList = courseService.listClassGroup();
        model.addAttribute("classGroupList", classGroupList);
        model.addAttribute("previousPage", paginationUtil.getPreviousPage(studentList));
        model.addAttribute("nextPage", paginationUtil.getNextPage(studentList));
        return "admin/student/list";
    }

    @RequestMapping(value = "/profile/{studentId}", method = RequestMethod.GET)
    public String studentDetail( @PathVariable("studentId") long studentId, Model model, HttpServletRequest request)
            throws StudentNotFoundException, UserNotFoundException{

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        Student s = studentService.findStudentById(studentId);
        model.addAttribute("student", s);
        model.addAttribute("birthdate", s.getBirthDate().toString());
        return "admin/student/profile";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showCreationForm(Model model, HttpServletRequest request) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        StudentCreateForm form = new StudentCreateForm();
        model.addAttribute("form", form);
        Page<User> userList = userService.findUserByRole(new PageRequest(0, 25), User.Role.USER);
        model.addAttribute("userList", userList.getContent());
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        List<ClassGroup> classGroupList = courseService.listClassGroup();
        model.addAttribute("classGroupList", classGroupList);
        return "admin/student/createForm";
    }

    @RequestMapping(value = "/createForm", method = RequestMethod.POST)
    public String processCreationForm(@Valid StudentCreateForm studentForm, BindingResult bindingResult)
            throws DuplicateStudentException, ClassGroupNotFoundException, UserNotFoundException {

        if(bindingResult.hasErrors()){
            return "redirect:/admin/student/createForm";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(studentForm.getBirthDate(), formatter);
        studentService.addStudent(studentForm.getFirstName(), studentForm.getLastName(), studentForm.getDni(),
                date, studentForm.getGuardian().getUserId(), studentForm.getCurrentClassGroup().getClassGroupId());
        return "redirect:/admin/student/list";
    }

    @RequestMapping(value = "/delete/{studentId}", method = RequestMethod.POST)
    public String deleteStudent(@PathVariable("studentId") Long studentId) throws StudentNotFoundException {

        studentService.deleteStudent(studentId);
        return "redirect:/admin/student/list";
    }

    @RequestMapping(value = "/updateStudent/{studentId}", method = RequestMethod.GET)
    public String showEditForm(@PathVariable("studentId") Long studentId, Model model, HttpServletRequest request)
            throws StudentNotFoundException, UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        List<Course> courseList = courseService.listCourse();
        model.addAttribute("courseList", courseList);
        List<ClassGroup> classGroupList = courseService.listClassGroup();
        model.addAttribute("classGroupList", classGroupList);
        Page<User> userList = userService.findUserByRole(new PageRequest(0,30), User.Role.USER);
        model.addAttribute("userList", userList.getContent());
        Student student = studentService.findStudentById(studentId);
        model.addAttribute("student", student);
        model.addAttribute("currentClassGroup", student.getCurrentClassGroup());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = student.getBirthDate().format(formatter);
        model.addAttribute("date",date);
        return "admin/student/updateStudent";
    }

    @RequestMapping(value = "/updateStudent/{studentId}", method = RequestMethod.POST)
    public String processEditForm(@PathVariable("studentId") Long studentId, @Valid StudentCreateForm form,
                                  BindingResult bindingResult)
            throws StudentNotFoundException, ClassGroupNotFoundException, UserNotFoundException {

        if(bindingResult.hasErrors()){
            return "redirect:/admin/student/updateStudent/"+studentId;
        }
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(form.getBirthDate(), formatter);
            studentService.updateStudent(studentId, form.getFirstName(), form.getLastName(), form.getDni(),
                    date, form.getGuardian().getUserId(), form.getCurrentClassGroup().getClassGroupId(), form.getCodigoAlumno());
            return "redirect:/admin/student/list";
        }
    }

}
