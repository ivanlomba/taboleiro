package taboleiro.model.repository.course;

import org.springframework.data.repository.CrudRepository;
import taboleiro.model.domain.course.Schedule;
import taboleiro.model.domain.course.ViewTeacherSchedule;
import java.util.List;

public interface ViewTeacherScheduleRepository extends CrudRepository<ViewTeacherSchedule, Long> {

    List<ViewTeacherSchedule> findByTeacher(Long teacher);

    List<ViewTeacherSchedule> findByTeacherAndWeekDay(Long teacher, Schedule.WeekDay weekDay);

}
