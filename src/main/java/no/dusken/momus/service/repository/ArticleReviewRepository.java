package no.dusken.momus.service.repository;

import no.dusken.momus.model.ArticleReview;
import no.dusken.momus.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleReviewRepository extends JpaRepository<ArticleReview, Long> {

}
