package com.yuaicodemother.ai.parser;

/**
 * 代码解析器策略模式接口
 */
public interface CodeParser<T> {
    /**
     * 解析代码内容
     * @param codeContent 原始代码内容
     * @return 解析后的结果
     */
    T parserCode(String codeContent);
}
