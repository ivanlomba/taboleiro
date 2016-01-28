package taboleiro.controller.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.domain.subject.Task.TaskType;
import taboleiro.model.domain.subject.Subject;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateForm {

    @NotNull
    private Subject subject;

    @NotNull
    private ClassGroup classGroup;

    @NotNull
    private String taskName;

    @NotNull
    private TaskType taskType;

    @NotNull
    private String taskDate;

    @NotNull
    private GlobalGrade.Evaluation evaluation;

}