package com.famcare.config;

import com.famcare.model.Family;
import com.famcare.model.FamilyMember;
import com.famcare.model.User;
import com.famcare.repository.FamilyMemberRepository;
import com.famcare.repository.FamilyRepository;
import com.famcare.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seed(UserRepository users, FamilyRepository families, FamilyMemberRepository members, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                users.save(admin);
            }
            if (!users.existsByUsername("member1")) {
                User u = new User();
                u.setUsername("member1");
                u.setPassword(encoder.encode("pass123"));
                u.setRole("ROLE_FAMILY_MEMBER");
                users.save(u);
            }
            if (!users.existsByUsername("member2")) {
                User u = new User();
                u.setUsername("member2");
                u.setPassword(encoder.encode("pass123"));
                u.setRole("ROLE_FAMILY_MEMBER");
                users.save(u);
            }

            if (families.count() == 0) {
                Family f = new Family();
                f.setFamilyName("Smith Family");
                f.setDescription("Family of three tracking wellness");
                f = families.save(f);

                User m1 = users.findByUsername("member1").orElse(null);
                User m2 = users.findByUsername("member2").orElse(null);
                if (m1 != null) {
                    FamilyMember fm = new FamilyMember();
                    fm.setFamilyId(f.getId());
                    fm.setUserId(m1.getId());
                    fm.setRelation("Father");
                    members.save(fm);
                }
                if (m2 != null) {
                    FamilyMember fm = new FamilyMember();
                    fm.setFamilyId(f.getId());
                    fm.setUserId(m2.getId());
                    fm.setRelation("Mother");
                    members.save(fm);
                }
            }
        };
    }
}
