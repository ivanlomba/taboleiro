package taboleiro.model.domain.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGrade {

    private Long taskId;

    private String taskName;

    private LocalDate taskDate;

    private Boolean graded;

}
