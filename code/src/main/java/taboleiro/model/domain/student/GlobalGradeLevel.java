package taboleiro.model.domain.student;

import lombok.*;
import taboleiro.model.domain.course.Course;
import java.util.Set;

import javax.persistence.*;

@Data
@Entity(name = "global_grade_level")
@Builder
@EqualsAndHashCode(exclude={"globalGrade"})
@NoArgsConstructor
@AllArgsConstructor
public class GlobalGradeLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long globalGradeLevelId;

    private String grade;

    private String gradeName;

    private String xadeGrade;

    @Enumerated(EnumType.STRING)
    private Course.CourseLevel courseLevel;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="grade")
    private Set<GlobalGrade> globalGrade;

}
