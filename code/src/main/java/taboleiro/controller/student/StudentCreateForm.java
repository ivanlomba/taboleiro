package taboleiro.controller.student;

import lombok.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.course.ClassGroup;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateForm {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String dni;

    @NotNull
    private String birthDate;

    @NotNull
    private User guardian;

    @NotNull
    private ClassGroup currentClassGroup;

    private String codigoAlumno;

}