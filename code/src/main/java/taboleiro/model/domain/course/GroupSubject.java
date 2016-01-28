package taboleiro.model.domain.course;

import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@Entity(name = "group_subject")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(exclude={"teacher"})
@AllArgsConstructor
public class GroupSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupSubjectId;

    @ManyToOne
    @JoinColumn(
            name = "classGroup", referencedColumnName = "classGroupId"
    )
    private ClassGroup classGroup;

    @ManyToOne
    @JoinColumn(
            name = "subject", referencedColumnName = "subjectId"
    )
    private Subject subject;

    @ManyToOne
    @JoinColumn(
            name = "teacher", referencedColumnName = "userId"
    )
    private User teacher;

}