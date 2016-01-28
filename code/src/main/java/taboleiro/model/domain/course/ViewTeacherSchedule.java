package taboleiro.model.domain.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.course.Schedule.WeekDay;
import javax.persistence.*;

@Data
@Entity(name = "view_teacher_schedule")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewTeacherSchedule {

    @Id
    @Column(insertable = false, updatable = false)
    private Long scheduleId;

    @Column(insertable = false, updatable = false)
    private Long classGroup;

    @Column(insertable = false, updatable = false)
    private String classGroupName;

    @Column(insertable = false, updatable = false)
    private Long groupSubjectId;

    @Column(insertable = false, updatable = false)
    private Long subject;

    @Column(insertable = false, updatable = false)
    private String subjectName;

    @Column(insertable = false, updatable = false)
    private Long teacher;

    @Column(insertable = false, updatable = false)
    private String classStart;

    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private WeekDay weekDay;

}