package ru.job4j.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.job4j.parser.utils.AutooptParserUtil;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        final AutooptParserUtil aoparserUtil = new AutooptParserUtil();
        String url = "https://www.autoopt.ru/auto/catalog";
//        String url = "https://www.autoopt.ru/auto/catalog/truck";
        SpringApplication.run(Main.class, args);
        System.out.println("Go to http://localhost:8080/index");
        aoparserUtil.getLinks(url);
//        aoparserUtil.getElement(url);
    }
}