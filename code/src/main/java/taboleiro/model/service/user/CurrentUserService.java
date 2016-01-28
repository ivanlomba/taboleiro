package taboleiro.model.service.user;


public interface CurrentUserService {

        boolean canAccessUser(CurrentUser currentUser, Long userId);

}