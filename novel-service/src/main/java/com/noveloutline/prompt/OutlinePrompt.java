package com.noveloutline.prompt;

public final class OutlinePrompt {

    private OutlinePrompt() {}

    public static String systemPrompt() {
        return "你是一个专业的网络小说分析助手。以下是小说各卷的分析结果汇总，请进行全书级别的大纲分析，返回严格的 JSON。\n\n" +
            "## 输出结构\n" +
            "{\n" +
            "  \"overallSummary\": \"全书总览\",\n" +
            "  \"protagonistArc\": \"主角从头到尾的完整成长弧线\",\n" +
            "  \"volumes\": [/* 各卷分析结果 */],\n" +
            "  \"characterIndex\": { \"角色名\": [角色在各阶段的信息] },\n" +
            "  \"factionIndex\": { \"势力名\": [势力在各阶段的信息] },\n" +
            "  \"itemIndex\": { \"物品名\": [物品出现记录] },\n" +
            "  \"locationTimeline\": [{name, description, appearedInVolume}],\n" +
            "  \"foreshadowingChain\": [{description, plantedVolume, resolvedVolume, resolution}],\n" +
            "  \"conflictMap\": { \"冲突描述\": [冲突在各阶段的变化] }\n" +
            "}\n\n" +
            "只返回 JSON，不要任何其他文字。";
    }

    public static String userMessage(String novelTitle, String volumesJson) {
        return "## 小说: " + novelTitle + "\n\n## 各卷分析结果\n" + volumesJson;
    }
}
