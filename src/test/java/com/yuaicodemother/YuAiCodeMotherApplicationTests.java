package com.yuaicodemother;

import com.yuaicodemother.ai.AiCodeGeneratorService;
import com.yuaicodemother.ai.model.MultiFileCodeResult;
import com.yuaicodemother.core.CodeFileSaver;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class YuAiCodeMotherApplicationTests {


    @Test
    void contextLoads() {
    }
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;
    @Test
    void AicodeGeneratorServiceTest() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("生成一个向大家介绍acm的");
        Assertions.assertNotNull(result);
    }

    @Test
    void FileWriteTest() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("生成一个向大家介绍acm的");
        File file = CodeFileSaver.saveMulFileCodeResult(result);
        Assertions.assertNotNull(file);
    }

}
