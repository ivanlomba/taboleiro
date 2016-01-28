package taboleiro.model.domain.student;

import lombok.*;
import javax.persistence.*;
import taboleiro.model.domain.course.SchoolYear;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.course.Schedule;

@Data
@Entity(name = "global_grade")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalGrade {

    public enum Evaluation {
        FIRST("1"), SECOND("2"), THIRD("3");

        private final String xadeEval;

        Evaluation(final String xadeEval) {
            this.xadeEval = xadeEval;
        }

        @Override
        public String toString() {
            return xadeEval;
        }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long globalGradeId;

    @ManyToOne
    @JoinColumn(
            name = "schoolYear", referencedColumnName = "schoolYearId"
    )
    private SchoolYear schoolYear;

    @ManyToOne
    @JoinColumn(
            name = "student", referencedColumnName = "studentId"
    )
    private Student student;

    @ManyToOne
    @JoinColumn(
            name = "subject", referencedColumnName = "subjectId"
    )
    private Subject subject;

    @Enumerated(EnumType.STRING)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(
            name = "grade", referencedColumnName = "GlobalGradeLevelId"
    )
    private GlobalGradeLevel grade;

    private String observation;

}
