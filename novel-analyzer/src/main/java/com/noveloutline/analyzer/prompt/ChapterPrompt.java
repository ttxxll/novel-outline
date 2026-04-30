package com.noveloutline.analyzer.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.dto.NovelContext;

public final class ChapterPrompt {

    private ChapterPrompt() {}

    public static String systemPrompt() {
        return "你是一个专业的网络小说分析助手。请根据提供的章节内容和累积的小说上下文，分析本章内容并返回严格的 JSON。\n\n" +
            "## 分析要求\n" +
            "1. summary: 详细的剧情概述，覆盖本章发生的完整事件链（起因、经过、结果、关键对话、转折点）。不要过于简短。\n" +
            "2. characters: 本章出场的人物，包括：\n" +
            "   - name: 人名\n" +
            "   - role: 主角/配角/反派/次要\n" +
            "   - relationshipToProtagonist: 与主角的关系（本人/同门师兄/师父/敌人/路人等）\n" +
            "   - action: 本章中的行为\n" +
            "3. factions: 本章出现或涉及的势力：\n" +
            "   - name: 势力名\n" +
            "   - type: 宗门/家族/王朝/帮会/散修\n" +
            "   - stanceTowardProtagonist: 盟友/中立/敌对/未知\n" +
            "   - members: 已知成员名单\n" +
            "   - description: 势力描述\n" +
            "4. items: 本章出现的物品，按类别分组：\n" +
            "   - weapons: [{name, grade, significance}]\n" +
            "   - techniques: [{name, grade, significance}]\n" +
            "   - elixirs: [{name, grade, significance}]\n" +
            "   - spiritBeasts: [{name, level, significance}]\n" +
            "5. locations: 本章出现的地点 [{name, type: 城市/城镇/村落/山/其他, events}]\n" +
            "6. conflicts: 本章发生的冲突 [{type: 战斗/口角/权谋/竞争, parties, cause, result}]\n" +
            "7. foreshadowing: 本章埋伏的伏笔 [{description, hint, likelihood: 高/中/低}]\n\n" +
            "## 重要\n" +
            "- 只返回 JSON，不要任何其他文字\n" +
            "- 未出现的内容用空数组 [] 或 null";
    }

    public static String userMessage(String chapterTitle, String chapterContent, NovelContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 当前章节\n");
        sb.append("标题: ").append(chapterTitle).append("\n");
        sb.append("内容:\n").append(chapterContent).append("\n\n");

        if (context != null && context.totalChaptersAnalyzed > 0) {
            sb.append("## 前文累积上下文\n");
            try {
                String ctxJson = new ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(context);
                sb.append(ctxJson);
            } catch (Exception e) {
                sb.append("(上下文序列化失败)");
            }
        } else {
            sb.append("## 前文累积上下文\n这是第一章，无前文上下文。请从本章开始建立小说世界观认知。\n");
        }

        return sb.toString();
    }
}
