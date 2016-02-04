package taboleiro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import taboleiro.model.exception.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import taboleiro.model.service.user.UserService;
import taboleiro.model.domain.user.User;
import taboleiro.controller.user.UserCreateForm;
import taboleiro.model.service.util.PasswordEncoderGenerator;
import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TaboleiroApplication.class)
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void findUserById() throws UserNotFoundException, DuplicateUserException {
        User user = userService.createUser("jktoole", "John", "K Toole", "necios", "", "", User.Role.USER);
        userService.createUser("hmelville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        assertThat(userService.findUserByUserId(user.getUserId())).isEqualTo(user);
    }

    @Test (expected = UserNotFoundException.class)
    public void findUserNotFoundById() throws UserNotFoundException, DuplicateUserException {
        userService.findUserByUserId(1L);
    }

    @Test
    public void registerUserTest() throws DuplicateUserException, UserNotFoundException {
        User user = userService.createUser("jktoole", "John", "K Toole", "necios", "", "", User.Role.USER);
        assertThat(userService.findUserByLoginName("jktoole")).isEqualTo(user);
    }

    @Test(expected = DuplicateUserException.class)
    public void registerDuplicatedUserTest() throws DuplicateUserException, UserNotFoundException{
        userService.createUser("jktoole", "John", "K Toole", "necios", "", "", User.Role.USER);
        userService.createUser("jktoole", "John", "K Toole", "necios", "", "", User.Role.USER);
    }

    @Test
    public void findUserByLoginNameTest() throws UserNotFoundException, DuplicateUserException {
        User user = userService.createUser("jktoole", "John", "K Toole", "necios", "", "", User.Role.USER);
        assertThat(userService.findUserByLoginName("jktoole")).isEqualTo(user);
    }

    @Test(expected= UserNotFoundException.class)
    public void findUserByLoginNameNotFoundTest() throws UserNotFoundException {
        userService.findUserByLoginName("asdflkñ");
    }

    @Test
    public void findUserByRoleTest() throws UserNotFoundException, DuplicateUserException {
        User user = userService.createUser("jktoole", "John", "K Toole", "necios", "", "", User.Role.USER);
        userService.createUser("hmelville", "Herman", "Melville", "mobydick", "", "", User.Role.TEACHER);
        assertThat(userService.findUserByRole(new PageRequest(0, 20), User.Role.USER)).contains(user);
    }

    @Test
    public void changePasswordTest() throws UserNotFoundException, IncorrectPasswordException,
            DuplicateUserException {

        userService.createUser("jktoole", "John", "K Toole", "necios", "", "", User.Role.USER);
        User u1 = userService.findUserByLoginName("jktoole");
        userService.changePassword(u1.getUserId(), "conjura");

        assertThat(PasswordEncoderGenerator.encoder().matches("conjura",
                userService.findUserByLoginName("jktoole").getPassword())).isTrue();
    }

    @Test(expected= UserNotFoundException.class)
    public void deleteUserNotFoundTest() throws UserNotFoundException, DuplicateUserException {
        User user = userService.createUser("elindo", "Elvira", "Lindo", "manolito", "", "", User.Role.USER);
        userService.deleteUser(user.getUserId());
        userService.findUserByLoginName("elindo");
    }

    @Test()
    public void deleteUserTest() throws UserNotFoundException, DuplicateUserException {
        User user = userService.createUser("elindo", "Elvira", "Lindo", "manolito", "", "", User.Role.USER);
        assertThat(userService.listUser(new PageRequest(0,10)).getContent()).hasSize(1);
        userService.deleteUser(user.getUserId());
        assertThat(userService.listUser(new PageRequest(0,10)).getContent()).hasSize(0);
    }

    @Test
    public void findAllTest() throws UserNotFoundException, DuplicateUserException {
        User u = userService.createUser("hemingway", "Ernesto", "Hemingway", "viejoymar", "", "", User.Role.USER);
        userService.createUser("mcervantes", "Miguel", "De Cervantes", "manco", "", "", User.Role.USER);
        Page<User> p = userService.listUser(new PageRequest(0, 20));
        assertThat(p).hasSize(2).contains(u);
    }

    @Test
    public void createUserTest() throws DuplicateStudentException, UserNotFoundException, DuplicateUserException,
            ChildrenListNotFoundException {

        UserCreateForm u = UserCreateForm.builder().loginName("gmelville").firstName("German").lastName("Melville")
                .password("mobydick").phoneNumber("666555444").email("gmelville@gmail.com").role(User.Role.USER)
                .build();
        userService.createUser(u.getLoginName(), u.getFirstName(), u.getLastName(), u.getPassword(), u.getPhoneNumber(),
                u.getEmail(), u.getRole());
        assertThat(userService.findUserByLoginName("gmelville").getLastName()).isEqualTo("Melville");
    }

    @Test
    public void updateUserTest() throws DuplicateStudentException, UserNotFoundException, DuplicateUserException {

        UserCreateForm u = UserCreateForm.builder().loginName("gmelville").firstName("German").lastName("Melville")
                .password("mobydick").phoneNumber("666555444").email("gmelville@gmail.com").role(User.Role.USER)
                .build();
        userService.createUser(u.getLoginName(), u.getFirstName(), u.getLastName(), u.getPassword(), u.getPhoneNumber(),
                u.getEmail(), u.getRole());
        User userUpdate = userService.findUserByLoginName("gmelville");
        Long userId = userUpdate.getUserId();
        UserCreateForm updateForm = UserCreateForm.builder().loginName("gmelville").firstName("Herman")
                .lastName("Melville").phoneNumber("666555444").email("gmelville@gmail.com").role(User.Role.USER)
                .build();
        userService.editUser(userId, updateForm.getFirstName(), updateForm.getLastName(), updateForm.getEmail(),
                updateForm.getPhoneNumber(), updateForm.getRole());
        assertThat(userService.findUserByLoginName("gmelville").getFirstName()).isEqualTo("Herman");
    }

}