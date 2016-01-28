package taboleiro.model.domain.course;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity(name = "course")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    public enum CourseLevel {
        INFANTIL, PRIMARIA, SECUNDARIA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @NotNull
    private String courseName;

    @Enumerated(EnumType.STRING)
    private CourseLevel courseLevel;

}
