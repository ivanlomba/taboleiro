package taboleiro.controller.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.student.GlobalGrade.Evaluation;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalGradeCreateForm {

    @NotNull
    private Long student;

    @NotNull
    private Long subject;

    @NotNull
    private Evaluation evaluation;

    @NotNull
    private Long grade;

    private String observation;


}