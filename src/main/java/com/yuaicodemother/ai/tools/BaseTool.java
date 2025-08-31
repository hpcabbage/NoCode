package com.yuaicodemother.ai.tools;

import cn.hutool.json.JSONObject;

/**
 * 获取工具的英文名称(对应方法名)
 * 策略模式 + 工厂模式
 * return 工具英文名称
 */
public abstract class BaseTool {
    /**
     * 获取工具的英文名称(对应方法名)
     * @return 工具英文名称
     */
    public abstract String getToolName();
    /**
     * 获取工具的中文显示名称
     * @return 工具中文名称
     */
    public abstract String getDisplayName();

    /**
     * 生成工具请求时的返回值(显示给用户)
     * @return 工具请求显示内容
     */
    public String generatorToolRequestResponse() {
        return String.format("\n\n[选择工具] %s\n\n",getDisplayName());
    }

    public abstract String generatorToolExecuteResult(JSONObject arguments);
}
