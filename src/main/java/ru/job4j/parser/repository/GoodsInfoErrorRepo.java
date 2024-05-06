package ru.job4j.parser.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.parser.domain.GoodsInfoError;

public interface GoodsInfoErrorRepo extends CrudRepository<GoodsInfoError, Long> {
}
