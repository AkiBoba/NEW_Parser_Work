package ru.job4j.parser.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AutooptParserUtil {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";

    /** Метод парсит страницу url и получает ссылки на все каталоги
     * @param url
     * @return список адресов каталогов
     */
    public List<String> getLinks(String url) {
        List<String> links = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
            List<Element> list = doc.getElementsByAttributeValue("class", "brand-item");
            list.forEach(element -> {
                if (element != null) {
                    links.add("https://www.autoopt.ru" + element.attr("href"));
                }
            });
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
        parseByCatsLinks(links);
        log.info("Получен первичный список ссылок размером {}", links.size());
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все каталоги каждого итема каталога
     * @param urls
     * @return список адресов
     */
    public List<String> parseByCatsLinks(List<String> urls) {
        List<String> links = new ArrayList<>();
        for (String url: urls) {
//            log.info(url);
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                Element element = doc.getElementById("disabled-btn-next");
                if (element != null) {
                    links.add("https://www.autoopt.ru" + element.attr("href"));
//                    log.info(element.attr("href"));
                } else {
                    links.add(url);
                }
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("Получен первичный список ссылок размером {}", links.size());
        getAllItemsLinks(links);

        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все товары итема
     * @param urls
     * @return список адресов товаров
     */
    public List<String> getAllItemsLinks(List<String> urls) {
        List<String> links = new ArrayList<>();
        for (String url : urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
//                list.forEach(element -> {
//                    if (element != null) {
//                        links.add("https://www.autoopt.ru" + element.attr("href"));
//                        log.info(url, element.attr("href"));
//                    }
//                });
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("Получен первичный список ссылок размером {}", links.size());
        return links;
    }

    public List<String> getElement(String url) {
        List<String> listStr = new ArrayList<>();
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> list = doc.getElementsByAttributeValue("class", "catalog-brand-item");
                log.info(String.valueOf(list.size()));
                list.forEach(element -> listStr.add(element.getElementsMatchingText("Показать все запчасти").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        return listStr;
    }

}
