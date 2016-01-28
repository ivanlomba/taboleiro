package taboleiro;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import taboleiro.controller.message.MessageForm;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.course.SchoolYear;
import taboleiro.model.domain.message.Message;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.student.Student;
import taboleiro.model.exception.*;
import taboleiro.model.service.CourseService;
import taboleiro.model.service.MessageService;
import taboleiro.model.service.StudentService;
import taboleiro.model.service.SubjectService;
import taboleiro.model.service.user.UserService;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TaboleiroApplication.class)
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
public class MessageServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    CourseService courseService;

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;


    @Before
    public void setUpMessage() throws DuplicateUserException, UserNotFoundException, MessageNotFoundException {

        User u1 = userService.createUser("newton", "Isaac", "Newton", "calculus", "123123", "newton@cambridge.com",
                User.Role.ADMIN);
        User u2= userService.createUser("preverte", "Arturo", "Pérez Reverte", "dumas", "321321", "reverte@rae.es",
                User.Role.TEACHER);
        MessageForm form = new MessageForm(u2.getUserId(), u1.getUserId(), "calculus book",
                "Hi, Do you think we could buy a book on calculus?");
        messageService.sendMessage(form);
        assertThat(messageService.findMessageBySender(u2.getUserId(), new PageRequest(0, 10)).getContent()).hasSize(1);
    }

    /*
     *  Message Operations
     */

    @Test
    public void findMessageByIdTest() throws DuplicateUserException, UserNotFoundException, MessageNotFoundException {

        User u1 = userService.findUserByLoginName("newton");
        User u2 = userService.findUserByLoginName("preverte");
        Message m = messageService.createMessage(u1.getUserId(), u2.getUserId(), "I need a favor",
                "Please, send me one of your students.");
        assertThat(messageService.findMessageByMessageId(m.getMessageId())).isEqualTo(m);
    }

    @Test
    public void findMessageByAddresseeTest() throws UserNotFoundException, MessageNotFoundException {
        User u1 = userService.findUserByLoginName("newton");
        User u2 = userService.findUserByLoginName("preverte");
        assertThat(messageService.findMessageByAddressee(u1.getUserId(), new PageRequest(0,10)).getContent()).hasSize(1);
        MessageForm form = new MessageForm(u2.getUserId(), u1.getUserId(), "Calculus book II",
                "Hi, I can't find a book cheaper than 30€!");
        messageService.sendMessage(form);
        assertThat(messageService.findMessageByAddressee(u1.getUserId(), new PageRequest(0,10)).getContent()).hasSize(2);
    }

    @Test
    public void findMessageBySenderTest() throws UserNotFoundException, MessageNotFoundException {
        User u1 = userService.findUserByLoginName("newton");
        User u2 = userService.findUserByLoginName("preverte");
        MessageForm form = new MessageForm(u2.getUserId(), u1.getUserId(), "Calculus book II",
                "Hi, I can't find a book cheaper than 30€!");
        messageService.sendMessage(form);
        assertThat(messageService.findMessageBySender(u2.getUserId(), new PageRequest(0, 10)).getContent()).hasSize(2);
        messageService.sendMessage(form);
        assertThat(messageService.findMessageBySender(u2.getUserId(), new PageRequest(0,10)).getContent()).hasSize(3);
    }

    @Test
    public void deleteMessageTest() throws UserNotFoundException, MessageNotFoundException {
        User u1 = userService.findUserByLoginName("newton");
        User u2 = userService.findUserByLoginName("preverte");
        MessageForm form = new MessageForm(u2.getUserId(), u1.getUserId(), "Calculus book II",
                "Hi, I can't find a book cheaper than 30€!");
        Message m = messageService.sendMessage(form);
        assertThat(messageService.findMessageBySender(u2.getUserId(), new PageRequest(0, 10)).getContent()).hasSize(2);
        messageService.deleteMessage(m.getMessageId());
        assertThat(messageService.findMessageBySender(u2.getUserId(), new PageRequest(0, 10)).getContent()).hasSize(1);
    }

    @Test
    public void countNewMailTest() throws UserNotFoundException, MessageNotFoundException {
        User u1 = userService.findUserByLoginName("newton");
        User u2 = userService.findUserByLoginName("preverte");
        MessageForm form = new MessageForm(u2.getUserId(), u1.getUserId(), "Calculus book II",
                "Hi, I can't find a book cheaper than 30€!");
        messageService.sendMessage(form);
        assertThat(messageService.findMessageBySender(u2.getUserId(), new PageRequest(0, 10)).getContent()).hasSize(2);
        assertThat(messageService.countNewMail(u1.getUserId())).isEqualTo(2);
    }

    @Test
    public void readMailTest() throws UserNotFoundException, MessageNotFoundException {
        User u1 = userService.findUserByLoginName("newton");
        User u2 = userService.findUserByLoginName("preverte");
        MessageForm form = new MessageForm(u2.getUserId(), u1.getUserId(), "Calculus book II",
                "Hi, I can't find a book cheaper than 30€!");
        messageService.sendMessage(form);
        assertThat(messageService.findMessageBySender(u2.getUserId(), new PageRequest(0, 10)).getContent()).hasSize(2);
        assertThat(messageService.countNewMail(u1.getUserId())).isEqualTo(2);
        Page<Message> messageList = messageService.findMessageByAddressee(u1.getUserId(), new PageRequest(0, 20));
        Message m = messageList.getContent().get(1);
        messageService.readMessage(m.getMessageId(), u1.getUserId());
        assertThat(messageService.countNewMail(u1.getUserId())).isEqualTo(1);
    }

    @Test
    public void markUnreadTest() throws UserNotFoundException, MessageNotFoundException {
        User u1 = userService.findUserByLoginName("newton");
        User u2 = userService.findUserByLoginName("preverte");
        MessageForm form = new MessageForm(u2.getUserId(), u1.getUserId(), "Calculus book II",
                "Hi, I can't find a book cheaper than 30€!");
        messageService.sendMessage(form);
        Page<Message> messageList = messageService.findMessageByAddressee(u1.getUserId(), new PageRequest(0, 20));
        Message m = messageList.getContent().get(1);
        messageService.readMessage(m.getMessageId(), u1.getUserId());
        assertThat(messageService.countNewMail(u1.getUserId())).isEqualTo(1);
        messageService.markUnread(m.getMessageId());
        assertThat(messageService.countNewMail(u1.getUserId())).isEqualTo(2);
    }

    @Test
    public void findStaffAddresseeTest() throws DuplicateUserException {
        userService.createUser("picasso", "Pablo", "Picasso", "guernica", "", "", User.Role.USER);
        User teacher = userService.createUser("steinbeck", "John", "Steinbeck", "uvas", "", "", User.Role.TEACHER);
        User teacher2 = userService.createUser("hemingway", "Ernest", "Hemingway", "mar", "", "", User.Role.TEACHER);
        assertThat(messageService.findStaffAddressee()).contains(teacher).contains(teacher2).hasSize(4);
    }

    @Test
    public void findFamilyAddresseeTest() throws UserNotFoundException, MessageNotFoundException,
            DuplicateSchoolYearException, DuplicateCourseException, DuplicateClassGroupException,
            DuplicateUserException, ClassGroupNotFoundException, DuplicateStudentException, StudentNotFoundException {

        SchoolYear sy = courseService.addSchoolYear("2016-2017");
        User u1 = userService.findUserByLoginName("preverte");
        User father = userService.createUser("picasso", "Pablo", "Picasso", "guernica", "", "", User.Role.USER);
        Course c1 = courseService.addCourse("1º Bach", Course.CourseLevel.SECUNDARIA);
        ClassGroup cg1 = courseService.addClassGroup("1B - A Ciencias", c1.getCourseId(), u1, sy.getSchoolYearId(),
                "23462");
        Student s = studentService.addStudent("Claude", "Picasso", "234234W", LocalDate.of(2010, Month.FEBRUARY, 10),
                father.getUserId(), cg1.getClassGroupId());
        assertThat(messageService.findStudentAddressee(s)).hasSize(1);
    }
}