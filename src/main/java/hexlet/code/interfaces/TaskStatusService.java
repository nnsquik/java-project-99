package hexlet.code.interfaces;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatusDTO> getAll();
    TaskStatusDTO getById(Long id);
    TaskStatusDTO create(TaskStatusCreateDTO dto);
    TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto);
    void delete(Long id);
}
