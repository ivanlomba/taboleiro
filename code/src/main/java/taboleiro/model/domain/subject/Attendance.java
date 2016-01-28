package taboleiro.model.domain.subject;

import lombok.*;
import taboleiro.model.domain.student.Student;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.service.util.LocalDateAttributeConverter;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity(name = "attendance")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    public enum FaultType {
        ATTENDANCE, PUNCTUALITY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

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

    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate faultDate;

    private boolean justified;

    @ManyToOne
    @JoinColumn(
            name = "classGroup", referencedColumnName = "classGroupId"
    )
    private ClassGroup classGroup;

    @Enumerated(EnumType.STRING)
    private FaultType faultType;

}
