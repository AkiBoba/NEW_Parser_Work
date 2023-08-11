package ru.job4j.parser.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.parser.utils.AutooptParserUtil;
import ru.job4j.parser.utils.ParserUtil;

import java.util.List;

@Slf4j
@RestController
public class ParserController {

    private final ParserUtil parserUtil;
    private final AutooptParserUtil aoparserUtil;

    public ParserController(ParserUtil parserUtil, AutooptParserUtil aoparserUtil) {
        this.parserUtil = parserUtil;
        this.aoparserUtil = aoparserUtil;
    }

    @PostMapping("/parser")
    public List<String> getList(Model model, @RequestParam String url) {
        log.info("Поступил запрос на парсинг сайта {}", url);
        return aoparserUtil.getLinks(url);
    }

}
