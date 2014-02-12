package no.dusken.momus.service.repository;

import no.dusken.momus.model.SourceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceTagRepository extends JpaRepository<SourceTag, String> {

    @Query("select st from SourceTag st where st.tag not in (select distinct sst.tag from Source s join s.tags sst)")
    public List<SourceTag> getUnusedTags();




}
