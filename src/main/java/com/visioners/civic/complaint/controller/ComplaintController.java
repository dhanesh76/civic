package com.visioners.civic.complaint.controller;



import java.io.IOException;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.visioners.civic.auth.userdetails.UserPrincipal;
import com.visioners.civic.complaint.dto.ComplaintRaiseRequest;
import com.visioners.civic.complaint.dto.ComplaintRaiseResponse;
import com.visioners.civic.complaint.dto.ComplaintView;
import com.visioners.civic.complaint.model.IssueSeverity;
import com.visioners.civic.complaint.model.IssueStatus;
import com.visioners.civic.complaint.service.ComplaintService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("api/complaint")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> raiseComplaint(@Valid @RequestPart ComplaintRaiseRequest complaintRaiseDto, @RequestPart MultipartFile imageFile,  @AuthenticationPrincipal UserPrincipal principal) throws IOException{
        ComplaintRaiseResponse response = complaintService.raiseComplaint(complaintRaiseDto, imageFile, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{complaintId}")
    ResponseEntity<ComplaintView> getComplaintById(@PathVariable("complaintId") long complaintId){
        ComplaintView complaintView = complaintService.getComplaintById(complaintId);
        return ResponseEntity.ok(complaintView);
    }

    // returns paginated list of complaints raised by the user
    @GetMapping("/all")
        ResponseEntity<Page<ComplaintView>> getAllComplaintByUser(
            @AuthenticationPrincipal UserPrincipal principal, Pageable page,
            @RequestParam IssueSeverity severity,
            @RequestParam IssueStatus status,
            @RequestParam Date from,
            @RequestParam Date to) {

        Page<ComplaintView> complaintPage = complaintService.getAllComplaintByUser(principal, page, severity, status, from, to);
        
        return ResponseEntity.ok(complaintPage);
    }
}

