package com.yuaicodemother;

import com.yuaicodemother.ai.AiCodeGeneratorService;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.MultiFileCodeResult;
import com.yuaicodemother.ai.core.AiCodeGeneratorFacade;
import jakarta.annotation.Resource;
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
}
