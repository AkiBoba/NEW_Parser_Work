package ru.job4j.parser.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.parser.domain.GoodsLinksError;

public interface GoodsLinksErrorRepo extends CrudRepository<GoodsLinksError, Long> {
}
