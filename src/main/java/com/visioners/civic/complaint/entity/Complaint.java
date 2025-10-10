package com.visioners.civic.complaint.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.visioners.civic.complaint.model.IssueSeverity;
import com.visioners.civic.complaint.model.IssueStatus;
import com.visioners.civic.complaint.model.Location;
import com.visioners.civic.staff.entity.Staff;
import com.visioners.civic.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
/*{
        "description":"jnenkjek",
        "category":"road",
        "subCategory":"pothole",
        "location" : {
            "latitude": 12.9716,
            "longitude": 77.5946,
            "accuracy": 5.0,
            "street": "MG Road",
            "locality": "Bangalore",
            "subLocality": "Ashok Nagar",
            "subAdminArea": "Bangalore Urban",
            "adminArea": "Karnataka",
            "postalCode": "560001",
            "country": "India",
            isoCountryCode": "IN"
        }
    }
    */

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 1000)
    String description;

    @Enumerated(EnumType.STRING)
    IssueSeverity severity;

    @Embedded
    Location location;  

    @Column(unique = true, nullable = false, name = "image_url")
    String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    IssueStatus status;

    @ManyToOne
    @JoinColumn(name = "raised_by_id", nullable = false)
    @JsonManagedReference
    Users raisedBy;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    @JsonManagedReference
    District district;

    @ManyToOne
    @JoinColumn(name = "block_id", nullable = false)
    @JsonManagedReference
    Block block;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @JsonManagedReference
    Department department;


    @ManyToOne
    @JoinColumn(name = "assigned_by_staff_id")
    @JsonManagedReference
    Staff assignedBy;
    
    @ManyToOne
    @JoinColumn(name = "assigned_staff_id")
    @JsonManagedReference
    Staff assignedTo;

    @Column(unique = true)
    String solutionImageUrl;
    
    String solutionNote;

    @ManyToOne
    @JoinColumn(name = "approved_by_id")
    @JsonManagedReference
    Staff approvedBy;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
}
