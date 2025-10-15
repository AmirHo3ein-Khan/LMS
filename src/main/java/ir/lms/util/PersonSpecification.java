package ir.lms.util;

import ir.lms.model.Person;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PersonSpecification {
    public static Specification<Person> searchPersonByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern));

            Join<Person, ?> accountJoin = root.join("account", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(accountJoin.get("username")), pattern));

            Join<Person, ?> majorJoin = root.join("major", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(majorJoin.get("name")), pattern));

            Join<Person, ?> roleJoin = root.join("roles", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(roleJoin.get("name")), pattern));

            query.distinct(true);

            return criteriaBuilder.or(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}


