package ru.job4j.parser.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.parser.domain.GoodInfo;

public interface GoodInfoRepository extends CrudRepository<GoodInfo, Long> {
    @Modifying
    @Query(value = "UPDATE alliance_goods SET description = :description, width = :width, height = :height, length = :length, weight = :weight WHERE article = :article AND name = :name")
    int update(@Param("description") String description, @Param("width") String width, @Param("height") String height, @Param("length") String length, @Param("weight") String weight, @Param("article") String article, @Param("name") String name);
}
