package no.dusken.momus.service.specification;

import no.dusken.momus.model.Article;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ArticleSpecifications {

    public static Specification<Article> contentContains(final String content) {
        return new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.<String>get("content"), "%" + content + "%");
            }
        };
    }

    public static Specification<Article> nameIs(final String name) {
        return new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("name"), name);
            }
        };
    }

    public static Specification<Article> starter() {
        return new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and();
            }
        };
    }

    public static Specification<Article> photoIdIs(final Long id) {
        return new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("photographers.id"), id);
            }
        };
    }
}
