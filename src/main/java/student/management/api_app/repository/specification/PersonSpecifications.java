package student.management.api_app.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import student.management.api_app.model.Person;

import java.time.LocalDate;

public class PersonSpecifications {

    public static Specification<Person> fullNameContains(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
            return cb.like(cb.lower(root.get("fullName")), SpecUtils.likePattern(keyword));
        };
    }

    public static Predicate fullNameContains(
            Join<?, Person> personJoin, CriteriaBuilder cb, String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        return cb.like(cb.lower(personJoin.get("fullName")), SpecUtils.likePattern(keyword));
    }

    public static Specification<Person> phoneEquals(String phone) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(phone)) return null;
            return cb.equal(root.get("phone"), phone);
        };
    }

    public static Predicate phoneEquals(
            Join<?, Person> personJoin, CriteriaBuilder cb, String phone) {
        if (!StringUtils.hasText(phone)) return null;
        return cb.equal(personJoin.get("phone"), phone);
    }

    public static Specification<Person> emailContains(String email) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(email)) return null;
            return cb.like(root.get("contactEmail"), SpecUtils.likePattern(email));
        };
    }

    public static Predicate emailContains(
            Join<?, Person> personJoin, CriteriaBuilder cb, String email) {
        if (!StringUtils.hasText(email)) return null;
        return cb.like(personJoin.get("contactEmail"), SpecUtils.likePattern(email));
    }

    public static Specification<Person> addressContains(String address) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(address)) return null;
            return cb.like(cb.lower(root.get("address")), SpecUtils.likePattern(address));
        };
    }

    public static Predicate addressContains(
            Join<?, Person> personJoin, CriteriaBuilder cb, String address) {
        if (!StringUtils.hasText(address)) return null;
        return cb.like(personJoin.get("address"), SpecUtils.likePattern(address));
    }

    public static Specification<Person> dobGte(LocalDate from) {
        return (root, query, cb) -> {
            if (from == null) return null;
            return cb.greaterThanOrEqualTo(root.get("dob"), from);
        };
    }

    public static Predicate dobGte(
            Join<?, Person> personJoin, CriteriaBuilder cb, LocalDate from) {
        if (from == null) return null;
        return cb.greaterThanOrEqualTo(personJoin.get("dob"), from);
    }

    public static Specification<Person> dobLte(LocalDate to) {
        return (root, query, cb) -> {
            if (to == null) return null;
            return cb.lessThanOrEqualTo(root.get("dob"), to);
        };
    }

    public static Predicate dobLte(
            Join<?, Person> personJoin, CriteriaBuilder cb, LocalDate to) {
        if (to == null) return null;
        return cb.lessThanOrEqualTo(personJoin.get("dob"), to);
    }
}
