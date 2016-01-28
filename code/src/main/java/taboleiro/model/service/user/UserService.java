package taboleiro.model.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.user.User;
import java.util.List;
import taboleiro.model.exception.ChildrenListNotFoundException;
import taboleiro.model.exception.DuplicateUserException;
import taboleiro.model.exception.UserNotFoundException;
import taboleiro.model.exception.IncorrectPasswordException;
import java.util.Set;

public interface UserService {

    User findUserByLoginName(String loginName) throws UserNotFoundException;

    User getUserByLoginName(String loginName);

    User findUserByUserId(Long userId) throws UserNotFoundException;

    User createUser(String loginName, String firstName, String lastName, String password, String phoneNumber,
                    String email, User.Role role) throws DuplicateUserException ;

    Page<User> findUserByRole(Pageable p, User.Role role);

    List<User> findAdmin();

    List<User> findTeacher();

    Page<User> listUser(Pageable p);

    void changePassword(Long userId, String newPassword)
            throws UserNotFoundException, IncorrectPasswordException;

    void deleteUser(Long userId) throws UserNotFoundException;

    Set<Student> listChildren(User user) throws ChildrenListNotFoundException;

    User editUser(Long userId, String firstName, String lastName, String email, String phoneNumber, User.Role role)
            throws UserNotFoundException;

    User editUser(Long userId, String firstName, String lastName, String email, String phoneNumber)
            throws UserNotFoundException;
}