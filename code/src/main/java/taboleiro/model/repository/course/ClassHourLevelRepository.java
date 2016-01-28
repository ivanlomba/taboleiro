package taboleiro.model.repository.course;

import taboleiro.model.domain.course.ClassHourLevel;
import taboleiro.model.domain.course.Course.CourseLevel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface ClassHourLevelRepository extends CrudRepository<ClassHourLevel, Long> {

    ClassHourLevel findByCourseLevelAndClassHour(CourseLevel courseLevel, ClassHourLevel.ClassHour classHour);

    Page<ClassHourLevel> findByCourseLevel(Pageable pageable, CourseLevel courseLevel);

    Page<ClassHourLevel> findAll(Pageable pageable);

    List<ClassHourLevel> findClassHourByCourseLevel(CourseLevel courseLevel);

}
