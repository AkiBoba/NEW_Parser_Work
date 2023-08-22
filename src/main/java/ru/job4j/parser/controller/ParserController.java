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

    @PostMapping("/parser")
    public void getList(Model model, @RequestParam String url) {
        Set<String> urls = new HashSet<>();
//        String url = "https://www.autoopt.ru/catalog/557946-bolt_m10h1_25h35_kamaz_vala_kardannogo_v_sbore_k_p__10_9_azotirovannaja_stal_megapower";
        urls.add(url);
        log.info("Поступил запрос на парсинг сайта {}", url);
//        aoparserUtil.getProductsInfo(url);
//        aoparserUtil.getLinks(url);
        aoparserUtil.parseByCatsLinks(urls);
    }

}
