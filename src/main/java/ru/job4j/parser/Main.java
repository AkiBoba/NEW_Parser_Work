package ru.job4j.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.job4j.parser.repository.GoodInfoRepository;
import ru.job4j.parser.utils.AutooptParserUtil;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
//        final AutooptParserUtil aoparserUtil = new AutooptParserUtil(goodInfoRepository);
        String url = "https://www.autoopt.ru/catalog/016353-mehanizm_rulevoj_gaz_53_oao_gaz_";
//        String url = "https://www.autoopt.ru/auto/catalog/truck";
        SpringApplication.run(Main.class, args);
        System.out.println("Go to http://localhost:8080/index");
//        aoparserUtil.getProducktsInfo(url);
//        aoparserUtil.getLinks(url);
//        aoparserUtil.getElement(url);
    }
}