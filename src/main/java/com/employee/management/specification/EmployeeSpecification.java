package com.employee.management.specification;

import com.employee.management.entity.Employee;
import com.employee.management.entity.EmployeeStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> filterBy(
            String keyword, String department, EmployeeStatus status) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate firstNameMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate lastNameMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")), likePattern);
                Predicate emailMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")), likePattern);
                Predicate codeMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("employeeCode")), likePattern);

                predicates.add(criteriaBuilder.or(
                        firstNameMatch, lastNameMatch, emailMatch, codeMatch));
            }

            if (department != null && !department.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("department").get("name")),
                        department.toLowerCase()));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}