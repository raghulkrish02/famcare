package com.famcare.repository;

import com.famcare.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    List<FamilyMember> findByFamilyId(Long familyId);

    // --- ADD THIS METHOD ---
    // This will delete all FamilyMember entries that match a familyId
    void deleteByFamilyId(Long familyId);
    void deleteByUserId(Long userId);
}