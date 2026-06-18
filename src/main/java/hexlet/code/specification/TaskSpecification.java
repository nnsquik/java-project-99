package hexlet.code.specification;

import hexlet.code.dto.filter.TaskFilter;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.JoinType;
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

    // загружаем все связи одним запросом
    private Specification<Task> withFetchJoins() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("taskStatus", JoinType.LEFT);
                root.fetch("assignee", JoinType.LEFT);
                root.fetch("labels", JoinType.LEFT);
            }
            query.distinct(true);
            return criteriaBuilder.conjunction();
        };
    }

    // фильтр по названию - содержит подстроку
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

    // фильтр по исполнителю
    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, criteriaBuilder) -> {
            if (assigneeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    // фильтр по статусу (slug)
    private Specification<Task> withStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    // фильтр по метке
    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, criteriaBuilder) -> {
            if (labelId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("labels").get("id"), labelId);
        };
    }
}
