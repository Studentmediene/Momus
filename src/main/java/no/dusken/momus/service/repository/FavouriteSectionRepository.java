package no.dusken.momus.service.repository;

import no.dusken.momus.model.FavouriteSection;
import no.dusken.momus.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteSectionRepository extends JpaRepository<FavouriteSection, Long> {

    public FavouriteSection findByOwner(Person owner);

    public FavouriteSection findByOwner_Id(Long owner);
}
