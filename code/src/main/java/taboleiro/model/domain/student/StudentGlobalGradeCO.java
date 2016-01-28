package taboleiro.model.domain.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGlobalGradeCO {

    private Long studentId;

    private String studentFirstName;

    private String studentLastName;

    private String globalGradeEv1;

    private String globalGradeEv2;

    private String globalGradeEv3;

}