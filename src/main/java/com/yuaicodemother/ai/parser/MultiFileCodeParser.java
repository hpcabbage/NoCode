package com.yuaicodemother.ai.parser;

import com.yuaicodemother.ai.model.MultiFileCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiFileCodeParser implements CodeParser<MultiFileCodeResult>{
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    /**
     * 解析多个文件代码
     * @param codeContent
     * @return
     */
    @Override
    public MultiFileCodeResult parserCode(String codeContent) {
        MultiFileCodeResult result = new MultiFileCodeResult();
        String htmlCode = extractCodeByPattern(codeContent,HTML_CODE_PATTERN);
        String cssCode = extractCodeByPattern(codeContent,CSS_CODE_PATTERN);
        String jsCode = extractCodeByPattern(codeContent,JS_CODE_PATTERN);
        if(htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        }
        if(cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssCode(cssCode.trim());
        }
        if(jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsCode(jsCode.trim());
        }
        return result;
    }


    /**
     * 根绝不同的正则模式提起代码(策略模式）
     * @param message
     * @param pattern
     * @return
     */
    private String extractCodeByPattern(String message,Pattern pattern) {
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
