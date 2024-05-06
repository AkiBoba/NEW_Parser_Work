package ru.job4j.parser.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ru.job4j.parser.domain.*;
import ru.job4j.parser.repository.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArmtekParserUtil {

    private final ArmLinksRepo repo;
    private final AlfavitCatLinksErrorRepo alfavitCatLinksErrorRepo;
    private final CatLinksErrorsRepo catLinksErrorsRepo;
    private final GoodsInfoErrorRepo goodsInfoErrorRepo;
    private final PaginationLinksErrorRepo paginationLinksErrorRepo;
    private final GoodsLinksErrorRepo goodsLinksErrorRepo;
    private final ArmtekGoodInfoRepository armtekGoodInfoRepository;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";

    /**
     * Сет всех ссылок на страницы с товарами
     */
    Set<String> pageLinks = new HashSet<>();

    /**
     * Сет всех ссылок на страницы, где вышло сообщение об ошибке парсинга
     */
    Set<String> errorsPagesLinks = new HashSet<>();

    /** Метод парсит страницу Бренды и получает ссылки на все подгруппы алфавитного указателя
     * @param url адрес страницы брендов
     */
    public void getLinks(String url) {
        List<String> links = new ArrayList<>(); // Все ссылки на все каталоги на странице бренды
        try {
            Document doc = Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();
            List<Element> list = doc.getElementsByAttributeValue("class", "filter-item ng-star-inserted");
            list.forEach(element -> {
                if (element != null) {
                    links.add("https://armtek.ru" + element.attr("href"));
                }
            });
        } catch (IOException e) {
            log.error("Ошибка парсинга урла {} \n {}", url, String.valueOf(e));
        }
        log.info("List of links on 1st page {}", links.size());
        parseByCatsLinks(links);
    }

    /** Метод парсит страницы urls и получает ссылки на все страницы каждой буквы
     * @param urls адреса алфавитных подкаталогов
     */
    public void parseByCatsLinks(List<String> urls) {
        List<String> links = new ArrayList<>();
        for (String url : urls) {
            try {
                Document doc = Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();
                List<Element> elements = doc.getElementsByAttributeValue("class", "brand-item ng-star-inserted");
                elements.forEach(element -> {
                    if (element != null) {
                        links.add("https://armtek.ru" + element.attr("href"));
                    } else {
                        links.add(url);
                    }
                });
            } catch (Exception e) {
//                log.error("Ошибка парсинга урла {} \n {}", url, e.getMessage());
                alfavitCatLinksErrorRepo.save(new AlfavitCatLinksError(url));
            }
        }
        log.info("List of links on cat page {}", links.size());
        getAllPaginationLinks(links);
    }

    /**
     * Сет всех страниц пагинации
     */
    Set<String> allPaginationUrls = new TreeSet<>();

    /** Метод парсит страницы каталогов и получает ссылки на каждый pagination__link
     * @param urls адреса всех каталогов
     * @return список адресов страниц
     */
    public void getAllPaginationLinks(List<String> urls) {
        List<String> pageNumbers = new ArrayList<>();
        Set<String> allPaginationUrlsInPage = new HashSet<>();
        for (String url: urls) {
            if (url.contains("stellox-5538")) continue; //только для черновой версии
            int sizeLinksOnPage = 0;
            int countOfTries  = 0;
            List<Element> elements = new ArrayList<>();

            Element doc = null;
                try {
                    Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();
                    doc = Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();
                } catch (IOException e) {
                    log.error("Ошибка парсинга урла {} \n {}", url, String.valueOf(e));
                    paginationLinksErrorRepo.save(new PaginationLinksError(url));
                }
                if (doc != null) {
                    while (sizeLinksOnPage == 0 && countOfTries < 15) {
                        allPaginationUrlsInPage.add(url);
                        countOfTries++;
                        log.info(String.valueOf(countOfTries));
                        elements = doc.getElementsByAttributeValue("class", "page-item-color-primary-size-md ng-star-inserted");
                        sizeLinksOnPage = elements.size();
                        log.info(String.valueOf(sizeLinksOnPage));
                    }
                    elements.forEach(element -> {
                        Element aTag = element.select("a").first();
                        if (aTag != null) {
                            String pageNumber = aTag.text();
                            pageNumbers.add(pageNumber);
                        }
                    });
                    int psize = pageNumbers.size();
                    if (psize > 1) {
                        String prefUrl = url + "?page=";
                        for (int i = 2; i <= Integer.parseInt(pageNumbers.get(psize - 1)); i++) {
                            String newUrl = prefUrl + (i);
                            allPaginationUrlsInPage.add((newUrl));
                        }
                    }
                }
        log.info("Список пагинатов урла {} состоит из {} строк", url, allPaginationUrlsInPage.size());
//        allPaginationUrls.addAll(allPaginationUrlsInPage);
        }
        log.info("Список пагинатов состоит из {} строк", allPaginationUrls.size());
//        repo.saveAll(armatekLinks);
        getAllGoodsLinks(allPaginationUrlsInPage);
    }

    Set<String> allUrlsSet = new TreeSet<>();
    List<String> allUrls = new ArrayList<>();

    /**
     * Получает все ссылки на товары на страницах из списка, переданного как параметр
     * @param urls список страниц
     */
    public void getAllGoodsLinks(Set<String> urls) {
        List<String> allGoodsUrlsOnPage = new ArrayList<>();
        for (String url : urls) {
            allGoodsUrlsOnPage = new ArrayList<>();
            int sizeLinksOnPage = 0;
            int countOfTries  = 0;
            List<Element> elements = new ArrayList<>();
            Element doc = null;
            try {
                doc = Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();
            } catch (Exception e) {
                log.error("Ошибка парсинга урла {} \n {}", url, e.getMessage());
                goodsLinksErrorRepo.save(new GoodsLinksError(url));
            }
            if (doc != null) {
                while (sizeLinksOnPage == 0 && countOfTries < 15) {
                    countOfTries++;
                    log.info(String.valueOf(countOfTries));
                    elements = doc.getElementsByAttributeValue("class", "title");
                    sizeLinksOnPage = elements.size();
                    log.info(String.valueOf(sizeLinksOnPage));
                }
                for (Element element : elements) {
                    String hrefValue = element.attr("href");
                    if (!hrefValue.equals("")) {
                        allUrlsSet.add(("https://armtek.ru" + hrefValue));
                    }
                }
//                allUrlsSet.addAll(allGoodsUrlsOnPage);
            }
        }
//        log.info("allUrls.size() = {}", allUrls.size());
        log.info("allUrlsSet.size() = {}", allUrlsSet.size());

//        log.info("allGoodsUrlsOnPage = {}", allGoodsUrlsOnPage.size());

//        repo.saveAll(list);
        getGoodsInfo(allUrlsSet);

    }

    List<String> goodsInfoErrors = new ArrayList<>();

    public void getGoodsInfo(Set<String> urls) {
        for (String url : urls) {

            Element doc = null;
            try {
                doc = Jsoup.connect(url).timeout(10000).userAgent(USER_AGENT).get();

            } catch (Exception e) {
//                log.error("Ошибка парсинга урла {} \n {}", url, e.getMessage());
                goodsInfoErrors.add(url);
                goodsInfoErrorRepo.save(new GoodsInfoError(url));
            }
            if (doc != null) {
                Map<String, String> leftRight = new TreeMap<>();
                String name = doc.getElementsByAttributeValueStarting("class", "font__headline4").text();
                String descriptions = doc.getElementsByAttributeValueStarting("class", "font__body2 product-card-info__body-block-content").text();
                List<Element> elements = doc.getElementsByAttributeValue("class", "product-key-values__column ng-star-inserted");
                elements.forEach(element -> {
                            List<Element> rightParts = element.getElementsByAttributeValueStarting("class", "font__body2 color-black_87 ng-star-inserted");
                            List<Element> leftParts = element.getElementsByAttributeValueStarting("class", "font__body2 color-black_36");

                            for (int i = 0; i < rightParts.size(); i++) {
                                String left = getTextFromElementLeft(leftParts.get(i));
                                String right = getTextFromElementRight(rightParts.get(i));
                                leftRight.put(left, right);

                            }
                        }
                );
                saveNewArmtekGood(leftRight, name, descriptions);
            }

        }
        log.info("количество ошибок {}", goodsInfoErrors.size());
        armtekGoodInfoRepository.saveAll(goodInfoList);
//        log.info("{}", allUrls.size());

    }

    private String getTextFromElementRight(Element part) {
        return part.getElementsByAttributeValueStarting("class", "font__body2 color-black_87 ng-star-inserted")
                .parents()
                .get(0)
                .text();
    }

    private String getTextFromElementLeft(Element part) {
        return part.getElementsByAttributeValueStarting("class", "font__body2 color-black_36")
                .parents()
                .get(0)
                .text();
    }

    List<ArmGoodInfo> goodInfoList = new ArrayList<>();

    private void saveNewArmtekGood(Map<String, String> map, String name, String description) {
        String article = "";
        String brand = "";
        String weight = "";
        String length = "";
        String height = "";
        String width = "";
        StringBuilder other = new StringBuilder();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            switch (entry.getKey()) {
                case "Артикул" -> article = entry.getValue();
                case "Бренд" -> brand = entry.getValue();
                case "Ширина" -> width = entry.getValue();
                case "Высота" -> height = entry.getValue();
                case "Длина" -> length = entry.getValue();
                case "Вес в инд. упак." -> weight = entry.getValue();
                default -> other.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
        }

        ArmGoodInfo goodInfo = ArmGoodInfo.builder()
                .code("")
                .name(name)
                .article(article)
                .brand(brand)
                .height(height)
                .length(length)
                .weight(weight)
                .width(width)
                .description(description)
                .other(other.toString())
                .build();

//        log.info(goodInfo.toString());
        if (!goodInfo.getArticle().isBlank()) {
            goodInfoList.add(goodInfo);
        }

    }

}
