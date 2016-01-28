package taboleiro.controller.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import taboleiro.model.domain.course.Schedule;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.ChildrenListNotFoundException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.service.MessageService;
import taboleiro.model.service.user.UserService;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

@Component
public class SessionUtil {

    private UserService userService;
    private MessageService messageService;

    @Autowired
    public SessionUtil(UserService userService, MessageService messageService){
        this.userService = userService;
        this.messageService = messageService;
    }

    public Student getCurrentChild(HttpServletRequest request)
            throws UserNotFoundException, ChildrenListNotFoundException {

        User u = userService.findUserByLoginName(request.getRemoteUser());
        Set<Student> childrenList = userService.listChildren(u);
        Student student;
        if (request.getSession().getAttribute("child") == null) {
            student = childrenList.iterator().next();
            request.getSession().setAttribute("child", student);
        }
        else {
            student = (Student)request.getSession().getAttribute("child");
        }
        return student;
    }

    public Integer getNewMail(HttpServletRequest request) throws UserNotFoundException {
        Long userId = userService.findUserByLoginName(request.getRemoteUser()).getUserId();
        return messageService.countNewMail(userId);
    }

    public String getDay() {
        SimpleDateFormat formato =
                new SimpleDateFormat("EEEE, d MMM yyyy", new Locale("es", "ES")); /*/MMM/yyyy*/
        formato.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String fecha = formato.format(new Date());
        return fecha;
    }

    public Schedule.WeekDay getWeekDay() {
        SimpleDateFormat formato =
                new SimpleDateFormat("EEEE");
        formato.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String fecha = formato.format(new Date()).toUpperCase();
        return Schedule.WeekDay.valueOf(fecha);
    }

}