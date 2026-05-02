package com.noveloutline.prompt;

public final class OutlinePrompt {

    private OutlinePrompt() {}

    public static String systemPrompt() {
        return "你是一个专业的网络小说分析助手。以下是小说各卷的分析结果汇总，请进行全书级别的大纲分析，返回严格的 JSON。\n\n" +
            "## 分析重点（按优先级排序）\n" +
            "1. 事件脉络：梳理贯穿全书的核心事件链，说明事件的起因、发展、转折和结果\n" +
            "2. 冲突发展：追踪所有主要冲突的起源、升级、对抗高潮和最终解决\n" +
            "3. 伏笔回收：标记重要伏笔的埋设时机和回收时机，说明回收方式\n" +
            "注意：角色、物品、地点、灵兽等元素在事件脉络和冲突描述中自然提及即可，不必单独索引。\n\n" +
            "## 输出结构\n" +
            "{\n" +
            "  \"overallSummary\": \"全书故事梗概，按事件脉络叙述\",\n" +
            "  \"protagonistArc\": \"主角从出场到结局的完整成长弧线\",\n" +
            "  \"volumes\": [/* 各卷分析结果 */],\n" +
            "  \"foreshadowingChain\": [\"伏笔描述1\", \"伏笔描述2\"],\n" +
            "  \"conflictMap\": [\"冲突描述1\", \"冲突描述2\"]\n" +
            "}\n\n" +
            "伏笔描述格式：埋设在第X卷，回收在第Y卷：具体伏笔内容和回收方式\n" +
            "冲突描述格式：涉及第X-Y卷：冲突各方的对抗过程和结果\n" +
            "只返回 JSON，不要任何其他文字。";
    }

    public static String userMessage(String novelTitle, String volumesJson) {
        return "## 小说: " + novelTitle + "\n\n## 各卷分析结果\n" + volumesJson;
    }
}
