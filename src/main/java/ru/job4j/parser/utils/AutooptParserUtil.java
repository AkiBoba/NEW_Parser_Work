package ru.job4j.parser.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.job4j.parser.domain.GoodInfo;

import java.io.IOException;
import java.util.*;
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
//        log.info("Получен первичный список ссылок размером {}", links.size());
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все каталоги каждого итема каталога
     * @param urls
     * @return список адресов
     */
    public Set<String> parseByCatsLinks(List<String> urls) {
        Set<String> links = new HashSet<>();
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
//        log.info("Получен первичный список ссылок размером {}", links.size());
        getElement(links);
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все товары итема
     * @param urls
     * @return список адресов товаров
     */
    public Set<String> getElement(Set<String> urls) {
        Set<String> links = new HashSet<>();
        for (String url: urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> list = doc.getElementsByAttributeValue("class", "catalog-brand-item");
//                log.info(String.valueOf(list.size()));
                list.forEach(element -> links.add("https://www.autoopt.ru" + element.getElementsMatchingText("Показать все запчасти").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
//        getProduckt(links);
        getAllPaginationLinks(links);
        return links;
    }

    Set<String> goodsLinks = new HashSet<>();

    /** Метод парсит страницы urls и получает ссылки на каждый товар на странице
     * @param urls
     * @return список адресов товаров
     */
    public Set<String> getProducktsLinks(Set<String> urls) {
        for (String url: urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> list = doc.getElementsByAttributeValue("class", "n-catalog-item relative grid-item n-catalog-item__product");
//                log.info(String.valueOf(list.size()));
                list.forEach(element -> goodsLinks.add("https://www.autoopt.ru" + element.getElementsByAttributeValue("class", "n-catalog-item__name-link actions name-popover").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
//        getProducktsInfo(goodsLinks);
        return goodsLinks;
    }

    Set<String> pageLinks = new HashSet<>();

    /** Метод парсит страницы urls и получает ссылки на каждый pagination__link
     * @param urls
     * @return список адресов страниц
     */
    public Set<String> getAllPaginationLinks(Set<String> urls) {
        for (String url: urls) {
                try {
                    Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                    List<Element> list = doc.getElementsByAttributeValue("class", "pagination__item");
//                    log.info(String.valueOf(list.size()));
                    pageLinks.add(url);
                    list.forEach(element -> pageLinks.add("https://www.autoopt.ru" + element.getElementsByAttributeValueStarting("class", "pagination__link").attr("href")));
                    if (!doc.getElementsByAttributeValueContaining("class", "pagination__item next").isEmpty()) {
                        url = "https://www.autoopt.ru" + list.get(list.size() - 1).getElementsByAttributeValueStarting("class", "pagination__link").attr("href");
                        getAllNextPaginationLinks(url);
                    }
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
        }
        getProducktsLinks(pageLinks);
        return pageLinks;
    }

    public Set<String> getAllNextPaginationLinks(String url) {
        List<Element> list = new ArrayList<>();
                Element doc = null;
        do {
            try {
                doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                list = doc.getElementsByAttributeValue("class", "pagination__item");
//                log.info(String.valueOf(list.size()));
                list.forEach(element -> pageLinks.add("https://www.autoopt.ru" + element.getElementsByAttributeValueStarting("class", "pagination__link").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            if (!doc.getElementsByAttributeValueContaining("class", "pagination__item next").isEmpty()) {
                url = "https://www.autoopt.ru" + list.get(list.size() - 1).getElementsByAttributeValueStarting("class", "pagination__link").attr("href");
            } else {
                url = null;
            }
        } while (url != null);
        return pageLinks;
    }

//    private void getProducktsInfo(Set<String> goodsLinks) {
    public void getProducktsInfo(String url) {
            try {
                Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByTag("tr");
                List<Elements> lengths = new ArrayList<>();
                elements.forEach(element -> lengths.add(element.getElementsMatchingText("Длина, м")));
                lengths.forEach(e -> log.info(String.valueOf(e.size())));
                List<Elements> lenght = lengths.stream().filter(e -> !e.isEmpty()).toList();
                String length = lenght.get(0).stream().filter(e -> !e.text().equals("Длина, м")).toList().get(0).text().replace("Длина, м", "");
                GoodInfo good = new GoodInfo(
                        doc.getElementsByAttributeValue("itemprop", "width").text(),
                        doc.getElementsByAttributeValue("itemprop", "height").text(),
                        length,
                        doc.getElementsByAttributeValue("itemprop", "weight").text()
                );
                log.info("good.toString()");
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        getProducktsLinks(pageLinks);

    }

}
