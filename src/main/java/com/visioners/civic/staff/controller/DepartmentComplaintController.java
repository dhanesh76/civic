package com.visioners.civic.staff.controller;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.visioners.civic.auth.userdetails.UserPrincipal;
import com.visioners.civic.complaint.dto.ComplaintView;
import com.visioners.civic.complaint.model.IssueSeverity;
import com.visioners.civic.complaint.model.IssueStatus;
import com.visioners.civic.staff.service.DepartmentComplaintService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/department/")
@RequiredArgsConstructor
public class DepartmentComplaintController {

    private final DepartmentComplaintService departmentComplaintService;

    @GetMapping
    ResponseEntity<Page<ComplaintView>> viewDeptComplaints(
        @AuthenticationPrincipal UserPrincipal principal,
        Pageable page,
        @RequestParam IssueSeverity severity,
        @RequestParam IssueStatus status,
        @RequestParam Date from,
        @RequestParam Date to
    ){
        Page<ComplaintView> complaintPage = departmentComplaintService
                                .viewDeptComplaints(principal, page, severity, status, from, to);
        
        return ResponseEntity.ok(complaintPage);
    }
}
