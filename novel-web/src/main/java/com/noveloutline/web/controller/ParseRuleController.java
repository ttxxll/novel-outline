package com.noveloutline.web.controller;

import com.noveloutline.common.entity.ParseRule;
import com.noveloutline.service.ParseRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parse-rules")
@Slf4j
public class ParseRuleController {

    @Autowired
    private ParseRuleService service;
    @GetMapping
    public List<ParseRule> list() {
        log.debug("Listing parse rules");
        return service.listAll();
    }
    @PostMapping
    public ParseRule create(@RequestBody ParseRule rule) {
        log.info("Creating parse rule: name={}", rule.getName());
        return service.create(rule);
    }
    @PutMapping("/{id}")
    public ParseRule update(@PathVariable Long id, @RequestBody ParseRule rule) {
        log.info("Updating parse rule: id={}, name={}", id, rule.getName());
        return service.update(id, rule);
    }
}
