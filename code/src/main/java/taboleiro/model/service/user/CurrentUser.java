package taboleiro.model.service.user;

import org.springframework.security.core.authority.AuthorityUtils;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.user.User.Role;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user) {
        super(user.getLoginName(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getUserId();
    }

    public Role getRole() {
        return user.getRole();
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "message=" + user +
                "} " + super.toString();
    }
}