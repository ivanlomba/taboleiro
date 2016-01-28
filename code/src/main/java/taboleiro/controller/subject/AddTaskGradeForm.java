package taboleiro.controller.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTaskGradeForm {

    @NotNull
    private Long student;

    @NotNull
    private Long task;

    @NotNull
    private Integer grade;

    private String observation;

}