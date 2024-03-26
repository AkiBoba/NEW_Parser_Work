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
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutooptParserUtil {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";
    private final GoodInfoRepository repository;
    private final SaveGoodsInFileUtil util;

    /**
     * Сет всех ссылок на страницы с товарами
     */
    Set<String> pageLinks = new HashSet<>();

    /** Метод парсит страницу url и получает ссылки на все каталоги с этой страницы
     * @param url
     * @return список адресов каталогов
     */
    public List<String> getLinks(String url) {
        List<String> links = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
            List<Element> list = doc.getElementsByAttributeValue("class", "filter-item ng-star-inserted");
            list.forEach(element -> {
                if (element != null) {
                    links.add("https://armtek.ru" + element.attr("href"));
                }
            });
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
        log.info("List of links on 1st page {}", links.size());
        List<String> innerLinks = parseByCatsLinks(links);
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все каталоги каждого итема каталога
     * @param urls
     * @return список адресов
     */
    public List<String> parseByCatsLinks(List<String> urls) {
        List<String> links = new ArrayList<>();
        for (String url : urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByAttributeValue("class", "brand-item ng-star-inserted");
                elements.forEach(element -> {
                    if (element != null) {
                        links.add("https://armtek.ru" + element.attr("href"));
                    } else {
                        links.add(url);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage());
            }
//        getCountGoodsOfItems(links);
        }
        log.info("List of links on cat page {}", links.size());
//        getElement(links);
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на все товары каждого итема (под-каталога)
     * @param urls
     * @return список адресов товаров
     */
    public List<String> getElement(List<String> urls) {
        List<String> links = new ArrayList<>();
        for (String url: urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> list = doc.getElementsByAttributeValue("class", "catalog-brand-item");
                list.forEach(element -> links.add("https://armtek.ru" + element.getElementsMatchingText("Показать все запчасти").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("List of links of elements page {}", links.size());
        getAllPaginationLinks(links);
        return links;
    }

    /** Метод парсит страницы urls и получает ссылки на каждый pagination__link
     * @param urls
     * @return список адресов страниц
     */
//    public Set<String> getAllPaginationLinks(List<String> urls) {
//        for (String url: urls) {
//                try {
//                    Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
//                    List<Element> list = doc.getElementsByAttributeValue("class", "page-item-color-primary-size-md ng-star-inserted");
//                    pageLinks.add(url);
//                    list.forEach(element -> pageLinks.add("https://armtek.ru" + element.getElementsByAttributeValueStarting("class", "page-item font__subtitle1 ng-star-inserted").attr("href")));
//                    List<Element> list2 = doc.getElementsByAttributeValueContaining("class", "sproit-ui-icon-button__wrapper sproit-ui-icon-button__link");
//                    if (!list2.isEmpty()) {
//                        List<Element> elements = list2.get(list2.size() - 1).getElementsByAttributeStarting("href");
//                        url = "https://armtek.ru" + list2.get(list2.size() - 1).getElementsByAttributeStarting("href"); //', "pagination__link").attr("href");
//                        getAllNextPaginationLinks(url);
//                    }
//                } catch (IOException e) {
//                    log.error(String.valueOf(e));
//                }
//        }
//        log.info("List of pagination links {}", pageLinks.size());
//        getProducktsLinks(pageLinks);
//        return pageLinks;
//    }

    /** Метод парсит страницы urls и получает ссылки на каждый pagination__link
     * @param urls
     * @return список адресов страниц
     */
    public Set<String> getAllPaginationLinks(List<String> urls) {
        List<String> elements = new ArrayList<>();
        for (String url: urls) {
                try {
                    Element doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();

                    List<Element> list = doc.getElementsByAttributeValue("class", "page-item-color-primary-size-md ng-star-inserted");

                    list.forEach(element -> elements.add("https://armtek.ru" + element.getElementsByAttributeValueStarting("class", "page-item font__subtitle1 ng-star-inserted").attr("href")));

                    pageLinks.add(url);

                    list.forEach(element -> elements.add(element.getElementsByAttributeValueContaining("class", "sproit-ui-icon-button__wrapper sproit-ui-icon-button__link").attr("href")));

//                    List<Element> s = doc.getElementsByAttributeValue("icon", "sproit-icon__chevron-right");
                    List<Element> s = doc.getElementsByAttributeValue("class", "right disabled hide");
                    log.info("s = {}", s.size());

                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
        }
        log.info("List of pagination links {}", pageLinks.size());
        getProducktsLinks(pageLinks);
        return pageLinks;
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
                list.forEach(element -> pageLinks.add("https://armtek.ru" + element.getElementsByAttributeValueStarting("class", "pagination__link").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            if (!doc.getElementsByAttributeValueContaining("class", "pagination__item next").isEmpty()) {
                url = "https://armtek.ru" + list.get(list.size() - 1).getElementsByAttributeValueStarting("class", "pagination__link").attr("href");
            } else {
                url = null;
            }
        } while (url != null);
        return pageLinks;
    }

    List<String> goodsLinks = new ArrayList<>();

    /** Метод парсит страницы urls и получает ссылки на каждый товар на странице
     * @param urls
     * @return список адресов товаров
     */
    public List<String> getProducktsLinks(Set<String> urls) {
        for (String url: urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> list = doc.getElementsByAttributeValue("class", "n-catalog-item relative grid-item n-catalog-item__product");
                list.forEach(element -> goodsLinks.add("https://armtek.ru" + element.getElementsByAttributeValue("class", "n-catalog-item__name-link actions name-popover").attr("href")));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info("goodsLinks {}", goodsLinks.size());
        getProductsInfo(goodsLinks);
        return goodsLinks;
    }

    Long countgoodsExist = 0L;

    public void getProductsInfo(List<String> urls) {
        String code;
        String description;
        String article;
        String name;
        List<GoodInfo> goods = new ArrayList<>();
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
                goods.add(good);
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        /**
        log.info("запись товаров в excel {}", goods.size());
        goods.sort(Comparator.comparing(GoodInfo::getCode));
        util.goodsFile(goods);
         */
        try {
                log.info("запись товаров в БД {}", goods.size());
            repository.saveAll(goods);

        } catch (Exception e) {
            countgoodsExist = countgoodsExist + 1;
            /**
             log.info("Такая деталь уже записана в БД, article {}", good.getArticle());
             repository.update(good.getDescription(), good.getWidth(), good.getHeight(), good.getLength(), good.getWeight(), good.getArticle(), good.getName());
             */
        }

        log.info("Парсинг введенного адреса завершен! ошибок сохранения {} шт", countgoodsExist);
    }

    /** Метод парсит страницы urls и возвращает количество всех товаров
     */
    public void getCountGoodsOfItems(List<String> urls) {
        int goodsSum = 0;
        for (String url: urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(30000).userAgent(USER_AGENT).get();
                List<Element> list = doc.getElementsByAttributeValue("class", "catalog-brand-item");
                for (Element element: list) {
                    goodsSum += Integer.parseInt(element.getElementsByAttributeValue("class", "black").text().replace("(", "").replace(")", ""));
                }
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
        }
        log.info(String.valueOf(goodsSum));
        log.info("Count of links of goods {}", goodsSum);
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
