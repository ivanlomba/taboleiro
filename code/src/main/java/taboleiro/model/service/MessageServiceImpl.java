package taboleiro.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import taboleiro.controller.message.MessageForm;
import taboleiro.controller.message.GroupMessageForm;
import taboleiro.model.domain.message.Message;
import taboleiro.model.domain.user.User;
import taboleiro.model.exception.ClassGroupNotFoundException;
import taboleiro.model.exception.MessageNotFoundException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.service.user.UserService;
import taboleiro.model.repository.message.MessageRepository;
import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.course.ClassGroup;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private UserService userService;
    private CourseService courseService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserService userService,
                              CourseService courseService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.courseService = courseService;
    }

    @Override
    @Transactional
    public Message findMessageByMessageId(Long messageId) throws MessageNotFoundException {
        checkNotNull(messageId, "messageId");

        Message message = messageRepository.findMessageByMessageId(messageId);
        if (message == null) {
            throw new MessageNotFoundException(messageId, "MessageId Not Found");
        }
        else {
            return message;
        }
    }

    @Override
    @Transactional
    public Message createMessage(Long addresseeId, Long senderId, String subject, String text)
            throws UserNotFoundException {
        checkNotNull(addresseeId, "addresseeId");
        checkNotNull(senderId, "senderId");
        checkNotNull(subject, "subject");
        checkNotNull(text, "text");

        User addressee = userService.findUserByUserId(addresseeId);
        User sender = userService.findUserByUserId(senderId);
        LocalDateTime date = LocalDateTime.now(ZoneId.of("GMT+1"));
        Message message = Message.builder().addressee(addressee).sender(sender).subject(subject).message(text)
                .viewed(false).messageDate(date).build();
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public Message sendMessage(MessageForm form) throws UserNotFoundException {
        checkNotNull(form, "form");

        User addressee = userService.findUserByUserId(form.getAddressee());
        User sender = userService.findUserByUserId(form.getSender());
        LocalDateTime date = LocalDateTime.now(ZoneId.of("GMT+1"));
        Message senderMessage = Message.builder()
                .addressee(addressee).sender(sender).copy(sender)
                .subject(form.getSubject())
                .message(form.getMessage())
                .viewed(false).messageDate(date).build();
        Message addresseeMessage = Message.builder()
                .addressee(addressee).sender(sender).copy(addressee)
                .subject(form.getSubject())
                .message(form.getMessage())
                .viewed(false).messageDate(date).build();
        messageRepository.save(addresseeMessage);
        return messageRepository.save(senderMessage);
    }

    @Override
    @Transactional
    public void sendGroupMessage(GroupMessageForm form)
            throws UserNotFoundException, ClassGroupNotFoundException {
        checkNotNull(form, "form");

        User sender = userService.findUserByUserId(form.getSender());
        LocalDateTime date = LocalDateTime.now(ZoneId.of("GMT+1"));
        ClassGroup cg = courseService.findClassGroupById(form.getClassGroup());
        Set<Student> studentList = cg.getStudentList();
        Iterator studentIterator = studentList.iterator();
        Set<User> addresseeList = new HashSet<>();
        while(studentIterator.hasNext()) {
            Student s = (Student)studentIterator.next();
            addresseeList.add(s.getGuardian());
        }
        Iterator addresseeIterator = addresseeList.iterator();
        while(addresseeIterator.hasNext()) {
            User addressee = (User)addresseeIterator.next();
            Message addresseeMessage = Message.builder()
                    .addressee(addressee).sender(sender).copy(addressee)
                    .subject(form.getSubject())
                    .message(form.getMessage())
                    .viewed(false).messageDate(date).build();
            messageRepository.save(addresseeMessage);
        }
    }

    @Override
    @Transactional
    public Message readMessage(Long messageId, Long userId) throws MessageNotFoundException {
        checkNotNull(messageId, "messageId");

        Message m = messageRepository.findMessageByMessageId(messageId);
        if (m == null) {
            throw new MessageNotFoundException(messageId,"Message Not Found");
        }
        else if(m.getCopy().getUserId().equals(userId)) {
            m.setViewed(true);
            return m;
        }
        else { // if the message is not property of the current user
            throw new MessageNotFoundException(messageId,"Error retrieving message");
        }
    }

    @Override
    @Transactional
    public Page<Message> findMessageByAddressee(Long userId, Pageable page) throws UserNotFoundException {
        checkNotNull(userId, "userId");

        return messageRepository.findMessageByAddresseeAndCopyOrderByMessageDateDesc(page, userId, userId);
    }

    @Override
    @Transactional
    public Page<Message> findMessageBySender(Long userId, Pageable page) throws UserNotFoundException {
        checkNotNull(userId, "userId");

        return messageRepository.findMessageBySenderAndCopyOrderByMessageDateDesc(page, userId, userId);
    }

    @Override
    @Transactional
    public Integer countNewMail(Long userId) {
        checkNotNull(userId, "userId");

        return messageRepository.countNotViewedMail(userId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) throws MessageNotFoundException {
        checkNotNull(messageId, "messageId");

        Message m = messageRepository.findMessageByMessageId(messageId);
        if (m == null) {
            throw new MessageNotFoundException(messageId,"Message Not Found");
        }
        else {
            messageRepository.delete(messageId);
        }
    }

    @Override
    @Transactional
    public void markUnread(Long messageId) throws MessageNotFoundException {
        checkNotNull(messageId, "messageId");

        Message m = messageRepository.findMessageByMessageId(messageId);
        if (m == null) {
            throw new MessageNotFoundException(messageId,"Message Not Found");
        }
        else {
            m.setViewed(false);
        }
    }

    @Override
    @Transactional
    public Set<User> findStudentAddressee(Student student) throws ClassGroupNotFoundException {
        checkNotNull(student, "student");

        ClassGroup cg = student.getCurrentClassGroup();
        Set<User> addressee = courseService.findTeacherByClassGroup(cg.getClassGroupId());
        List<User> adminList = userService.findAdmin();
        addressee.addAll(adminList);
        return addressee;
    }

    @Override
    @Transactional
    public Set<User> findStaffAddressee() {
        Set<User> addressee = new HashSet<>();
        addressee.addAll(userService.findAdmin());
        addressee.addAll(userService.findTeacher());
        return addressee;
    }
}
