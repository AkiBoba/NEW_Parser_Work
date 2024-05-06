package ru.job4j.parser.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.parser.utils.ArmtekParserUtil;
import ru.job4j.parser.utils.AutooptParserUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParserController {

    private final AutooptParserUtil aoparserUtil;
    private final ArmtekParserUtil armtekParserUtil;

    /**
     * Метод получает адрес, где находятся ссылки на все каталоги сайта
     * @param url
     */
    @PostMapping("/parser")
    public void getList(@RequestParam String url) {
        log.info("Поступил запрос на парсинг сайта {}", url);
//        aoparserUtil.getLinks(url);
//        List<String> urls = new ArrayList<>();
//        urls.add("https://armtek.ru/brand/aircomfort-175");
//        aoparserUtil.getAllPaginationLinks(urls);
//        urls.add("https://armtek.ru/brand");
//        urls.add("https://armtek.ru/brand/lukoil-3658");
//        urls.add("https://armtek.ru/brand/bosch-934");
//        urls.add("https://armtek.ru/brand/brix-1007");
//        armtekParserUtil.getAllPaginationLinks(urls);

//        armtekParserUtil.getLinks("https://armtek.ru/brand");

//        Set<String> urls = new HashSet<>();
//        String url1 = "https://armtek.ru/product/podkladnaya-shayba---iveco-4440849";
//        String url2 = "https://armtek.ru/product/6020-1889-sx-baraban-tormoznoy--fiat-seicento-0911-98-12228540";
//        String url3 = "https://armtek.ru/product/10w40-1l-moto-ride-basic-4t-sl-motornoe-maslo-62402913";
//        urls.add(url1);
//        urls.add(url2);
//        urls.add(url3);
//        armtekParserUtil.getGoodsInfo(urls);

//        armtekParserUtil.getAllPaginationLinks(List.of("https://armtek.ru/brand/2x3-6"));
//        armtekParserUtil.getAllGoodsLinks(Set.of("https://armtek.ru/brand/stellox-5538"));
//        armtekParserUtil.getAllPaginationLinks(List.of("https://armtek.ru/brand/airtech-190"));

//        armtekParserUtil.getAllPaginationLinks(List.of("https://armtek.ru/brand/stellox-5538"));
//        armtekParserUtil.getAllPaginationLinks(List.of("https://armtek.ru/brand/adr-37"));
        armtekParserUtil.getAllPaginationLinks(List.of("https://armtek.ru/brand/kzae-7015"));

    }

}
