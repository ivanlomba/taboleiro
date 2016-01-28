package taboleiro.model.domain.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.student.Student;
import javax.persistence.*;

@Data
@Entity(name = "grade")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;

    @ManyToOne
    @JoinColumn(
            name = "task", referencedColumnName = "taskId"
    )
    private Task task;

    @ManyToOne
    @JoinColumn(
            name = "student", referencedColumnName = "studentId"
    )
    private Student student;

    private Integer grade;

    private String observation;

}
