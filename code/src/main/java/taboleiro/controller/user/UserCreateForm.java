package taboleiro.controller.user;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import taboleiro.model.domain.user.User;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateForm {

    @NotEmpty
    private String loginName;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String password;

    @NotNull
    private User.Role role;

}