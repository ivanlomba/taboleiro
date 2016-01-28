package taboleiro.model.domain.course;

import javax.persistence.*;
import lombok.*;
import taboleiro.model.domain.course.Course.CourseLevel;
import java.util.Set;

@Data
@Entity(name = "class_hour_level")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(exclude={"schedule"})
@AllArgsConstructor
public class ClassHourLevel {

    public enum ClassHour {
        FIRST_HOUR, SECOND_HOUR, THIRD_HOUR, FOURTH_HOUR, FIFTH_HOUR, SIXTH_HOUR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classHourLevelId;

    @Enumerated(EnumType.STRING)
    private CourseLevel courseLevel;

    @Enumerated(EnumType.STRING)
    private ClassHour classHour;

    private String classStart;

    private String classEnd;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="classHour")
    private Set<Schedule> schedule;

}
