package hexlet.code.specification;

import hexlet.code.dto.filter.TaskFilter;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskFilter filter) {
        return withTitleCont(filter.getTitleCont())
                .and(withAssigneeId(filter.getAssigneeId()))
                .and(withStatus(filter.getStatus()))
                .and(withLabelId(filter.getLabelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, criteriaBuilder) -> {
            if (titleCont == null || titleCont.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + titleCont.toLowerCase() + "%"
            );
        };
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, criteriaBuilder) -> {
            if (assigneeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, criteriaBuilder) -> {
            if (labelId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("labels").get("id"), labelId);
        };
    }
}
