package taboleiro.model.repository.course;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import taboleiro.model.domain.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {

    Page<Course> findAll(Pageable pageable);

    @Query("SELECT c FROM course c ORDER BY c.courseLevel")
    List<Course> findAll();

    Course findCourseByCourseId(Long courseId);

    Course findCourseByCourseName(String courseName);

    Page<Course> findCourseByCourseNameContainingIgnoringCase(Pageable pageable, String courseName);

    Page<Course> findCourseByCourseLevel(Pageable pageable, Course.CourseLevel courseLevel);

}
