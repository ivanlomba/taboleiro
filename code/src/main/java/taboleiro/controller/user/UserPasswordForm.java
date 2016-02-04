package taboleiro.controller.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordForm {

    @NotEmpty
    private String password;

    @NotEmpty
    private String repeatPassword;

}