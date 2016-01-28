package taboleiro.model.domain.course;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.student.Student;
import java.util.Set;

@Data
@Entity(name = "class_group")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude={"studentList","tutor"})
public class ClassGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classGroupId;

    @NotNull
    private String classGroupName;

    @ManyToOne
    @JoinColumn(
            name = "course", referencedColumnName = "courseId"
    )
    private Course course;

    @ManyToOne
    @JoinColumn(
            name = "tutor", referencedColumnName = "userId"
    )
    private User tutor;

    @ManyToOne
    @JoinColumn(
            name = "schoolYear", referencedColumnName = "schoolYearId"
    )
    private SchoolYear schoolYear;

    @ManyToMany(mappedBy = "classGroups")
    private Set<Student> studentList;

    /*
     * codigoGrupo is a Xade field (to export globlalGrades)
     */
    private String codigoGrupo;

}
