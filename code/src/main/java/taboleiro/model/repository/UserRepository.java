package taboleiro.model.repository;

import taboleiro.model.domain.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUserId(Long userId);

    User findByLoginNameAllIgnoringCase(String userName);

    Page<User> findUserByRole(Pageable page, User.Role role);

    List<User> findUserByRole(User.Role role);

    Page<User> findAll(Pageable pageable);

}
