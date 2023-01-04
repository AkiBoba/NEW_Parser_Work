package ru.job4j.parser.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class ParserController{

    @PostMapping("/parser")
    public List<String> getList(Model model, @RequestParam(name = "url", required = false) String url) {
        List<String> str = new ArrayList<>();
        log.info(url);
        return str;
    }

}
