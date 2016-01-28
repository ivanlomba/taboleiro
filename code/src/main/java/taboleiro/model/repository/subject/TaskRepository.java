package taboleiro.model.repository.subject;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.subject.Task;
import taboleiro.model.domain.subject.Subject;
import taboleiro.model.domain.student.GlobalGrade.Evaluation;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends CrudRepository<Task, Long> {

    @Query("SELECT t FROM task t WHERE t.taskName = :taskName AND t.subject.subjectId = :subject" +
            " AND t.taskDate = :taskDate AND t.classGroup.classGroupId = :classGroup AND t.evaluation = :evaluation")
    Task findTaskByTaskNameAndSubjectAndTaskDateAndClassGroupAndEvaluation(@Param("taskName")String taskName,
                                                                           @Param("subject")Long subject,
                                                                           @Param("taskDate")LocalDate taskDate,
                                                                           @Param("classGroup")Long classGroup,
                                                                           @Param("evaluation")Evaluation evaluation);

    Task findTaskByTaskId(Long taskId);

    Page<Task> findAll(Pageable page);

    @Query("SELECT t FROM task t WHERE t.classGroup.classGroupId = :classGroup")
    Page<Task> findTaskByClassGroup(Pageable page,  @Param("classGroup") Long classGroup);

    @Query("SELECT t FROM task t WHERE t.classGroup.classGroupId = :classGroup AND t.taskDate >= CURRENT_DATE ORDER BY t.taskDate")
    Page<Task> findPendingTaskByClassGroup(Pageable page, @Param("classGroup") Long classGroup);

    @Query("SELECT t FROM task t WHERE t.classGroup.classGroupId = :classGroup AND t.taskDate <= CURRENT_DATE ORDER BY t.taskDate")
    Page<Task> findOldTaskByClassGroup(Pageable page, @Param("classGroup") Long classGroup);

    @Query("SELECT t FROM task t WHERE t.classGroup.classGroupId = :classGroup AND t.taskType='EXAM'" +
            "AND t.taskDate >= CURRENT_DATE ORDER BY t.taskDate ASC")
    Page<Task> findExams(Pageable page, @Param("classGroup") Long classGroup);

    @Query("SELECT t FROM task t WHERE t.classGroup.classGroupId = :classGroup AND t.taskType !='EXAM' " +
            "AND t.taskDate >= CURRENT_DATE ORDER BY t.taskDate ASC")
    Page<Task> findProjectAndHomework(Pageable page, @Param("classGroup") Long classGroup);

    @Query("SELECT t FROM task t WHERE t.classGroup.classGroupId = :classGroup AND t.subject.subjectId = :subject ORDER BY t.taskDate DESC")
    Page<Task> findTaskByClassGroupAndSubject(Pageable page, @Param("classGroup") Long classGroup,
                                              @Param("subject") Long subject);

    @Query("SELECT t FROM task t WHERE t.classGroup.classGroupId = :classGroup AND t.subject.subjectId = :subject " +
            "AND t.taskDate <= CURRENT_DATE ORDER BY t.taskDate DESC")
    Page<Task> findOldTaskByClassGroupAndSubject(Pageable page, @Param("classGroup") Long classGroup,
                                              @Param("subject") Long subject);

}
