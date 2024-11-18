package com.example.RulesEngine.controller;

import com.example.RulesEngine.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    /**
     * Endpoint to process approvals based on amount and business unit.
     * @param amount the transaction amount
     * @param businessUnit the business unit for the rule
     * @return a message indicating the approval process outcome
     */
    @GetMapping("/process")
    public String processApproval(
            @RequestParam double amount,
            @RequestParam String businessUnit) {
        return approvalService.processApproval(amount, businessUnit);
    }
}
