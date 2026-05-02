package com.noveloutline.prompt;

public final class VolumePrompt {

    private VolumePrompt() {}

    public static String systemPrompt() {
        return "你是一个专业的网络小说分析助手。以下是本卷所有章节的分析结果汇总，请进行卷级别的聚合分析，返回严格的 JSON。\n\n" +
            "## 输出结构\n" +
            "{\n" +
            "  \"volumeSummary\": \"本卷总体剧情概述\",\n" +
            "  \"keyEvents\": [\"关键事件1\", \"关键事件2\", ...],\n" +
            "  \"charactersIntroduced\": [{name, role, relationshipToProtagonist, action}],\n" +
            "  \"factionsIntroduced\": [{name, type, stanceTowardProtagonist, members, description}],\n" +
            "  \"itemsAcquired\": { weapons: [{name, grade, significance}], techniques: [{name, grade, significance}], elixirs: [{name, grade, significance}], spiritBeasts: [{name, grade, significance}] },\n" +
            "  \"locationsVisited\": [{name, description}],\n" +
            "  \"majorConflicts\": [{type, parties, cause, result}],\n" +
            "  \"foreshadowingPlanted\": [{description, hint}],\n" +
            "  \"foreshadowingResolved\": [{description, hint, resolution}]\n" +
            "}\n\n" +
            "只返回 JSON，不要任何其他文字。";
    }

    public static String userMessage(String volumeTitle, String chaptersJson) {
        return "## 卷: " + volumeTitle + "\n\n## 各章节分析结果\n" + chaptersJson;
    }
}
