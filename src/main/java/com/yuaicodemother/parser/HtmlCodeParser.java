package com.yuaicodemother.parser;

import com.yuaicodemother.ai.model.HtmlCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlCodeParser implements CodeParser<HtmlCodeResult>{

    /**
     * 解析HTML代码
     * @param codeContent
     * @return
     */
    private final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    @Override
    public HtmlCodeResult parserCode(String codeContent) {
        HtmlCodeResult result = new HtmlCodeResult();
        String htmlCode = extracHtmlCode(codeContent);
        if(htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 如果没有匹配到HTML代码，则将整个消息作为HTML代码
            result.setHtmlCode(codeContent.trim());
        }
        return result;
    }
    /**
     * 根据正则表达式提取代码
     * @param message
     * @return
     */
    private String extracHtmlCode(String message) {
        Matcher matcher = HTML_CODE_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
