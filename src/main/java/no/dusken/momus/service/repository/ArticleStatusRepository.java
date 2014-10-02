package no.dusken.momus.service.repository;

import no.dusken.momus.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleStatusRepository extends JpaRepository<ArticleStatus, Long> {

}
