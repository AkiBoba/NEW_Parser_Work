package ru.job4j.parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.parser.domain.ArmGoodInfo;

@Repository
public interface ArmtekGoodInfoRepository extends CrudRepository<ArmGoodInfo, Long> {
}
