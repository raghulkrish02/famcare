package com.famcare.service;

import com.famcare.model.Family;
import com.famcare.model.FamilyMember;
import com.famcare.repository.FamilyMemberRepository;
import com.famcare.repository.FamilyRepository;
import org.springframework.stereotype.Service;

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

    public Family saveFamily(Family f) { return familyRepository.save(f); }
    public List<Family> allFamilies() { return familyRepository.findAll(); }
    public Optional<Family> getFamily(Long id) { return familyRepository.findById(id); }

    public FamilyMember addMember(FamilyMember fm) { return familyMemberRepository.save(fm); }
    public List<FamilyMember> membersOf(Long familyId) { return familyMemberRepository.findByFamilyId(familyId); }
    public List<FamilyMember> membershipOfUser(Long userId) { return familyMemberRepository.findByUserId(userId); }
}
