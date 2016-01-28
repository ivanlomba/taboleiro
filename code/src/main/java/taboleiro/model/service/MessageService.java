package taboleiro.model.service;

import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.message.Message;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.student.Student;
import taboleiro.controller.message.MessageForm;
import taboleiro.controller.message.GroupMessageForm;
import taboleiro.model.exception.ClassGroupNotFoundException;
import taboleiro.model.exception.MessageNotFoundException;
import taboleiro.model.exception.UserNotFoundException;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface MessageService {

    Message findMessageByMessageId(Long messageId) throws MessageNotFoundException;

    Message createMessage(Long addresseeId, Long senderId, String subject, String text) throws UserNotFoundException;

    Message sendMessage(MessageForm form) throws UserNotFoundException;

    void sendGroupMessage(GroupMessageForm form) throws UserNotFoundException, ClassGroupNotFoundException;

    Message readMessage(Long messageId, Long userId) throws MessageNotFoundException;

    Page<Message> findMessageByAddressee(Long userId, Pageable page) throws UserNotFoundException;

    Page<Message> findMessageBySender(Long userId, Pageable page) throws UserNotFoundException;

    Integer countNewMail(Long userId);

    void deleteMessage(Long messageId) throws MessageNotFoundException;

    void markUnread(Long messageId) throws MessageNotFoundException;

    Set<User> findStudentAddressee(Student student) throws ClassGroupNotFoundException;

    Set<User> findStaffAddressee();

}