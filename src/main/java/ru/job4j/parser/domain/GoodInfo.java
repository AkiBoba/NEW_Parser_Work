package ru.job4j.parser.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
//@Entity
//@Table(name = "goods")
public class GoodInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "article")
    private String article;
    @Column(columnDefinition = "name")
    private String name;
    @Column(columnDefinition = "description")
    private String description;
    @Column(columnDefinition = "width")
    private String width;
    @Column(columnDefinition = "height")
    private String height;
    @Column(columnDefinition = "length")
    private String length;
    @Column(columnDefinition = "weight")
    private String weight;

    public GoodInfo(String width, String height, String length, String weight) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.weight = weight;
    }
}