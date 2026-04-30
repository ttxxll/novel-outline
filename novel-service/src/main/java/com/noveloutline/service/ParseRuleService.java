package com.noveloutline.service;

import com.noveloutline.common.entity.ParseRule;
import com.noveloutline.common.mapper.ParseRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParseRuleService {

    private static final Logger log = LoggerFactory.getLogger(ParseRuleService.class);

    private final ParseRuleMapper mapper;

    public ParseRuleService(ParseRuleMapper mapper) {
        this.mapper = mapper;
    }

    public List<ParseRule> listAll() {
        return mapper.findAll();
    }

    public ParseRule getById(Long id) {
        ParseRule rule = mapper.findById(id);
        if (rule == null) {
            throw new RuntimeException("ParseRule not found: " + id);
        }
        return rule;
    }

    public ParseRule create(ParseRule rule) {
        mapper.insert(rule);
        log.info("ParseRule created: id={}, name={}", rule.getId(), rule.getName());
        return rule;
    }

    public ParseRule update(Long id, ParseRule updated) {
        ParseRule existing = getById(id);
        existing.setName(updated.getName());
        existing.setVolumeRegex(updated.getVolumeRegex());
        existing.setChapterRegex(updated.getChapterRegex());
        existing.setEnabled(updated.getEnabled());
        mapper.update(existing);
        log.info("ParseRule updated: id={}, name={}", existing.getId(), existing.getName());
        return existing;
    }
}
