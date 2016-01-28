package taboleiro.model.repository.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.course.GroupSubject;
import java.util.List;

public interface GroupSubjectRepository extends CrudRepository<GroupSubject, Long> {

    GroupSubject findByGroupSubjectId(Long groupSubjectId);

    Page<ClassGroup> findAll(Pageable pageable);

    @Query("SELECT gs FROM group_subject gs WHERE gs.classGroup.classGroupId = :classGroup")
    List<GroupSubject> findByClassGroup(@Param("classGroup")Long classGroup);

    @Query("SELECT gs FROM group_subject gs WHERE gs.classGroup.classGroupId = :classGroup" +
            " AND gs.subject.subjectId = :subject")
    GroupSubject findByClassGroupAndSubject(@Param("classGroup")Long classGroup, @Param("subject")Long subject);

    @Query("SELECT gs FROM group_subject gs WHERE gs.teacher.userId = :teacher AND gs.classGroup.schoolYear.visible = true " +
            "ORDER BY gs.classGroup")
    List<GroupSubject> findByTeacher(@Param("teacher") Long teacher);

}
