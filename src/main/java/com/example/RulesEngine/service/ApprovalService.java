package com.example.RulesEngine.service;

import com.example.RulesEngine.entity.Rule;
import com.example.RulesEngine.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalService {

    @Autowired
    private RuleRepository ruleRepository;

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * Process approval based on the amount and business unit.
     * @param amount the transaction amount
     * @param businessUnit the business unit for the rule
     * @return a message indicating the approval process
     */
    public String processApproval(double amount, String businessUnit) {
        // Retrieve rules for the given business unit
        List<Rule> rules = ruleRepository.findByBusinessUnit(businessUnit);

        for (int i = 0; i < rules.size(); i++) {
            System.out.println(rules.get(i));
        }
        System.out.println(amount);
        System.out.println(businessUnit);

        // Log the rules fetched for debugging
        System.out.println("Number of rules found for business unit " + businessUnit + ": " + rules.size());

        if (rules.isEmpty()) {
            return "No rules found for the provided business unit.";
        }

        for (Rule rule : rules) {
            try {
                // Substitute "amount" in the condition with the actual amount value
                String conditionWithAmount = rule.getCondition().replace("amount", String.valueOf(amount));

                // Log the modified condition for debugging
                System.out.println("Evaluating condition: " + conditionWithAmount);

                // Evaluate the modified condition directly
                Boolean conditionMet = parser.parseExpression(conditionWithAmount).getValue(Boolean.class);

                if (Boolean.TRUE.equals(conditionMet)) {
                    // If the condition is met, execute the consequence
                    return executeConsequence(rule.getConsequence());
                }
            } catch (Exception e) {
                System.err.println("Error evaluating condition '" + rule.getCondition() + "': " + e.getMessage());
                return "Error processing rule condition.";
            }
        }
        return "No applicable rule found for the provided conditions.";
    }

    /**
     * Parses and executes the consequence action.
     * @param consequence the action defined in the rule
     * @return a message indicating the approval action taken
     */
    private String executeConsequence(String consequence) {
        // Example consequence format: "require_approval(manager, director)"
        if (consequence.startsWith("require_approval(")) {
            // Parse the approval levels from the consequence string
            String approvalsString = consequence.substring("require_approval(".length(), consequence.length() - 1);
            String[] approvals = approvalsString.split(", ");

            StringBuilder approvalChain = new StringBuilder("Approval process initiated: ");
            for (String approval : approvals) {
                approvalChain.append(approval.trim()).append(" -> ");
                // Simulate actual approval requests here
            }
            // Remove the trailing " -> "
            approvalChain.setLength(approvalChain.length() - 4);
            return approvalChain.toString();
        }
        return "Unknown consequence action.";
    }
}
