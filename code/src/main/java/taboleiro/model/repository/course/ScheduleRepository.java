package taboleiro.model.repository.course;

import taboleiro.model.domain.course.Schedule;
import taboleiro.model.domain.course.ClassHourLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

    Page<Schedule> findAll(Pageable page);

    Schedule findScheduleByScheduleId(Long scheduleId);

    @Query("SELECT s FROM schedule s WHERE s.classGroup.classGroupId = :classGroup AND s.subject.subjectId = :subject" +
            " AND s.weekDay = :day AND s.classHour = :classHour")
    Schedule findByClassGroupAndSubjectAndWeekDayAndClassHour(@Param("classGroup")Long classGroup,
                                                              @Param("subject")Long subject,
                                                              @Param("day")Schedule.WeekDay day,
                                                              @Param("classHour")ClassHourLevel classHour);

    @Query("SELECT s FROM schedule s WHERE s.classGroup.classGroupId = :classGroup ORDER BY s.weekDay")
    List<Schedule> findByClassGroup(@Param("classGroup")Long classGroup);

    @Query("SELECT s FROM schedule s WHERE s.classGroup.classGroupId = :classGroup AND s.classHour = :classHour")
    List<Schedule> findByClassGroupAndClassHour(@Param("classGroup")Long classGroup,
                                                              @Param("classHour")ClassHourLevel classHour);

    @Query("SELECT s FROM schedule s WHERE s.classGroup.classGroupId = :classGroup AND s.weekDay = :day" +
            " ORDER BY s.weekDay")
    List<Schedule> findScheduleByClassGroupAndWeekDay(@Param("classGroup")Long classGroup,
                                                      @Param("day")Schedule.WeekDay day);

    @Query("SELECT s FROM schedule s WHERE s.subject.subjectId = :subject")
    Page<Schedule> findBySubject(Pageable page, @Param("subject")Long subject);


}
