package taboleiro.controller.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import taboleiro.model.domain.course.ClassHourLevel;
import taboleiro.model.domain.course.Course.CourseLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourLevelForm {

    @NotNull
    private CourseLevel courseLevel;

    @NotNull
    private ClassHourLevel.ClassHour classHour;

    @NotEmpty
    private String classStart;

    @NotEmpty
    private String classEnd;

}