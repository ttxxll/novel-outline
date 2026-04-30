package com.noveloutline.analyzer;

import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class NovelContextManager {

    public NovelContext createInitial() {
        return new NovelContext();
    }

    public void applyChapterResult(NovelContext context, ChapterAnalysisResult result, int chapterIndex) {
        if (result == null) return;

        if (context.protagonist == null && result.characters != null) {
            result.characters.stream()
                    .filter(c -> "主角".equals(c.role))
                    .findFirst()
                    .ifPresent(c -> context.protagonist = c.name);
        }

        if (result.characters != null) {
            for (ChapterAnalysisResult.CharacterEntry ch : result.characters) {
                java.util.Optional<ChapterAnalysisResult.CharacterEntry> existing = context.characters.stream()
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
            for (ChapterAnalysisResult.FactionEntry f : result.factions) {
                java.util.Optional<ChapterAnalysisResult.FactionEntry> existing = context.activeFactions.stream()
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

    public void pruneAfterVolume(NovelContext context, String volumeSummary) {
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
