package com.example.library.service;

import com.example.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    void registerMemberShouldCreateNewMemberWithGivenDetails() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String phone = "1234567890";
        Member expectedMember = Member.builder()
            .name(name)
            .email(email)
            .phone(phone)
            .build();
        when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

        // When
        Member actualMember = memberService.registerMember(name, email, phone);

        // Then
        assertThat(actualMember)
            .isNotNull()
            .extracting("name", "email", "phone")
            .containsExactly(name, email, phone);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void getAllMembersShouldReturnAllMembers() {
        // Given
        List<Member> expectedMembers = List.of(
            Member.builder().name("John").email("john@example.com").phone("123").build(),
            Member.builder().name("Jane").email("jane@example.com").phone("456").build()
        );
        when(memberRepository.findAll()).thenReturn(expectedMembers);

        // When
        List<Member> actualMembers = memberService.getAllMembers();

        // Then
        assertThat(actualMembers)
            .hasSize(2)
            .extracting("name")
            .containsExactly("John", "Jane");
    }

    @Test
    void getMemberByIdShouldReturnMemberWhenExists() {
        // Given
        Long memberId = 1L;
        Member expectedMember = Member.builder()
            .id(memberId)
            .name("John Doe")
            .email("john@example.com")
            .phone("1234567890")
            .build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(expectedMember));

        // When
        Optional<Member> actualMember = memberService.getMemberById(memberId);

        // Then
        assertThat(actualMember).isPresent();
        assertThat(actualMember.get())
            .extracting("id", "name", "email", "phone")
            .containsExactly(memberId, "John Doe", "john@example.com", "1234567890");
    }

    @Test
    void getMemberByIdShouldReturnEmptyWhenNotExists() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When
        Optional<Member> actualMember = memberService.getMemberById(memberId);

        // Then
        assertThat(actualMember).isEmpty();
    }
}
