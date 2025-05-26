package com.example.library.service;

import com.example.library.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    
    public Member registerMember(String name, String email, String phone) {
        Member member = Member.builder()
            .name(name)
            .email(email)
            .phone(phone)
            .build();
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
}

interface MemberRepository extends JpaRepository<Member, Long> {
}
