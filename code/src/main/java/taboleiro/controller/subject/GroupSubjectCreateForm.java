package taboleiro.controller.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.user.User;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSubjectCreateForm {

    @NotNull
    private Subject subject;

    @NotNull
    private User teacher;

    @NotNull
    private ClassGroup classGroup;

}