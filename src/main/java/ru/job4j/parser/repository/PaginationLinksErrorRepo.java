package ru.job4j.parser.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.parser.domain.PaginationLinksError;

public interface PaginationLinksErrorRepo extends CrudRepository<PaginationLinksError, Long> {
}
