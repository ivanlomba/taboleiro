package taboleiro.controller.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.course.SchoolYear;
import taboleiro.model.domain.user.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupForm {

    @NotEmpty
    private String classGroupName;

    @NotNull
    private Course course;

    @NotNull
    private User tutor;

    @NotNull
    private SchoolYear schoolYear;

    private String codigoGrupo;

}