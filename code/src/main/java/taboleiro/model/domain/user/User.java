package taboleiro.model.domain.user;

import lombok.*;
import taboleiro.model.domain.student.Student;
import javax.persistence.*;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude={"children"})
@Entity(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public enum Role {
        USER, TEACHER, ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String loginName;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy="guardian", cascade = CascadeType.ALL)
    private Set<Student> children;



}
