package ru.job4j.parser.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

@Slf4j
@Service
public class ParseExcelFile {
    public List<String> getUrles() {
        try (Workbook workbook = WorkbookFactory.create(new File("C:\\Users\\yo114\\Downloads"))) {
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        workbook.setMissingCellPolicy(CREATE_NULL_AS_BLANK);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> result = new ArrayList<>(20000);
        sheet.forEach(row -> {
            if (row != null && row.getRowNum() >= 1) {
                String string = readExcelStrings(row);
                if (string != null) {
                    result.add(string);
                }
            }
        });
        return result;
    } catch (OldExcelFormatException oldExcelFormatException) {
        log.info("Old excel format. {}. Init old excel parser.", oldExcelFormatException.getMessage());
    } catch (IOException e) {
        log.error("Ошибка обработки excel файла");
    }
        return Collections.emptyList();

    }

    private String readExcelStrings(Row row) {
        String result = null;
        try {
            result = Optional.of(row.getCell(3)).toString();

        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Excel error {}.Row number: {}", e.getMessage(), row.getRowNum());
            return null;
        }
        return result;
    }
}
