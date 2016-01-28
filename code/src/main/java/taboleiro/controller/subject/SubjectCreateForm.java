package taboleiro.controller.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import taboleiro.model.domain.course.Course;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCreateForm {

    @NotEmpty
    private String subjectName;

    @NotNull
    private Course course;

    private String codigoMateriaAvaliable;

}