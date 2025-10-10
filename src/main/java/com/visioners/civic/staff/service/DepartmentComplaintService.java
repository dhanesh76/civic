package com.visioners.civic.staff.service;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.visioners.civic.auth.userdetails.UserPrincipal;
import com.visioners.civic.complaint.Specifications.ComplaintSpecification;
import com.visioners.civic.complaint.dto.ComplaintView;
import com.visioners.civic.complaint.entity.Complaint;
import com.visioners.civic.complaint.model.IssueSeverity;
import com.visioners.civic.complaint.model.IssueStatus;
import com.visioners.civic.complaint.repository.ComplaintRepository;
import com.visioners.civic.complaint.service.ComplaintService;
import com.visioners.civic.staff.entity.Staff;
import com.visioners.civic.staff.repository.StaffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentComplaintService {

    private final StaffRepository staffRepository;
    private final ComplaintRepository complaintRepository;

    public Page<ComplaintView> viewDeptComplaints(UserPrincipal principal, Pageable page, IssueSeverity severity,
            IssueStatus status, Date from, Date to) {
        
        Staff staff = staffRepository.findByUser(principal.getUser()).orElseThrow(
            () -> new RuntimeException("invalid officer")
        );

        Specification<Complaint> specification = ComplaintSpecification.getComplaintSpecification(severity, status, from, to);
        specification.and(ComplaintSpecification.hasBlock(staff.getBlock()));
        specification.and(ComplaintSpecification.hasDepartment(staff.getDepartment()));
        specification.and(ComplaintSpecification.hasDistrict(staff.getDistrict()));

        Page<Complaint> complaint = complaintRepository.findAll(specification, page);

        return complaint.map(ComplaintService::convertToComplaintView);
    }
    
}
