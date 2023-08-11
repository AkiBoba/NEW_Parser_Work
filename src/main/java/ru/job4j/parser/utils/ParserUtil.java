package ru.job4j.parser.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ParserUtil {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";

    public List<String> getLinks(String url) {
        List<String> resultLinks = new ArrayList<>();
        List<String> firstLinks = getFirstLinks(url);
        log.info("Получен первичный список ссылок размером {}", firstLinks.size());
//        List<String> secondLinks = getSecondLinks(firstLinks);

        return firstLinks;
    }

    List<String> getFirstLinks(String url) {
        List<String> links = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
            List<Element> list = doc.getElementsByAttributeValue("class", "iva-item-titleStep-pdebR");
            list.forEach(element -> {
                Elements href = element.children();
                String h = href.attr("href");
                links.add("https://www.avito.ru" + h);
                log.info(h);
            });
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
        return links;
    }

    List<String> getSecondLinks(List<String> urls) {
        List<String> links = new ArrayList<>();
        urls.forEach(url -> {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                if (doc != null) {
                    List<Element> list = doc.getElementsMatchingText("реальный пробег");
                    if (!list.isEmpty()) {
                        links.add(url);
                    }
                }
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        });
        return links;
    }



}
