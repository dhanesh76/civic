package com.visioners.civic.complaint.dto;

import java.time.Instant;

import com.visioners.civic.complaint.model.IssueSeverity;
import com.visioners.civic.complaint.model.IssueStatus;

public record ComplaintRaiseResponse(
    String department,
    IssueSeverity severity,
    IssueStatus status,
    Instant createdAt
) {
}
