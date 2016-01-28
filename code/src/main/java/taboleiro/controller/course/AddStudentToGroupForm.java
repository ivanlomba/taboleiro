package taboleiro.controller.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.course.ClassGroup;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStudentToGroupForm {

    @NotEmpty
    private ClassGroup classGroup;

    @NotEmpty
    private Student student;

}