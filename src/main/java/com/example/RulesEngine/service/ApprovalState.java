package com.example.RulesEngine.service;

public class ApprovalState {
    private Long ruleId;
    private String[] approvers;
    private int currentApproverIndex;

    public ApprovalState(Long ruleId, String[] approvers) {
        this.ruleId = ruleId;
        this.approvers = approvers;
        this.currentApproverIndex = 0; // Start with the first approver
    }

    public String getCurrentApprover() {
        if (currentApproverIndex < approvers.length) {
            return approvers[currentApproverIndex];
        }
        return null; // No more approvers
    }

    public boolean advanceToNextApprover() {
        currentApproverIndex++;
        return currentApproverIndex < approvers.length; // Return true if there are more approvers
    }
}
