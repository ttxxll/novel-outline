package com.noveloutline.service;

import com.noveloutline.common.entity.ParseRule;
import com.noveloutline.common.mapper.ParseRuleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParseRuleService {

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
        return rule;
    }

    public ParseRule update(Long id, ParseRule updated) {
        ParseRule existing = getById(id);
        existing.setName(updated.getName());
        existing.setVolumeRegex(updated.getVolumeRegex());
        existing.setChapterRegex(updated.getChapterRegex());
        existing.setEnabled(updated.getEnabled());
        mapper.update(existing);
        return existing;
    }
}
