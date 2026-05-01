package com.noveloutline.util;

import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.CharacterEntry;
import com.noveloutline.common.dto.FactionEntry;
import com.noveloutline.common.dto.NovelContext;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class AnalysisUtil {

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
                } else {
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
            context.unresolvedForeshadowing.addAll(result.foreshadowing);
        }
        if (result.conflicts != null) {
            context.activeConflicts.addAll(result.conflicts);
        }
        context.totalChaptersAnalyzed++;
    }

    public static void pruneAfterVolume(NovelContext context, String volumeSummary) {
        if (context.unresolvedForeshadowing.size() > 10) {
            context.unresolvedForeshadowing = new ArrayList<>(
                    context.unresolvedForeshadowing.subList(
                            Math.max(0, context.unresolvedForeshadowing.size() - 10),
                            context.unresolvedForeshadowing.size()));
        }
        if (context.activeConflicts.size() > 5) {
            context.activeConflicts = new ArrayList<>(
                    context.activeConflicts.subList(
                            Math.max(0, context.activeConflicts.size() - 5),
                            context.activeConflicts.size()));
        }
        context.characters = context.characters.stream()
                .filter(c -> !"次要".equals(c.role) || context.characters.indexOf(c) > context.characters.size() - 20)
                .collect(Collectors.toList());
        context.recentSummary = volumeSummary;
    }
}
