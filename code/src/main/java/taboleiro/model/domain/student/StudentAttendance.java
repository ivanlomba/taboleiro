package taboleiro.model.domain.student;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendance {

    private Long studentId;

    private String studentFirstName;

    private String studentLastName;

    private String fault;

}