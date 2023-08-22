package ru.job4j.parser.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.job4j.parser.domain.GoodInfo;
import ru.job4j.parser.repository.GoodInfoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutooptParserUtil {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";
    private final GoodInfoRepository repository;

    /** Метод парсит страницу url и получает ссылки на все каталоги с этой страницы
     * @param url
     * @return список адресов каталогов
     */
    public Set<String> getLinks(String url) {
        Set<String> links = new HashSet<>();
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
        log.info("List of links on 1st page {}", links.size());
        parseByCatsLinks(links);
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все каталоги каждого итема каталога
     * @param urls
     * @return список адресов
     */
    public Set<String> parseByCatsLinks(Set<String> urls) {
        Set<String> links = new HashSet<>();
        for (String url: urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                Element element = doc.getElementById("disabled-btn-next");
                if (element != null) {
                    links.add("https://www.autoopt.ru" + element.attr("href"));
                } else {
                    links.add(url);
                }
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("List of links on cat page {}", links.size());
        getElement(links);
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все товары каждого итема (под-каталога)
     * @param urls
     * @return список адресов товаров
     */
    public Set<String> getElement(Set<String> urls) {
        Set<String> links = new HashSet<>();
        for (String url: urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> list = doc.getElementsByAttributeValue("class", "catalog-brand-item");
                list.forEach(element -> links.add("https://www.autoopt.ru" + element.getElementsMatchingText("Показать все запчасти").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("List of links of elements page {}", links.size());
        getAllPaginationLinks(links);
        return links;
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
        log.info("List of pagination links {}", pageLinks.size());
        getProducktsLinks(pageLinks);
        return pageLinks;
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
                list.forEach(element -> goodsLinks.add("https://www.autoopt.ru" + element.getElementsByAttributeValue("class", "n-catalog-item__name-link actions name-popover").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("goodsLinks {}", goodsLinks.size());
        getProductsInfo(goodsLinks);
        return goodsLinks;
    }

    /** Метод парсит страницу url и получает ссылки на каждый pagination__link который выходит за границы старицы и не видим
     * @param url
     * @return список адресов страниц
     */
    public Set<String> getAllNextPaginationLinks(String url) {
        List<Element> list = new ArrayList<>();
                Element doc = null;
        do {
            try {
                doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                list = doc.getElementsByAttributeValue("class", "pagination__item");
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

    Long countgoodsExist = 0L;

    public void getProductsInfo(Set<String> urls) {
        String code;
        String description;
        String article;
        String name;
        for (String url: urls) {
            try {
//                log.info("url of good {}", url);
                Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByAttributeValueContaining("itemprop", "description");
                description = elements.isEmpty() ? "" : elements.get(0).text();
                elements = doc.getElementsByAttributeValueContaining("class", "card-product-article");
                article = elements.isEmpty() ? "" : elements.get(0).text().replace("Артикул: ", "");
                elements = doc.getElementsByAttributeValueContaining("class", "card-product-title");
                name = elements.isEmpty() ? "" : elements.get(0).text();
                elements = doc.getElementsByAttributeValueContaining("itemprop", "sku");
                code = elements.isEmpty() ? "" : elements.get(0).text();

                elements = doc.getElementsByTag("tr");
                List<Elements> lengths = new ArrayList<>();
                elements.forEach(element -> lengths.add(element.getElementsMatchingText("Длина, м")));
                List<Elements> lenghtList = lengths.stream().filter(e -> !e.isEmpty()).toList();
                String lenght = !lenghtList.isEmpty() ?  lenghtList.get(0).stream().filter(e -> !e.text().equals("Длина, м")).toList().get(0).text().replace("Длина, м", "") : "";
                GoodInfo good = new GoodInfo(
                        code,
                        article,
                        name,
                        description,
                        doc.getElementsByAttributeValue("itemprop", "width").text(),
                        doc.getElementsByAttributeValue("itemprop", "height").text(),
                        lenght,
                        doc.getElementsByAttributeValue("itemprop", "weight").text()
                );
                try {
                    repository.save(good);
                } catch (Exception e) {
                    countgoodsExist = countgoodsExist + 1;
//                    log.info("Такая деталь уже записана в БД, article {}", good.getArticle());
//                    repository.update(good.getDescription(), good.getWidth(), good.getHeight(), good.getLength(), good.getWeight(), good.getArticle(), good.getName());
                }
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("Парсинг введенного адреса завершен! Совпадение ссылок {} шт", countgoodsExist);
    }

    public void getSingleProductsInfo(String url) {
        String code;
        String description;
        String article;
        String name;
            try {
//                log.info("url of good {}", url);
                Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByAttributeValueContaining("itemprop", "description");
                description = elements.isEmpty() ? "" : elements.get(0).text();
                elements = doc.getElementsByAttributeValueContaining("class", "card-product-article");
                article = elements.isEmpty() ? "" : elements.get(0).text().replace("Артикул: ", "");
                elements = doc.getElementsByAttributeValueContaining("class", "card-product-title");
                name = elements.isEmpty() ? "" : elements.get(0).text();
                elements = doc.getElementsByAttributeValueContaining("itemprop", "sku");
                code = elements.isEmpty() ? "" : elements.get(0).text();

                elements = doc.getElementsByTag("tr");
                List<Elements> lengths = new ArrayList<>();
                elements.forEach(element -> lengths.add(element.getElementsMatchingText("Длина, м")));
                List<Elements> lenghtList = lengths.stream().filter(e -> !e.isEmpty()).toList();
                String lenght = !lenghtList.isEmpty() ?  lenghtList.get(0).stream().filter(e -> !e.text().equals("Длина, м")).toList().get(0).text().replace("Длина, м", "") : "";
                GoodInfo good = new GoodInfo(
                        code,
                        article,
                        name,
                        description,
                        doc.getElementsByAttributeValue("itemprop", "width").text(),
                        doc.getElementsByAttributeValue("itemprop", "height").text(),
                        lenght,
                        doc.getElementsByAttributeValue("itemprop", "weight").text()
                );
                try {
                    repository.save(good);
                } catch (Exception e) {
                    countgoodsExist = countgoodsExist + 1;
//                    log.info("Такая деталь уже записана в БД, article {}", good.getArticle());
//                    repository.update(good.getDescription(), good.getWidth(), good.getHeight(), good.getLength(), good.getWeight(), good.getArticle(), good.getName());
                }
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        log.info("Парсинг введенного адреса завершен! {}", countgoodsExist);
    }

    public String getProducktsDescription(String url) {
        String description = null;
            try {
                Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByAttributeValueContaining("itemprop", "description");
                description = elements.get(0).text();
                log.info("good.toString()");
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            return description;
    }

    public String getProducktsArticle(String url) {
        String article = null;
            try {
                Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByAttributeValueContaining("class", "card-product-article");
                article = elements.get(0).text().replace("Артикул: ", "");
                log.info("good.toString()");
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            return article;
    }

    public String getProducktsName(String url) {
        String name = null;
            try {
                Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByAttributeValueContaining("class", "card-product-title");
                name = elements.get(0).text();
                log.info("good.toString()");
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            return name;
    }

}
