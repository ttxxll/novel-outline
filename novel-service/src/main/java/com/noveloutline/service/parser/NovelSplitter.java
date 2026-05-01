package com.noveloutline.service.parser;

import com.noveloutline.common.entity.ParseRule;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NovelSplitter {
    public static class ChapterSegment {
        public String title;
        public String content;
        public int volumeIndex;
        public String volumeTitle;
    }
    public static class SplitResult {
        public List<String> volumeTitles = new ArrayList<>();
        public List<ChapterSegment> segments = new ArrayList<>();
    }
    private static final int CHAPTERS_PER_VOLUME = 100;

    public SplitResult split(Path filePath, ParseRule rule) throws IOException {
        String content = readWithDetectedEncoding(filePath);
        log.info("File content length: {} chars", content.length());
        SplitResult result = new SplitResult();
        String chapterRegex = rule.getChapterRegex();
        log.info("Splitting chapters: chapterRegex={}", chapterRegex);

        List<String[]> chapterPairs = findAllChapters(content, chapterRegex);

        int volumeCount = (int) Math.ceil((double) chapterPairs.size() / CHAPTERS_PER_VOLUME);
        for (int v = 0; v < volumeCount; v++) {
            int startCh = v * CHAPTERS_PER_VOLUME + 1;
            int endCh = Math.min((v + 1) * CHAPTERS_PER_VOLUME, chapterPairs.size());
            result.volumeTitles.add("第" + (v + 1) + "卷 (第" + startCh + "-" + endCh + "章)");
        }

        for (int i = 0; i < chapterPairs.size(); i++) {
            String[] pair = chapterPairs.get(i);
            ChapterSegment seg = new ChapterSegment();
            seg.volumeIndex = i / CHAPTERS_PER_VOLUME;
            seg.volumeTitle = result.volumeTitles.get(seg.volumeIndex);
            seg.title = pair[0];
            seg.content = pair[1];
            result.segments.add(seg);
        }

        log.info("Split result: {} volumes, {} chapters", result.volumeTitles.size(), result.segments.size());
        for (int i = 0; i < result.segments.size(); i++) {
            ChapterSegment seg = result.segments.get(i);
            log.debug("  [{}/{}] {} > {} ({} chars)",
                    i + 1, result.segments.size(), seg.volumeTitle, seg.title, seg.content.length());
        }
        return result;
    }

    private List<String[]> findAllChapters(String content, String chapterRegex) {
        List<String[]> chapters = new ArrayList<>();
        Pattern chPattern = Pattern.compile(chapterRegex, Pattern.MULTILINE);
        Matcher chMatcher = chPattern.matcher(content);
        int prevStart = -1;
        String prevTitle = null;
        while (chMatcher.find()) {
            if (prevStart >= 0) {
                chapters.add(new String[]{prevTitle, content.substring(prevStart, chMatcher.start()).trim()});
            }
            prevStart = chMatcher.start();
            prevTitle = chMatcher.group().trim();
        }
        if (prevStart >= 0) {
            chapters.add(new String[]{prevTitle, content.substring(prevStart).trim()});
        }
        return chapters;
    }
    private String readWithDetectedEncoding(Path filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(filePath);
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String detected = detector.getDetectedCharset();
        detector.reset();
        String encoding = (detected != null) ? detected : "UTF-8";
        log.info("Detected encoding: {} for file: {}", encoding, filePath.getFileName());
        Charset charset;
        try {
            charset = Charset.forName(encoding);
        } catch (Exception e) {
            charset = Charset.forName("GBK");
        }
        return new String(bytes, charset);
    }
}
