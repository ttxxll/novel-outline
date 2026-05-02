package com.noveloutline.util;

import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.CharacterEntry;
import com.noveloutline.common.dto.ConflictEntry;
import com.noveloutline.common.dto.FactionEntry;
import com.noveloutline.common.dto.ForeshadowingEntry;
import com.noveloutline.common.dto.NovelContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class AnalysisUtil {

    private static final int CHAR_LIMIT = 30;
    private static final int FACTION_LIMIT = 10;
    private static final int FORESHADOW_LIMIT = 10;
    private static final int CONFLICT_LIMIT = 5;

    private AnalysisUtil() {}

    public static String extractJson(String raw) {
        String trimmed = raw.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }

    public static void applyChapterResult(NovelContext context, ChapterAnalysisResult result, int chapterIndex) {
        if (result == null) {
            return;
        }
        if (context.protagonist == null && result.characters != null) {
            result.characters.stream()
                    .filter(c -> "主角".equals(c.role))
                    .findFirst()
                    .ifPresent(c -> context.protagonist = c.name);
        }
        if (result.characters != null) {
            for (CharacterEntry ch : result.characters) {
                java.util.Optional<CharacterEntry> existing = context.characters.stream()
                        .filter(ec -> ec.name.equals(ch.name))
                        .findFirst();
                if (existing.isPresent()) {
                    existing.get().action = ch.action;
                    existing.get().lastSeenChapter = chapterIndex;
                } else {
                    ch.lastSeenChapter = chapterIndex;
                    context.characters.add(ch);
                }
            }
        }
        if (result.factions != null) {
            for (FactionEntry f : result.factions) {
                java.util.Optional<FactionEntry> existing = context.activeFactions.stream()
                        .filter(ef -> ef.name.equals(f.name))
                        .findFirst();
                if (existing.isPresent()) {
                    existing.get().description = f.description;
                } else {
                    context.activeFactions.add(f);
                }
            }
        }
        if (result.foreshadowing != null) {
            for (ForeshadowingEntry fe : result.foreshadowing) {
                if (fe.description == null) continue;
                context.unresolvedForeshadowing.removeIf(e -> fe.description.equals(e.description));
                context.unresolvedForeshadowing.add(fe);
            }
        }
        if (result.conflicts != null) {
            for (ConflictEntry ce : result.conflicts) {
                String key = conflictKey(ce);
                context.activeConflicts.removeIf(e -> key.equals(conflictKey(e)));
                context.activeConflicts.add(ce);
            }
        }
        context.totalChaptersAnalyzed++;

        pruneContext(context);
    }

    public static void pruneAfterVolume(NovelContext context, String volumeSummary) {
        pruneContext(context);
        context.recentSummary = volumeSummary;
    }

    /** 每章分析后裁剪所有列表到硬上限 */
    private static void pruneContext(NovelContext context) {
        context.characters = pruneCharacters(context.characters, CHAR_LIMIT);
        context.activeFactions = pruneTail(context.activeFactions, FACTION_LIMIT);
        context.unresolvedForeshadowing = pruneTail(context.unresolvedForeshadowing, FORESHADOW_LIMIT);
        context.activeConflicts = pruneTail(context.activeConflicts, CONFLICT_LIMIT);
    }

    /** 按综合权重排序后取前 N 个（权重 = 角色等级 × 30 + 最近出现章节号） */
    private static List<CharacterEntry> pruneCharacters(List<CharacterEntry> characters, int maxSize) {
        if (characters.size() <= maxSize) {
            return characters;
        }
        return characters.stream()
                .sorted((a, b) -> Integer.compare(characterWeight(b), characterWeight(a)))
                .limit(maxSize)
                .collect(Collectors.toList());
    }

    /** 保留最近出现的 N 条（列表尾部） */
    private static <T> List<T> pruneTail(List<T> list, int maxSize) {
        if (list.size() <= maxSize) {
            return list;
        }
        return new ArrayList<>(list.subList(Math.max(0, list.size() - maxSize), list.size()));
    }

    private static int roleRank(String role) {
        if (role == null) return 0;
        switch (role) {
            case "主角": return 4;
            case "反派": return 3;
            case "配角": return 2;
            case "次要": return 1;
            default: return 0;
        }
    }

    private static int characterWeight(CharacterEntry c) {
        return roleRank(c.role) * 30 + c.lastSeenChapter;
    }

    private static String conflictKey(ConflictEntry c) {
        String parties = c.parties != null ? String.join(",", c.parties) : "";
        return c.type + "|" + parties + "|" + c.cause;
    }
}
