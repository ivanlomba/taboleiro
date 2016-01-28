package taboleiro.controller.course;

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
public class CourseCreateForm {

    @NotEmpty
    private String courseName;

    @NotNull
    private Course.CourseLevel courseLevel;

}