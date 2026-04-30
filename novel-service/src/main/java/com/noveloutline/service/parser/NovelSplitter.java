package com.noveloutline.service.parser;

import com.noveloutline.common.entity.ParseRule;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NovelSplitter {

    private static final Logger log = LoggerFactory.getLogger(NovelSplitter.class);

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

    public SplitResult split(Path filePath, ParseRule rule) throws IOException {
        String content = readWithDetectedEncoding(filePath);

        SplitResult result = new SplitResult();

        String volumeRegex = rule.getVolumeRegex();
        String chapterRegex = rule.getChapterRegex();

        if (volumeRegex != null && !volumeRegex.trim().isEmpty()) {
            splitWithVolumes(content, volumeRegex, chapterRegex, result);
        } else {
            splitChaptersOnly(content, chapterRegex, result);
        }

        return result;
    }

    private void splitWithVolumes(String content, String volumeRegex, String chapterRegex, SplitResult result) {
        Pattern volPattern = Pattern.compile(volumeRegex, Pattern.MULTILINE);
        Pattern chPattern = Pattern.compile(chapterRegex, Pattern.MULTILINE);

        List<int[]> volRanges = new ArrayList<>();
        Matcher volMatcher = volPattern.matcher(content);
        int lastVolStart = -1;

        while (volMatcher.find()) {
            if (lastVolStart >= 0) {
                volRanges.add(new int[]{lastVolStart, volMatcher.start()});
            }
            lastVolStart = volMatcher.start();
        }
        if (lastVolStart >= 0) {
            volRanges.add(new int[]{lastVolStart, content.length()});
        }

        if (volRanges.isEmpty()) {
            splitChaptersOnly(content, chapterRegex, result);
            return;
        }

        int volIdx = 0;
        for (int[] range : volRanges) {
            String volContent = content.substring(range[0], range[1]);
            Matcher chMatcher = chPattern.matcher(volContent);

            int prevChStart = -1;
            String prevChTitle = null;
            List<int[]> chRanges = new ArrayList<>();
            List<String> chTitles = new ArrayList<>();

            while (chMatcher.find()) {
                if (prevChStart >= 0) {
                    chRanges.add(new int[]{prevChStart, chMatcher.start()});
                    chTitles.add(prevChTitle);
                }
                prevChStart = chMatcher.start();
                prevChTitle = chMatcher.group().trim();
            }
            if (prevChStart >= 0) {
                chRanges.add(new int[]{prevChStart, volContent.length()});
                chTitles.add(prevChTitle);
            }

            String firstLine = volContent.split("\\R", 2)[0].trim();
            String volTitle = firstLine.length() < 100 ? firstLine : ("第" + (volIdx + 1) + "卷");

            for (int i = 0; i < chRanges.size(); i++) {
                int[] cr = chRanges.get(i);
                ChapterSegment seg = new ChapterSegment();
                seg.volumeIndex = volIdx;
                seg.volumeTitle = volTitle;
                seg.title = chTitles.get(i);
                seg.content = volContent.substring(cr[0], cr[1]).trim();
                result.segments.add(seg);
            }

            if (!result.volumeTitles.contains(volTitle)) {
                result.volumeTitles.add(volTitle);
            }
            volIdx++;
        }
    }

    private void splitChaptersOnly(String content, String chapterRegex, SplitResult result) {
        Pattern chPattern = Pattern.compile(chapterRegex, Pattern.MULTILINE);
        Matcher chMatcher = chPattern.matcher(content);

        int prevStart = -1;
        String prevTitle = null;

        while (chMatcher.find()) {
            if (prevStart >= 0) {
                ChapterSegment seg = new ChapterSegment();
                seg.volumeIndex = 0;
                seg.volumeTitle = "正文";
                seg.title = prevTitle;
                seg.content = content.substring(prevStart, chMatcher.start()).trim();
                result.segments.add(seg);
            }
            prevStart = chMatcher.start();
            prevTitle = chMatcher.group().trim();
        }
        if (prevStart >= 0) {
            ChapterSegment seg = new ChapterSegment();
            seg.volumeIndex = 0;
            seg.volumeTitle = "正文";
            seg.title = prevTitle;
            seg.content = content.substring(prevStart).trim();
            result.segments.add(seg);
        }

        if (!result.volumeTitles.contains("正文")) {
            result.volumeTitles.add("正文");
        }
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
