package taboleiro.controller.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.student.GlobalGrade;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalGradeLevelForm {

    @NotNull
    private Course.CourseLevel courseLevel;

    @NotEmpty
    private String grade;

    @NotEmpty
    private String gradeName;

    private String xadeGrade;

}