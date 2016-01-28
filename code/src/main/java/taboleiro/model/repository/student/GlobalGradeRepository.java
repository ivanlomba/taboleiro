package taboleiro.model.repository.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.domain.student.GlobalGrade.Evaluation;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface GlobalGradeRepository extends CrudRepository<GlobalGrade, Long> {

    Page<GlobalGrade> findAll(Pageable pageable);

    GlobalGrade findByGlobalGradeId(Long globalGradeId);

    @Query("select gg FROM global_grade gg WHERE gg.student.studentId = :student AND gg.subject.subjectId = :subject" +
            " AND gg.schoolYear.schoolYearId = :schoolYear AND gg.evaluation = :evaluation")
    GlobalGrade findByStudentAndSubjectAndSchoolYearAndEvaluation(@Param("student")Long student,
                                                                  @Param("subject")Long subject,
                                                                  @Param("schoolYear")Long schoolYear,
                                                                  @Param("evaluation")Evaluation evaluation);


    @Query("select gg FROM global_grade gg WHERE gg.subject.subjectId = :subject" +
            " AND gg.schoolYear.schoolYearId = :schoolYear")
    Page<GlobalGrade> findGlobalGradeBySubjectAndSchoolYear(Pageable page, @Param("subject")Long subject,
                                                            @Param("schoolYear")Long schoolYear);

    @Query("select gg FROM global_grade gg WHERE gg.subject.subjectId = :subject" +
            " AND gg.schoolYear.schoolYearId = :schoolYear")
    List<GlobalGrade> findGlobalGradeBySubjectAndSchoolYear(@Param("subject")Long subject,
                                                            @Param("schoolYear")Long schoolYear);

    @Query("select gg FROM global_grade gg WHERE gg.student.studentId = :student")
    List<GlobalGrade> findGlobalGradeByStudent(@Param("student")Long student); //TODO PAGE?

    @Query("select gg FROM global_grade gg WHERE gg.student.studentId = :student AND" +
            " gg.schoolYear.schoolYearId = :schoolYear ORDER BY gg.evaluation DESC")
    List<GlobalGrade> findGlobalGradeByStudentAndSchoolYearOrderByEvaluationDesc(@Param("student")Long student,
                                                                                 @Param("schoolYear")Long schoolYear);

    @Query("select gg FROM global_grade gg WHERE gg.subject.subjectId = :subject AND" +
            " gg.schoolYear.schoolYearId = :schoolYear AND gg.evaluation = :evaluation")
    List<GlobalGrade> findGlobalGradeBySubjectAndSchoolYearAndEvaluation(@Param("subject")Long subject,
                                                                         @Param("schoolYear")Long schoolYear,
                                                                         @Param("evaluation")Evaluation evaluation);
}

