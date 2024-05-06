package ru.job4j.parser.domain;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("armatek_link")
public class ArmatekLink implements Comparable<ArmatekLink> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String link;

    public ArmatekLink(String link) {
        this.link = link;
    }

    @Override
    public int compareTo(ArmatekLink otherArmLink) {
        return this.link.compareTo(otherArmLink.getLink());
    }
}
