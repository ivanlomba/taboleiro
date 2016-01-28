package taboleiro.controller.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.course.ClassHourLevel;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.course.Schedule;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleForm {

    @NotNull
    private ClassGroup classGroup;

    @NotNull
    private Subject subject;

    @NotNull
    private ClassHourLevel classHour;

    @NotNull
    private Schedule.WeekDay weekDay;

}