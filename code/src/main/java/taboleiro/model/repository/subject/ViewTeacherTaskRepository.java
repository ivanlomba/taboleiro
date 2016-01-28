package taboleiro.model.repository.subject;

import org.springframework.data.repository.CrudRepository;
import taboleiro.model.domain.subject.Task;
import taboleiro.model.domain.subject.ViewTeacherTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ViewTeacherTaskRepository extends CrudRepository<ViewTeacherTask, Long> {

    Page<ViewTeacherTask> findTaskByTeacher(Pageable pageable, Long teacher);

    Page<ViewTeacherTask> findTaskByTeacherAndTaskType(Pageable page, Long teacher, Task.TaskType task);

}
