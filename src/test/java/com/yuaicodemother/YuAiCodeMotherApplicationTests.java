package com.yuaicodemother;

import com.yuaicodemother.ai.AiCodeGeneratorService;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.HtmlCodeResult;
import com.yuaicodemother.ai.model.MultiFileCodeResult;
import com.yuaicodemother.core.AiCodeGeneratorFacade;
import com.yuaicodemother.core.CodeFileSaver;
import com.yuaicodemother.core.CodeParser;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class YuAiCodeMotherApplicationTests {


    @Test
    void contextLoads() {
    }
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Test
    void AicodeGeneratorServiceTest() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("生成一个向大家介绍acm的");
        assertNotNull(result);
    }

    @Test
    void FileWriteTest() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("生成一个向大家介绍acm的");
        File file = CodeFileSaver.saveMulFileCodeResult(result);
        assertNotNull(file);
    }
    @Test
    void parseHtmlCode() {
        String codeContent = """
                随便写一段描述：
                ```html
                ‏         ؜  <!DOCTY‎PE html>
                <html>
                <head>
                    <title>测试页面</title>
                </head>
                <body>
                    <h1>Hello World!</h1>
                </body>
                </html>
                ```
                随便写一段描述
                """;
        HtmlCodeResult result = CodeParser.parseHtmlCode(codeContent);
        assertNotNull(result);
        assertNotNull(result.getHtmlCode());
    }
    @Test
    void parseMultiFileCode() {
        String codeContent = """
                创建一个完整的网页：
                ```html
     ‏         ؜  <!DOCTY‎PE html>          
                <html>
                <head>
                    <title>多文件示例</title>
                    <link rel="stylesheet" href="style.css">
                </head>
                <body>
                    <h1>欢迎使用</h1>
                    <script src="script.js"></script>
                </body>
                </html>
                ```
                ```css
                h1 {
                    color: blue;
                    text-align: center;
                }
                ```
                ```js
                console.log('页面加载完成');
                ```
                文件创建完成！
                """;
        MultiFileCodeResult result = CodeParser.parseMultiFileCode(codeContent);
        assertNotNull(result);
        assertNotNull(result.getHtmlCode());
        assertNotNull(result.getCssCode());
        assertNotNull(result.getJsCode());
    }

    @Test
    void generaterFluxCode() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream("生成一个向大家介绍acm的", CodeGenTypeEnum.MULTI_FILE);
        List<String> allChunks = codeStream.collectList().block(Duration.ofSeconds(300));
        for(String chunk : allChunks) {
            System.out.println(chunk);
        }
    }
}
