package taboleiro.model.repository.course;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import taboleiro.model.domain.course.ClassGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ClassGroupRepository extends CrudRepository<ClassGroup, Long> {

    Page<ClassGroup> findAll(Pageable pageable);

    @Query("SELECT c FROM class_group c ORDER BY c.course")
    List<ClassGroup> findAll();

    @Query("SELECT c FROM class_group c WHERE c.codigoGrupo IS NOT NULL")
    List<ClassGroup> findExportableClassGroup();

    ClassGroup findClassGroupByClassGroupId(Long classGroupId);

    ClassGroup findClassGroupByClassGroupName(String classGroup);

    @Query("SELECT c FROM class_group c WHERE c.classGroupName = :classGroupName AND" +
            " c.schoolYear.schoolYearId = :schoolYear")
    ClassGroup findClassGroupByClassGroupNameAndSchoolYear(@Param("classGroupName") String classGroupName,
                                                           @Param("schoolYear")Long schoolYear);

    @Query("SELECT c FROM class_group c WHERE c.course.courseId = :courseId")
    Page<ClassGroup> findClassGroupByCourse(Pageable pageable, @Param("courseId") Long courseId);

    @Query("SELECT c FROM class_group c WHERE c.schoolYear.schoolYearId = :schoolYear")
    Page<ClassGroup> findClassGroupBySchoolYear(Pageable pageable, @Param("schoolYear") Long schoolYear);

    @Query("SELECT c FROM class_group c WHERE c.tutor.userId = :tutor")
    Page<ClassGroup> findClassGroupByTutor(Pageable pageable, @Param("tutor")Long tutor);

}
