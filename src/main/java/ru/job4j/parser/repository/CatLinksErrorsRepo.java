package ru.job4j.parser.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.parser.domain.CatLinksErrors;

public interface CatLinksErrorsRepo extends CrudRepository<CatLinksErrors, Long> {
}
