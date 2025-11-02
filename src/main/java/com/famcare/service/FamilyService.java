package com.famcare.service;

import com.famcare.model.Family;
import com.famcare.model.FamilyMember;
import com.famcare.repository.FamilyMemberRepository;
import com.famcare.repository.FamilyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.List;
import java.util.Optional;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    public FamilyService(FamilyRepository familyRepository, FamilyMemberRepository familyMemberRepository) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    public List<Family> allFamilies() {
        return familyRepository.findAll();
    }

    public Family saveFamily(Family family) {
        return familyRepository.save(family);
    }

    public FamilyMember addMember(FamilyMember fm) {
        return familyMemberRepository.save(fm);
    }

    public List<FamilyMember> allMembers() {
        return familyMemberRepository.findAll();
    }

    public Optional<Family> findFamilyById(Long id) {
        return familyRepository.findById(id);
    }

    public List<FamilyMember> findMembersByFamilyId(Long familyId) {
        return familyMemberRepository.findByFamilyId(familyId);
    }

    @Transactional // Add this annotation
    public void removeMember(Long memberId) {
        familyMemberRepository.deleteById(memberId);
    }

    // --- ADD THIS NEW METHOD ---
    @Transactional // This is very important
    public void deleteFamily(Long familyId) {
        // 1. Delete all member links first
        familyMemberRepository.deleteByFamilyId(familyId);

        // 2. Then, delete the family itself
        familyRepository.deleteById(familyId);
    }
}