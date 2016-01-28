package taboleiro.model.domain.subject;

import lombok.*;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.student.GlobalGrade;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity(name = "task")
@Builder
@EqualsAndHashCode(exclude={"grade"})
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    public enum TaskType {
        EXAM, HOMEWORK, PROJECT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String taskName;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    private LocalDate taskDate;

    @ManyToOne
    @JoinColumn(
            name = "subject", referencedColumnName = "subjectId"
    )
    private Subject subject;

    @ManyToOne
    @JoinColumn(
            name = "classGroup", referencedColumnName = "classGroupId"
    )
    private ClassGroup classGroup;

    @Enumerated(EnumType.STRING)
    private GlobalGrade.Evaluation evaluation;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="task")
    private Set<Grade> grade;
}
