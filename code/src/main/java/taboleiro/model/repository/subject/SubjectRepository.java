package taboleiro.model.repository.subject;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.subject.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubjectRepository extends CrudRepository<Subject, Long> {

    Page<Subject> findAll(Pageable pageable);

    Subject findSubjectBySubjectId(Long subjectId);

    @Query("SELECT s FROM subject s WHERE s.subjectName = :subjectName AND s.course.courseId = :course")
    Subject findSubjectBySubjectNameAndCourse(@Param("subjectName")String subjectName,
                                              @Param("course")Long course);

    @Query("SELECT s FROM subject s WHERE s.course.courseId = :course")
    List<Subject> findSubjectByCourse(@Param("course")Long course);
}
