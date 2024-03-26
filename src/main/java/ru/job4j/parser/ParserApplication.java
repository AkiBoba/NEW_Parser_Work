package ru.job4j.parser;

import ru.job4j.parser.utils.AutooptParserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class ParserApplication {

    private static AutooptParserUtil aoparserUtil;

    public ParserApplication(AutooptParserUtil aoparserUtil) {
        this.aoparserUtil = aoparserUtil;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
