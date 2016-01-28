package taboleiro.model.repository.subject;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import taboleiro.model.domain.subject.Attendance;
import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> {

    Page<Attendance> findAll(Pageable page);

    Attendance findAttendanceByAttendanceId(Long AttendanceId);

    @Query("SELECT att FROM attendance att WHERE att.student.studentId = :student" +
            " AND att.subject.subjectId = :subject AND att.faultDate = :faultDate")
    Attendance findAttendanceByStudentAndSubjectAndFaultDate(@Param("student")Long student,
                                                             @Param("subject")Long subject,
                                                             @Param("faultDate")LocalDate faultDate);

    @Query("SELECT att FROM attendance att WHERE att.student.studentId = :student")
    Page<Attendance> findAttendanceByStudent(Pageable page, @Param("student")Long student);

    @Query("SELECT att FROM attendance att WHERE att.classGroup.classGroupId = :classGroup")
    Page<Attendance> findAttendanceByClassGroup(Pageable page, @Param("classGroup")Long classGroup);

    @Query("SELECT att FROM attendance att WHERE att.classGroup.classGroupId = :classGroup" +
            " AND att.subject.subjectId = :subject AND att.faultDate = :faultDate")
    List<Attendance> findAttendanceByClassGroupAndSubjectAndFaultDate(@Param("classGroup")Long classGroup,
                                                                      @Param("subject")Long subject,
                                                                      @Param("faultDate")LocalDate faultDate);

    @Query("SELECT COUNT(att) from attendance att" +
            " WHERE att.student.studentId = :student and att.classGroup.classGroupId = :classGroup and att.justified = false")
    Integer countAttendanceByStudentNotJustified(@Param("student")Long student,
                                                 @Param("classGroup")Long classGroup);

    @Query("SELECT att from attendance att " +
            "WHERE att.student.studentId = :student and att.classGroup.classGroupId = :classGroup and att.justified = false")
    Page<Attendance> findNotJustifiedAttendanceByStudent(Pageable page,
                                                         @Param("student")Long student,
                                                         @Param("classGroup")Long classGroup);

    @Query("SELECT att from attendance att " +
            "WHERE att.student.studentId = :student and att.classGroup.classGroupId = :classGroup and att.justified = true")
    Page<Attendance> findJustifiedAttendanceByStudent(Pageable page,
                                                      @Param("student")Long student,
                                                      @Param("classGroup")Long classGroup);

    @Query("SELECT att from attendance att " +
            "WHERE att.subject.subjectId = :subject and att.classGroup.classGroupId = :classGroup and att.justified = true")
    Page<Attendance> findAttendanceByClassGroupAndSubjectAndJustified(@Param("classGroup")Long classGroup,
                                                                      @Param("subject")Long subject,
                                                                      Pageable page);

    @Query("SELECT att from attendance att " +
            "WHERE att.subject.subjectId = :subject and att.classGroup.classGroupId = :classGroup and att.justified = false")
    Page<Attendance> findAttendanceByClassGroupAndSubjectAndNotJustified(@Param("classGroup")Long classGroup,
                                                                      @Param("subject")Long subject, Pageable page);

}
