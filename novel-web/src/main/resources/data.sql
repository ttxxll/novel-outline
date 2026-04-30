INSERT IGNORE INTO parse_rule (id, name, volume_regex, chapter_regex, enabled) VALUES
(1, '起点常见格式', '第[一二三四五六七八九十百千零\\d]+卷\\s*.*', '第[一二三四五六七八九十百千零\\d]+章\\s*.*', 1),
(2, '纯章切分（无卷）', NULL, '第[一二三四五六七八九十百千零\\d]+章\\s*.*', 1),
(3, '数字序号格式', NULL, '^\\d+[\\.、\\s]+.*$', 1);
