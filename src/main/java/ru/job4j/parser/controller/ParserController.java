package ru.job4j.parser.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    /**
     * Метод получает адрес, где находятся ссылки на все каталоги сайта
     * @param url
     */
    @PostMapping("/parser")
    public void getList(@RequestParam String url) {
        log.info("Поступил запрос на парсинг сайта {}", url);
//        aoparserUtil.getLinks(url);
        List<String> urls = new ArrayList<>();
        urls.add("https://armtek.ru/brand/aircomfort-175");
        aoparserUtil.getAllPaginationLinks(urls);
    }

}
