package com.yuaicodemother.parser;

import com.yuaicodemother.ai.enums.CodeGenTypeEnum;

import java.io.File;

public class CodeParserExecutor {
    private static final HtmlCodeParser  htmlCodeParser = new HtmlCodeParser();
    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> htmlCodeParser.parserCode(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parserCode(codeContent);
            default -> throw new RuntimeException("不支持的生成模式");
        };
    }
}
