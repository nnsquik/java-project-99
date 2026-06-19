package hexlet.code.service;

import hexlet.code.dto.filter.TaskFilter;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.interfaces.TaskService;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    @Override
    public List<TaskDTO> getAll(TaskFilter filter) {
        var spec = taskSpecification.build(filter);
        return taskRepository.findAll(spec).stream()
                .map(taskMapper::map)
                .toList();
    }

    @Override
    public TaskDTO getById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.map(task);
    }

    @Override
    public TaskDTO create(TaskCreateDTO dto) {
        var task = taskMapper.map(dto);

        var status = taskStatusRepository.findBySlug(dto.getStatus())
                .orElseThrow(() -> new RuntimeException("Status not found"));
        task.setTaskStatus(status);

        if (dto.getAssigneeId() != null) {
            var assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignee(assignee);
        }

        if (dto.getTaskLabelIds() != null && !dto.getTaskLabelIds().isEmpty()) {
            var labels = labelRepository.findAllById(dto.getTaskLabelIds());
            task.setLabels(new HashSet<>(labels));
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Override
    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        taskMapper.update(dto, task);

        if (dto.getStatus() != null && dto.getStatus().isPresent()) {
            var status = taskStatusRepository.findBySlug(dto.getStatus().get())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            task.setTaskStatus(status);
        }

        if (dto.getAssigneeId() != null && dto.getAssigneeId().isPresent()) {
            var assignee = userRepository.findById(dto.getAssigneeId().get())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignee(assignee);
        }

        if (dto.getTaskLabelIds() != null && dto.getTaskLabelIds().isPresent()) {
            var labels = labelRepository.findAllById(dto.getTaskLabelIds().get());
            task.setLabels(new HashSet<>(labels));
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
