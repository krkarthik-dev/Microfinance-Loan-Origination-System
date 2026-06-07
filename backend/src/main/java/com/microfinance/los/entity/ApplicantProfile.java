package com.microfinance.los.entity;

import com.microfinance.los.security.PiiAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "applicant_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = false)
    private String address;

    @Convert(converter = PiiAttributeConverter.class)
    @Column(name = "aadhaar_number", nullable = false, unique = true)
    private String aadhaarNumber;

    @Convert(converter = PiiAttributeConverter.class)
    @Column(name = "pan_number", nullable = false, unique = true)
    private String panNumber;

}
