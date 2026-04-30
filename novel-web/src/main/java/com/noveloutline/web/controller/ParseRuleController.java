package com.noveloutline.web.controller;

import com.noveloutline.common.entity.ParseRule;
import com.noveloutline.service.ParseRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parse-rules")
public class ParseRuleController {

    private final ParseRuleService service;

    public ParseRuleController(ParseRuleService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParseRule> list() {
        return service.listAll();
    }

    @PostMapping
    public ParseRule create(@RequestBody ParseRule rule) {
        return service.create(rule);
    }

    @PutMapping("/{id}")
    public ParseRule update(@PathVariable Long id, @RequestBody ParseRule rule) {
        return service.update(id, rule);
    }
}
