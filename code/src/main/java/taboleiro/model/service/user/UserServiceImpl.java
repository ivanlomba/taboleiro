package taboleiro.model.service.user;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.student.Student;
import taboleiro.model.exception.*;
import taboleiro.model.repository.UserRepository;
import static com.google.common.base.Preconditions.checkNotNull;
import taboleiro.model.service.util.PasswordEncoderGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User findUserByUserId(Long userId) throws UserNotFoundException{
        User u = userRepository.findByUserId(userId);
        if (u == null){
            throw new UserNotFoundException(userId, Student.class.getName());
        } else {
            return u;
        }
    }

    @Transactional
    @Override
    public User getUserByLoginName(String loginName) throws UsernameNotFoundException{ // login service
        return userRepository.findByLoginNameAllIgnoringCase(checkNotNull(loginName, "loginName"));
    }

    @Transactional
    @Override
    public User findUserByLoginName(String loginName) throws UserNotFoundException {

        User u = userRepository.findByLoginNameAllIgnoringCase(checkNotNull(loginName, "loginName"));
        if (u == null) {
            throw new UserNotFoundException(loginName, User.class.getName());
        } else {
            return u;
        }
    }

    @Transactional
    @Override
    public Page<User> findUserByRole(Pageable p, User.Role role){
        checkNotNull(p, "page");
        checkNotNull(role, "role");

        return userRepository.findUserByRole(p, role);
    }

    @Transactional
    @Override
    public List<User> findAdmin() {
        return userRepository.findUserByRole(User.Role.ADMIN);
    }

    @Transactional
    @Override
    public List<User> findTeacher() {
        return userRepository.findUserByRole(User.Role.TEACHER);
    }

    @Transactional
    @Override
    public Page<User> listUser(Pageable p) {
        return userRepository.findAll(p);
    }

    @Transactional
    @Override
    public User createUser(String loginName, String firstName, String lastName, String password, String phoneNumber,
                           String email, User.Role role) throws DuplicateUserException  {
        checkNotNull(loginName, "loginName");

        if(userRepository.findByLoginNameAllIgnoringCase(loginName) != null) {
            throw new DuplicateUserException(loginName, User.class.getName());
        }
        String encryptedPassword = PasswordEncoderGenerator.Encode(checkNotNull((password), "password"));
        User newUser = User.builder().firstName(checkNotNull(firstName, "firstName"))
                .lastName(checkNotNull(lastName, "lastName"))
                .password(encryptedPassword)  //new BCryptPasswordEncoder().encode
                .loginName(loginName)
                .phoneNumber((checkNotNull((phoneNumber), "phoneNumber")))
                .email(checkNotNull((email), "email"))
                .role(checkNotNull(role, "role")).build();
        return userRepository.save(newUser);
    }

    @Transactional
    @Override
    public void changePassword(Long userId, String newPassword)
            throws UserNotFoundException, IncorrectPasswordException {

        User user = userRepository.findByUserId(checkNotNull(userId, "userId"));
        String encryptedPassword = PasswordEncoderGenerator.Encode(checkNotNull(newPassword, "newPassword"));
        user.setPassword(checkNotNull(encryptedPassword, "encryptedPassword"));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        userRepository.delete(checkNotNull(userId, "userId"));
    }

    @Transactional
    @Override
    public Set<Student> listChildren(User user) throws ChildrenListNotFoundException {
        checkNotNull(user, "message");

        Set<Student> childrenList = user.getChildren();
        if (childrenList == null) {
            throw new ChildrenListNotFoundException(user, User.class.getName());
        }
        else {
            return childrenList;
        }
    }

    @Transactional
    @Override
    public User editUser(Long userId, String firstName, String lastName, String email, String phoneNumber,
                         User.Role role) throws UserNotFoundException {
        User u = userRepository.findByUserId(userId);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setPhoneNumber(phoneNumber);
        u.setRole(role);
        return userRepository.save(u);
    }


    @Transactional
    @Override
    public User editUser(Long userId, String firstName, String lastName, String email, String phoneNumber)
            throws UserNotFoundException {
        User u = userRepository.findByUserId(userId);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setPhoneNumber(phoneNumber);
        return userRepository.save(u);
    }

}