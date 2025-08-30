package com.yuaicodemother;

import com.yuaicodemother.ai.AiCodeGeneratorService;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.MultiFileCodeResult;
import com.yuaicodemother.ai.core.AiCodeGeneratorFacade;
import com.yuaicodemother.utils.WebScreenshotUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

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
    void generaterFluxCode() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream("生成一个向大家介绍acm的", CodeGenTypeEnum.MULTI_FILE,1L);
        List<String> allChunks = codeStream.collectList().block(Duration.ofSeconds(300));
        for(String chunk : allChunks) {
            System.out.println(chunk);
        }
    }


    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(
                "简单的任务记录网站，总代码量不超过 200 行",
                CodeGenTypeEnum.VUE, 1L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }


        @Test
        void saveWebPageScreenshot() {
            String testUrl = "https://www.codefather.cn";
            String webPageScreenshot = WebScreenshotUtils.saveWebPageScreenshot(testUrl);
            Assertions.assertNotNull(webPageScreenshot);
        }

}
