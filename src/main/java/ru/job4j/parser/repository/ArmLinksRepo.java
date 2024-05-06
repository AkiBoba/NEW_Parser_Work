package ru.job4j.parser.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.parser.domain.ArmatekLink;

@Repository
public interface ArmLinksRepo extends CrudRepository<ArmatekLink, Long> {
}
