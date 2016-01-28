package taboleiro.model.domain.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTaskGrade {

    private Long studentId;

    private String studentFirstName;

    private String studentLastName;

    private Long taskId;

    private String grade;

}
