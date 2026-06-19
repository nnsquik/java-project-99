package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    List<Task> findAll(org.springframework.data.jpa.domain.Specification<Task> spec);
}
