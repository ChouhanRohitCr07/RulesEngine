package com.example.RulesEngine.service;

import com.example.RulesEngine.entity.Rule;
import com.example.RulesEngine.repository.RuleRepository;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    public List<Rule> saveRulesFromExcel(MultipartFile file) throws IOException {
        List<Rule> rulesToSave = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            Long ruleId = Long.parseLong(dataFormatter.formatCellValue(row.getCell(0))); // Assume the ID is in the first column
            String businessUnit = dataFormatter.formatCellValue(row.getCell(1));
            String condition = dataFormatter.formatCellValue(row.getCell(2));
            String consequence = dataFormatter.formatCellValue(row.getCell(3));
            String description = dataFormatter.formatCellValue(row.getCell(4));
            String approvalType = dataFormatter.formatCellValue(row.getCell(5));

            // Check if a rule with the same ID and businessUnit exists
            Rule existingRule = ruleRepository.findByIdAndBusinessUnit(ruleId, businessUnit);

            if (existingRule != null) {
                // Update the existing rule
                existingRule.setCondition(condition);
                existingRule.setConsequence(consequence);
                existingRule.setDescription(description);
                existingRule.setApprovalType(approvalType);
                rulesToSave.add(existingRule);
            } else {
                // Create a new rule
                Rule rule = new Rule();
                rule.setId(ruleId);
                rule.setBusinessUnit(businessUnit);
                rule.setCondition(condition);
                rule.setConsequence(consequence);
                rule.setDescription(description);
                rule.setApprovalType(approvalType);
                rulesToSave.add(rule);
            }
        }

        workbook.close();
        return ruleRepository.saveAll(rulesToSave);
    }



    /**
     * Load rules from a predefined Excel file at application startup.
     */
    @PostConstruct
    public void loadInitialRules() {
        try {
            File file = new File("src/main/resources/sample_data.xlsx"); // Specify the path to your Excel file
            if (!file.exists()) {
                System.out.println("Initial rules file not found. Skipping initial load.");
                return;
            }

            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            List<Rule> rules = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                Rule rule = new Rule();

                // Use DataFormatter to convert each cell to a string
                rule.setBusinessUnit(dataFormatter.formatCellValue(row.getCell(1)));
                rule.setCondition(dataFormatter.formatCellValue(row.getCell(2)));
                rule.setConsequence(dataFormatter.formatCellValue(row.getCell(3)));
                rule.setDescription(dataFormatter.formatCellValue(row.getCell(4)));
                rule.setApprovalType(dataFormatter.formatCellValue(row.getCell(5)));

                rules.add(rule);
            }
            workbook.close();

            ruleRepository.saveAll(rules);
            System.out.println("Initial rules loaded successfully from Excel file.");
        } catch (IOException e) {
            System.err.println("Failed to load initial rules from Excel file: " + e.getMessage());
        }
    }
}

