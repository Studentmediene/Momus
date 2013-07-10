package no.dusken.momus.service;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRevisionRepository extends JpaRepository<ArticleRevision, Long> {

    public List<ArticleRevision> findByArticle(Article article);

    public List<ArticleRevision> findByArticle_Id(Long id);
}
