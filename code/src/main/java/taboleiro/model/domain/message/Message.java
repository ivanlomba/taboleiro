package taboleiro.model.domain.message;

import lombok.*;
import javax.persistence.*;
import taboleiro.model.domain.user.User;
import taboleiro.model.service.util.LocalDateTimeAttributeConverter;
import java.time.LocalDateTime;

@Data
@Entity(name = "message")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(
            name = "addressee", referencedColumnName = "userId"
    )
    private User addressee;

    @ManyToOne
    @JoinColumn(
            name = "sender", referencedColumnName = "userId"
    )
    private User sender;

    @ManyToOne
    @JoinColumn(
            name = "copy", referencedColumnName = "userId"
    )
    private User copy;

    private String subject;

    private String message;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime messageDate;

    private boolean viewed;

}
