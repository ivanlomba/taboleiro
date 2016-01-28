package taboleiro.controller.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import taboleiro.controller.utils.SessionUtil;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.course.GroupSubject;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.exception.ClassGroupNotFoundException;
import taboleiro.model.exception.GroupSubjectNotFoundException;
import taboleiro.model.exception.StudentNotFoundException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.xade.Xade;
import taboleiro.model.service.xade.XadeConverter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin/xade")
public class XadeController {

    private XadeConverter xadeConverter;
    private CourseService courseService;
    private SessionUtil sessionUtil;

    @Autowired
    public XadeController(XadeConverter xadeConverter, CourseService courseService, SessionUtil sessionUtil) {
        this.xadeConverter = xadeConverter;
        this.courseService = courseService;
        this.sessionUtil = sessionUtil;
    }

    @RequestMapping(value ="/selectGroup")
    public String selectGroup(Model model, HttpServletRequest request) throws UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        model.addAttribute("classGroupList", courseService.listClassGroupByXade());
        return "admin/xade/selectGroup";
    }

    @RequestMapping(value ="/selectSubject/{classGroupId}")
    public String selectSubject(@PathVariable("classGroupId") Long classGroupId,  Model model,
                                HttpServletRequest request) throws ClassGroupNotFoundException, UserNotFoundException {

        model.addAttribute("newMail", sessionUtil.getNewMail(request));
        ClassGroup classGroup = courseService.findClassGroupById(classGroupId);
        model.addAttribute("classGroup", classGroup);
        List<GroupSubject> subjectList = courseService.findGroupSubjectByClassGroup(classGroupId);
        model.addAttribute("subjectList", subjectList);
        return "admin/xade/selectSubject";
    }

    @RequestMapping(value="/download/{groupSubjectId}/eval/{evaluation}", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public Xade downloadXade(@PathVariable("groupSubjectId") Long groupSubjectId,
                             @PathVariable("evaluation") GlobalGrade.Evaluation evaluation)
            throws GroupSubjectNotFoundException, StudentNotFoundException {

        GroupSubject gs = courseService.findGroupSubjectById(groupSubjectId);
        return xadeConverter.groupSubjectToXade(gs, evaluation);
    }
}
