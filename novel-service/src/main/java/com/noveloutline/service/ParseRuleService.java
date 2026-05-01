package com.noveloutline.service;

import com.noveloutline.common.entity.ParseRule;
import com.noveloutline.common.mapper.ParseRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ParseRuleService {

    @Autowired
    private ParseRuleMapper mapper;
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
