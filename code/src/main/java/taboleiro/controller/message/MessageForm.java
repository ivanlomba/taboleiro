package taboleiro.controller.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageForm {

    @NotNull
    private Long sender;

    @NotNull
    private Long addressee;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String message;

}
