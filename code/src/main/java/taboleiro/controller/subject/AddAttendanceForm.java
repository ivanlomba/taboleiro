package taboleiro.controller.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.subject.Attendance.FaultType;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAttendanceForm {

    @NotNull
    private Long student;

    @NotNull
    private Long subject;

    @NotNull
    private Long classGroup;

    private boolean justified = false;

    @NotNull
    private FaultType faultType;

}