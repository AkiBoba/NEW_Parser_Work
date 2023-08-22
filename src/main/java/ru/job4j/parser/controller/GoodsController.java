package ru.job4j.parser.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.job4j.parser.config.TempFileConfigProps;
import ru.job4j.parser.service.OSValidator;
import ru.job4j.parser.utils.SaveGoodsInFileUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GoodsController {
    private final TempFileConfigProps fileCatalog;
    private final OSValidator osValidator;
    private final SaveGoodsInFileUtil util;

    @GetMapping(value = "/goods")
    @ResponseBody
    public ResponseEntity<?> downloadorderslfile() throws IOException {
        String result = util.goodsFile();
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/downloadorderslfile/{fileName}")
    public void downloadGoodsFile(HttpServletResponse response, @PathVariable("fileName") String fileName) throws IOException {
        File file = new File(Paths.get(getUploadFolder(), fileName).toString());
        response.addHeader("Content-disposition", "attachment; filename=" + file.getName());
        InputStream fis = new FileInputStream(file);
        IOUtils.copy(fis, response.getOutputStream());
        response.flushBuffer();
        IOUtils.closeQuietly(fis);
        log.info("Загрузка файла завершена и файл сохранен");
    }

    public String getUploadFolder() {
        return this.osValidator.isWindows() ? this.fileCatalog.getCatalog().getWin() : this.fileCatalog.getCatalog().getUni();
    }
}
