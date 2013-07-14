package no.dusken.momus.service.repository;

import no.dusken.momus.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {


}
