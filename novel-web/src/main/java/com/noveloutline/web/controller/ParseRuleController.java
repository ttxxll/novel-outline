package com.noveloutline.web.controller;

import com.noveloutline.common.entity.ParseRule;
import com.noveloutline.service.ParseRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parse-rules")
public class ParseRuleController {

    private static final Logger log = LoggerFactory.getLogger(ParseRuleController.class);

    private final ParseRuleService service;

    public ParseRuleController(ParseRuleService service) {
        this.service = service;
    }

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
