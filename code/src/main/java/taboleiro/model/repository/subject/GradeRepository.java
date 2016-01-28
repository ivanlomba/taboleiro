package taboleiro.model.repository.subject;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.subject.Grade;
import java.util.List;

public interface GradeRepository extends CrudRepository<Grade, Long> {

    Grade findGradeByGradeId(Long gradeId);

    @Query("SELECT g FROM grade g WHERE g.task.taskId = :task AND g.student.studentId = :student")
    Grade findGradeByTaskAndStudent(@Param("task")Long task, @Param("student")Long student);

    @Query("SELECT g FROM grade g WHERE g.task.taskId = :task")
    List<Grade> findGradeByTask(@Param("task")Long task);

    @Query("SELECT g FROM grade g WHERE g.student.studentId = :student")
    Page<Grade> findGradeByStudent(Pageable page, @Param("student")Long student);

}
