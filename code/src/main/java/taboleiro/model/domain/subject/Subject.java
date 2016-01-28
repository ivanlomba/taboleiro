package taboleiro.model.domain.subject;

import javax.persistence.*;
import lombok.*;
import taboleiro.model.domain.course.Course;


@Data
@Entity(name = "subject")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude={"schedule, groupSubject"})
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subjectId;

    private String subjectName;

    @ManyToOne
    @JoinColumn(
            name = "course", referencedColumnName = "courseId"
    )
    private Course course;

    /*
     * codigoMateriaAvaliable is a Xade field (to export globlalGrades)
     */
    private String codigoMateriaAvaliable;

}
