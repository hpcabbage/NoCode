package com.yuaicodemother.ai;

import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

public interface AiCodeGenTypeRoutingService {
    /**
     * 根据用户需求智能选择代码生成类型
     * @param userPrompt 用户输入的需求描述
     * @return 推荐生成的代码类型
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userPrompt);
}
