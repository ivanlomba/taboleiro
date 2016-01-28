package taboleiro.model.domain.course;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.subject.Subject;

@Data
@Entity(name = "schedule")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    public enum WeekDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

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
            name = "classHour", referencedColumnName = "classHourLevelId"
    )
    private ClassHourLevel classHour;

    @Enumerated(EnumType.STRING)
    private WeekDay weekDay;



}
