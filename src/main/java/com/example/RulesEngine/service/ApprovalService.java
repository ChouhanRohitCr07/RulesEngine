package com.example.RulesEngine.service;

import com.example.RulesEngine.entity.Rule;
import com.example.RulesEngine.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalService {

    @Autowired
    private RuleRepository ruleRepository;

    private final ExpressionParser parser = new SpelExpressionParser();

    // Store the approval state for each rule ID
    private Map<Long, ApprovalState> approvalStates = new HashMap<>();

    public String processApproval(double amount, String businessUnit) {
        // Retrieve rules for the given business unit
        List<Rule> rules = ruleRepository.findByBusinessUnit(businessUnit);

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
                        return startSequentialApproval(rule);
                    }
                    return "Approval type '" + rule.getApprovalType() + "' is not implemented.";
                }
            } catch (Exception e) {
                System.err.println("Error evaluating condition '" + rule.getCondition() + "': " + e.getMessage());
                return "Error processing rule condition.";
            }
        }
        return "No applicable rule found for the provided conditions.";
    }

    private String startSequentialApproval(Rule rule) {
        // Parse the consequence to get the list of approvers
        if (rule.getConsequence().startsWith("require_approval(")) {
            String approversString = rule.getConsequence().substring("require_approval(".length(), rule.getConsequence().length() - 1);
            String[] approvers = approversString.split(", ");

            // Initialize the approval state
            ApprovalState approvalState = new ApprovalState(rule.getId(), approvers);
            approvalStates.put(rule.getId(), approvalState);

            return "Approval process started. Waiting for input from: " + approvalState.getCurrentApprover();
        }
        return "Invalid consequence format.";
    }

    public String handleApprovalInput(Long ruleId, String approver, boolean approved) {
        ApprovalState approvalState = approvalStates.get(ruleId);

        if (approvalState == null) {
            return "No active approval process for the provided rule ID.";
        }

        // Check if the current approver matches
        String currentApprover = approvalState.getCurrentApprover();
        if (!currentApprover.equals(approver)) {
            return "Invalid approver. Waiting for input from: " + currentApprover;
        }

        // Handle the approval/rejection
        if (approved) {
            if (approvalState.advanceToNextApprover()) {
                return "Approval granted by " + approver + ". Waiting for input from: " + approvalState.getCurrentApprover();
            } else {
                approvalStates.remove(ruleId); // Approval process completed
                return "Approval process completed successfully.";
            }
        } else {
            approvalStates.remove(ruleId); // Approval process terminated
            return approver + " rejected the approval. Process terminated.";
        }
    }
}
