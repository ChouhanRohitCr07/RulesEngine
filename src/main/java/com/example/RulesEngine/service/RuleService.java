package com.example.RulesEngine.service;

import com.example.RulesEngine.entity.Rule;
import com.example.RulesEngine.repository.RuleRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    public List<Rule> saveRulesFromExcel(MultipartFile file) throws IOException {
        List<Rule> rules = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            Rule rule = new Rule();

            // Use DataFormatter to convert each cell to a string
            rule.setBusinessUnit(dataFormatter.formatCellValue(row.getCell(1)));
            rule.setCondition(dataFormatter.formatCellValue(row.getCell(2)));
            rule.setConsequence(dataFormatter.formatCellValue(row.getCell(3)));
            rule.setDescription(dataFormatter.formatCellValue(row.getCell(4)));

            rules.add(rule);
        }
        workbook.close();
        return ruleRepository.saveAll(rules);
    }
}
