package ru.job4j.parser.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.job4j.parser.config.TempFileConfigProps;
import ru.job4j.parser.domain.GoodInfo;
import ru.job4j.parser.repository.GoodInfoRepository;
import ru.job4j.parser.service.OSValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SaveGoodsInFileUtil {
    private final GoodInfoRepository repository;
    private final OSValidator osValidator;
    private final TempFileConfigProps fileCatalog;

    public String goodsFile() {

        log.info("Начинается формирование файла с заказами клиентов с сайта");
        SimpleDateFormat date = new SimpleDateFormat("HH:mm.dd.MM.yyyy");
        String path = Paths.get(getWorkFolder(), ("Характеристики_товаров" + ".xlsx")).toString();
        List<GoodInfo> goods = (List) repository.findAll();

        try (XSSFWorkbook book = new XSSFWorkbook()) {

            Sheet sheet = book.createSheet("товары");

            Row row0 = sheet.createRow(0);

            row0.createCell(0).setCellValue("код");
            row0.createCell(1).setCellValue("артикл");
            row0.createCell(2).setCellValue("название");
            row0.createCell(3).setCellValue("описание");
            row0.createCell(4).setCellValue("ширина");
            row0.createCell(5).setCellValue("высота");
            row0.createCell(6).setCellValue("длина");
            row0.createCell(7).setCellValue("вес");

            int rowNum = 1;
            for (GoodInfo good : goods) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(good.getCode());
                row.createCell(1).setCellValue(good.getArticle());
                row.createCell(2).setCellValue(good.getName());
                row.createCell(3).setCellValue(good.getDescription());
                row.createCell(4).setCellValue(good.getWidth());
                row.createCell(5).setCellValue(good.getHeight());
                row.createCell(6).setCellValue(good.getLength());
                row.createCell(7).setCellValue(good.getWeight());
            }

            try (FileOutputStream fos = new FileOutputStream(path)) {
                book.write(fos);
            }
        } catch (IOException exception) {
            log.info("Ошибка записи фала с товарами {}", exception.getMessage());
        }
        log.info("Файл сохранился в папке {}", path);

        return (new File(path)).getName();
    }

    private String getWorkFolder() {
        return this.osValidator.isWindows() ? this.fileCatalog.getCatalog().getWin() : this.fileCatalog.getCatalog().getUni();
    }
}
