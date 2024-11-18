package com.example.RulesEngine.controller;

import com.example.RulesEngine.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.RulesEngine.entity.Rule;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping("/upload")
    public ResponseEntity<List<Rule>> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            List<Rule> rules = ruleService.saveRulesFromExcel(file);
            return ResponseEntity.ok(rules);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}

