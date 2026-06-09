package hexlet.code.interfaces;

import hexlet.code.dto.filter.TaskFilter;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getAll();
    List<TaskDTO> getAll(TaskFilter filter);
    TaskDTO getById(Long id);
    TaskDTO create(TaskCreateDTO dto);
    TaskDTO update(Long id, TaskUpdateDTO dto);
    void delete(Long id);
}
