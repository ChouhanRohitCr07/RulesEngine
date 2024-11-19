package com.example.RulesEngine.service;

import com.example.RulesEngine.entity.Rule;
import com.example.RulesEngine.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
public class ApprovalService {

    @Autowired
    private RuleRepository ruleRepository;

    private final ExpressionParser parser = new SpelExpressionParser();

    public String processApproval(double amount, String businessUnit) {
        // Retrieve rules for the given business unit
        List<Rule> rules = ruleRepository.findByBusinessUnit(businessUnit);

        for (int i = 0; i < rules.size(); i++) {
            System.out.println(rules.get(i));
        }
        System.out.println(amount);
        System.out.println(businessUnit);

        if (rules.isEmpty()) {
            return "No rules found for the provided business unit.";
        }

        for (Rule rule : rules) {
            try {
                // Evaluate the condition
                Boolean conditionMet = parser.parseExpression(rule.getCondition().replace("amount", String.valueOf(amount))).getValue(Boolean.class);

                if (Boolean.TRUE.equals(conditionMet)) {
                    // Check approval type and call the appropriate handler
                    if ("Sequential".equalsIgnoreCase(rule.getApprovalType())) {
                        return handleSequentialApproval(rule.getConsequence());
                    }
                    // You can add handling for other approval types here, e.g., Parallel or Hierarchical
                    return "Approval type '" + rule.getApprovalType() + "' is not implemented.";
                }
            } catch (Exception e) {
                System.err.println("Error evaluating condition '" + rule.getCondition() + "': " + e.getMessage());
                return "Error processing rule condition.";
            }
        }
        return "No applicable rule found for the provided conditions.";
    }

    private String handleSequentialApproval(String consequence) {
        if (consequence.startsWith("require_approval(")) {
            String approversString = consequence.substring("require_approval(".length(), consequence.length() - 1);
            String[] approvers = approversString.split(", ");

            for (String approver : approvers) {
                // Simulate asking for user input for approval/rejection
                boolean approved = requestApproval(approver.trim());

                if (!approved) {
                    return approver + " rejected the approval.";
                }
            }
            return "All approvals completed successfully.";
        }
        return "Invalid consequence format.";
    }

    private boolean requestApproval(String approver) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Approval required from: " + approver);
        System.out.print("Approve or Reject (A/R): ");

        String input = scanner.nextLine().trim().toUpperCase();
        return "A".equals(input);
    }

}
