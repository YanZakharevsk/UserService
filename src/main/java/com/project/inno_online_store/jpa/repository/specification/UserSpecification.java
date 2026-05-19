package com.project.inno_online_store.jpa.repository.specification;

import com.project.inno_online_store.jpa.entity.User;
import com.project.inno_online_store.jpa.repository.filter.UserFilter;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static final String NAME = "name";
    public static final String SURNAME = "surname";

    public UserSpecification() {
    }

    public static Specification<User> filterBy(UserFilter userFilter){
        return Specification
                .where(hasName(userFilter.name()))
                .and(hasSurname(userFilter.surname()));
    }

    private static Specification<User> hasName(String name) {
        return (
                (root, query, criteriaBuilder) ->
                        name == null || name.isEmpty() ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get(NAME), name));
    }

    private static Specification<User> hasSurname(String surname) {
        return (
                (root, query, criteriaBuilder) ->
                        surname == null || surname.isEmpty() ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get(SURNAME), surname));
    }

}
