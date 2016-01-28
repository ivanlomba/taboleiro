package taboleiro.model.domain.course;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "school_year")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schoolYearId;

    @NotNull
    private String schoolYearName;

    private Boolean visible;
}
