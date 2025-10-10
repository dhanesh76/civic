package com.visioners.civic.complaint.service;


import java.io.IOException;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.visioners.civic.auth.userdetails.UserPrincipal;
import com.visioners.civic.aws.S3Service;
import com.visioners.civic.complaint.Specifications.ComplaintSpecification;
import com.visioners.civic.complaint.dto.ComplaintRaiseRequest;
import com.visioners.civic.complaint.dto.ComplaintRaiseResponse;
import com.visioners.civic.complaint.dto.ComplaintView;
import com.visioners.civic.complaint.entity.Block;
import com.visioners.civic.complaint.entity.Complaint;
import com.visioners.civic.complaint.entity.Department;
import com.visioners.civic.complaint.entity.District;
import com.visioners.civic.complaint.model.IssueSeverity;
import com.visioners.civic.complaint.model.IssueStatus;
import com.visioners.civic.complaint.model.Location;
import com.visioners.civic.complaint.repository.BlockRepository;
import com.visioners.civic.complaint.repository.ComplaintRepository;
import com.visioners.civic.complaint.repository.DepartmentRepository;
import com.visioners.civic.complaint.repository.DistrictRepository;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ComplaintService {
    
    private final ComplaintRepository complaintRepository;
    private final DistrictRepository districtRepository;
    private final BlockRepository blockRepository;
    private final DepartmentRepository departmentRepository;
    private final UsersRepository usersRepository;
    
    private final S3Service s3Service;

    public ComplaintRaiseResponse raiseComplaint(ComplaintRaiseRequest complaintRaiseDto, MultipartFile imageFile, UserPrincipal principal) throws IOException {
        
        Users raisedBy = usersRepository.findByMobileNumber(principal.getUsername()).get();
        
        Location location = complaintRaiseDto.location();
        District district = districtRepository.findByName(location.getSubAdminArea())
                            .orElseThrow(() -> new RuntimeException("invalid district"));

        Block block = blockRepository.findByName(location.getLocality())
                            .orElseThrow(() -> new RuntimeException("invalid block"));
                            
        /*
         * TODO
         * REQUEST HAS TO BE DELAGATED TO THE 
         * ML SERVER GET THE DETAIL OF DEPARTMENT TO WHICH TO
         * BE ROUTED  
         * 
         * RIGHT NOW USING A DUMMY DEPARTMENT FOR ALL REQUEST  
        */
        Department department = departmentRepository.findByName("ROAD_DEPARTMENT")
                                                    .orElseThrow(() -> new RuntimeException("invalid department"));

        /***
         * TODO 
         * SEVERITY NEED TO BE RETRIVED FROM THE 
         * ML SERVER BASED ON THE USER COMPLAINT DETIALS RIGHT NOW HARD CODING 
         * IT TO MEDIUM FOR ALL REQUEST 
         */
        IssueSeverity severity = IssueSeverity.MEDIUM;
        
        /*
          * TODO 
            THE CREDIBILITY OF THE IMAGE NEED TO BE
            ASSUERED BY THE ML SERVER, IF FOUND NOT LEGIT COMPLAINT WON'T BE PERSISTED 
            RN CONSIDERING ALL AS LEGIT  
        */
        String imageUrl = s3Service.uploadFile(imageFile);

        IssueStatus status = IssueStatus.OPEN;
        Complaint complaint = Complaint.builder()
                                        .description(complaintRaiseDto.description())        
                                        .raisedBy(raisedBy)
                                        .location(location)
                                        .district(district)
                                        .block(block)
                                        .department(department)
                                        .status(status)
                                        .imageUrl(imageUrl)
                                        .severity(severity)
                                        .build();
        complaintRepository.save(complaint);

        return new ComplaintRaiseResponse(department.getName(), severity, status, complaint.getCreatedAt());
    }

    public ComplaintView getComplaintById(long complaintId) {
        Complaint complaint = complaintRepository.getReferenceById(complaintId);

        return ComplaintView.builder()
                    .raidedBy(complaint.getRaisedBy().getMobileNumber())
                    .imageUrl(complaint.getImageUrl())
                    .assignedBy(complaint.getAssignedBy().getUser().getUsername())
                    .assignedTo(complaint.getAssignedTo().getUser().getUsername())
                    .severity(complaint.getSeverity())
                    .status(complaint.getStatus())
                    .solutionImageUrl(complaint.getSolutionImageUrl())
                    .solutionNote(complaint.getSolutionNote())
                    .location(complaint.getLocation())
                    .build();
    }

    public Page<ComplaintView> getAllComplaintByUser(UserPrincipal principal, Pageable page, IssueSeverity severity, IssueStatus status, Date from, Date to){
        Specification<Complaint> specification = ComplaintSpecification.getComplaintSpecification(severity, status, from, to);

        specification.and(ComplaintSpecification.hasRaisedBy(principal.getUser()));
        Page<Complaint> complaint = complaintRepository.findAll(specification, page);

        return complaint.map(ComplaintService::convertToComplaintView);
    }    

    public static ComplaintView convertToComplaintView(Complaint complaint){
        return ComplaintView.builder()
                    .id(complaint.getId())
                    .raidedBy(complaint.getRaisedBy().getMobileNumber())
                    .imageUrl(complaint.getImageUrl())
                    .assignedBy(complaint.getAssignedBy().getUser().getUsername())
                    .assignedTo(complaint.getAssignedTo().getUser().getUsername())
                    .severity(complaint.getSeverity())
                    .status(complaint.getStatus())
                    .solutionImageUrl(complaint.getSolutionImageUrl())
                    .solutionNote(complaint.getSolutionNote())
                    .location(complaint.getLocation())
                    .build();
    }
}
