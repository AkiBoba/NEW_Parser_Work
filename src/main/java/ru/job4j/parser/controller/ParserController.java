package ru.job4j.parser.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.parser.utils.AutooptParserUtil;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParserController {

    private final AutooptParserUtil aoparserUtil;

//    @PostMapping("/parser")
//    public List<String> getList(Model model, @RequestParam String url) {
//        log.info("Поступил запрос на парсинг сайта {}", url);
//        return aoparserUtil.getLinks(url);
//    }

    @PostMapping("/parser")
    public void getList(Model model, @RequestParam String url) {
        Set<String> urls = new HashSet<>();
        urls.add(url);
        log.info("Поступил запрос на парсинг сайта {}", url);
//        aoparserUtil.getProductsInfo(urls);
        aoparserUtil.getLinks(url);
    }

}
