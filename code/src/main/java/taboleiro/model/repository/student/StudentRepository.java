package taboleiro.model.repository.student;

import org.springframework.data.repository.query.Param;
import taboleiro.model.domain.student.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Long> {

    Student findStudentByStudentId(Long studentId);

    Student findStudentByDni(String dni);

    Page<Student> findAll(Pageable pageable);

    @Query("SELECT s FROM student s WHERE s.currentClassGroup.classGroupId = :currentClassGroup ORDER BY s.lastName, s.firstName")
    List<Student> findStudentByCurrentClassGroup(@Param("currentClassGroup") Long currentClassGroup);

    @Query("SELECT s FROM student s WHERE s.course.courseId = :course")
    Page<Student> findStudentByCourse(Pageable page, @Param("course")Long course);

    @Query("SELECT s FROM student s WHERE s.guardian.userId = :guardian")
    List<Student> findStudentByGuardian(@Param("guardian")Long guardian);

    @Query("SELECT s FROM student s" +
            " WHERE s.currentClassGroup.classGroupId = :currentClassGroup ORDER BY s.lastName, s.firstName")
    List<Student> findStudentAttendance(@Param("currentClassGroup")Long currentClassGroup);
}

