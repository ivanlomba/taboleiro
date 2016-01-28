package taboleiro.model.domain.student;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;
import taboleiro.model.domain.course.Course;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.user.User;
import taboleiro.model.domain.subject.Grade;
import taboleiro.model.domain.subject.Attendance;
import taboleiro.model.service.util.LocalDateAttributeConverter;

@Data
@EqualsAndHashCode(exclude={"classGroup", "guardian", "course", "attendance", "grade", "globalGrade"})
@Entity(name = "student")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(unique=true)
    private String dni;

    private String firstName;

    private String lastName;

    @ManyToOne
    @JoinColumn(
            name = "guardian", referencedColumnName = "userId"
    )
    private User guardian;

    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(
            name = "currentClassGroup", referencedColumnName = "classGroupId"
    )
    private ClassGroup currentClassGroup;

    @ManyToOne
    @JoinColumn(
            name = "course", referencedColumnName = "courseId"
    )
    private Course course;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name = "student_class_group",
            joinColumns = {@JoinColumn(name = "student", referencedColumnName = "studentId")},
            inverseJoinColumns = {@JoinColumn(name = "classGroup", referencedColumnName = "classGroupId")})
    private Set<ClassGroup> classGroups;

    /*
     * codigoAlumno is a Xade field (to export globlalGrades)
     */
    private String codigoAlumno;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="student")
    private Set<Attendance> attendance;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="student")
    private Set<Grade> grade;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="student")
    private Set<GlobalGrade> globalGrade;

}