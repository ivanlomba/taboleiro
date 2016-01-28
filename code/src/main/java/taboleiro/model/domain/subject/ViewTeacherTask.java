package taboleiro.model.domain.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taboleiro.model.domain.subject.Task.TaskType;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity(name = "view_teacher_task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewTeacherTask {

    @Id
    @Column(insertable = false, updatable = false)
    private Long taskId;

    @Column(insertable = false, updatable = false)
    private String taskName;

    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Column(insertable = false, updatable = false)
    private LocalDate taskDate;

    @Column(insertable = false, updatable = false)
    private String subjectName;

    @Column(insertable = false, updatable = false)
    private String classGroupName;

    @Column(insertable = false, updatable = false)
    private Long teacher;

}
