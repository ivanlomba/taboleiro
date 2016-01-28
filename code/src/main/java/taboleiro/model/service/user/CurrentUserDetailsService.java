package taboleiro.model.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import taboleiro.model.domain.user.User;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserDetailsService.class);
    private final UserService userService;

    @Autowired
    public CurrentUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CurrentUser loadUserByUsername(String loginName) throws UsernameNotFoundException {
        LOGGER.debug("Authenticating message with loginName={}");
        User u = userService.getUserByLoginName(checkNotNull(loginName, "loginName"));
        if (u == null) {
            throw new UsernameNotFoundException(String.format("User with loginName=%s was not found", loginName));
        }
        else {
            return new CurrentUser(u);
        }
    }
}