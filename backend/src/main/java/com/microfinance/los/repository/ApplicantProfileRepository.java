package com.microfinance.los.repository;

import com.microfinance.los.entity.ApplicantProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicantProfileRepository extends JpaRepository<ApplicantProfile, UUID> {
    Optional<ApplicantProfile> findByUserId(UUID userId);
}
