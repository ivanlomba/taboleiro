package taboleiro.model.repository.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.student.GlobalGradeLevel;
import java.util.List;

public interface GlobalGradeLevelRepository extends CrudRepository<GlobalGradeLevel, Long> {

    Page<GlobalGradeLevel> findAll(Pageable page);

    GlobalGradeLevel findGlobalGradeLevelByGlobalGradeLevelId(Long globalGradeLevelId);

    GlobalGradeLevel findGlobalGradeLevelByGradeAndCourseLevel(String grade, Course.CourseLevel courseLevel);

    List<GlobalGradeLevel> findGlobalGradeLevelByCourseLevel(Course.CourseLevel courseLevel);

}
