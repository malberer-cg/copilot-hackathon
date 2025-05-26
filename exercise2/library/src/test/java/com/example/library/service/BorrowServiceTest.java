package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookService bookService;

    @Mock
    private MemberService memberService;

    private BorrowService borrowService;

    @BeforeEach
    void setUp() {
        borrowService = new BorrowService(borrowRecordRepository, bookService, memberService);
    }

    @Test
    void borrowBookShouldCreateBorrowRecordWhenBookAndMemberExistAndBookIsAvailable() {
        // Given
        Long memberId = 1L;
        Long bookId = 1L;
        Member member = Member.builder().id(memberId).name("John").build();
        Book book = Book.builder().id(bookId).title("Test Book").available(true).build();
        BorrowRecord expectedRecord = BorrowRecord.builder()
            .member(member)
            .book(book)
            .borrowDate(LocalDateTime.now())
            .build();

        when(memberService.getMemberById(memberId)).thenReturn(Optional.of(member));
        when(bookService.getBookById(bookId)).thenReturn(Optional.of(book));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(expectedRecord);

        // When
        BorrowRecord actualRecord = borrowService.borrowBook(memberId, bookId);

        // Then
        assertThat(actualRecord)
            .isNotNull()
            .extracting("member", "book", "borrowDate")
            .doesNotContainNull();
        verify(bookService).updateBookAvailability(bookId, false);
    }

    @Test
    void borrowBookShouldThrowExceptionWhenMemberNotFound() {
        // Given
        Long memberId = 1L;
        Long bookId = 1L;
        when(memberService.getMemberById(memberId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> borrowService.borrowBook(memberId, bookId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Member not found");
    }

    @Test
    void borrowBookShouldThrowExceptionWhenBookNotFound() {
        // Given
        Long memberId = 1L;
        Long bookId = 1L;
        Member member = Member.builder().id(memberId).name("John").build();
        when(memberService.getMemberById(memberId)).thenReturn(Optional.of(member));
        when(bookService.getBookById(bookId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> borrowService.borrowBook(memberId, bookId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Book not found");
    }

    @Test
    void borrowBookShouldThrowExceptionWhenBookNotAvailable() {
        // Given
        Long memberId = 1L;
        Long bookId = 1L;
        Member member = Member.builder().id(memberId).name("John").build();
        Book book = Book.builder().id(bookId).title("Test Book").available(false).build();
        when(memberService.getMemberById(memberId)).thenReturn(Optional.of(member));
        when(bookService.getBookById(bookId)).thenReturn(Optional.of(book));

        // When/Then
        assertThatThrownBy(() -> borrowService.borrowBook(memberId, bookId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Book is not available");
    }

    @Test
    void returnBookShouldUpdateRecordAndBookAvailabilityWhenBorrowExists() {
        // Given
        Long memberId = 1L;
        Long bookId = 1L;
        Member member = Member.builder().id(memberId).name("John").build();
        Book book = Book.builder().id(bookId).title("Test Book").build();
        BorrowRecord existingRecord = BorrowRecord.builder()
            .id(1L)
            .member(member)
            .book(book)
            .borrowDate(LocalDateTime.now().minusDays(1))
            .build();

        when(borrowRecordRepository.findByMember_IdAndBook_IdAndReturnDateIsNull(memberId, bookId))
            .thenReturn(Optional.of(existingRecord));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(i -> i.getArgument(0));

        // When
        borrowService.returnBook(memberId, bookId);

        // Then
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
        verify(bookService).updateBookAvailability(bookId, true);
    }

    @Test
    void returnBookShouldThrowExceptionWhenNoBorrowRecordFound() {
        // Given
        Long memberId = 1L;
        Long bookId = 1L;
        when(borrowRecordRepository.findByMember_IdAndBook_IdAndReturnDateIsNull(memberId, bookId))
            .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> borrowService.returnBook(memberId, bookId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No active borrow record found");
    }

    @Test
    void getBorrowHistoryShouldReturnAllRecordsOrderedByBorrowDate() {
        // Given
        List<BorrowRecord> expectedRecords = List.of(
            BorrowRecord.builder()
                .borrowDate(LocalDateTime.now())
                .member(Member.builder().name("John").build())
                .book(Book.builder().title("Book 1").build())
                .build(),
            BorrowRecord.builder()
                .borrowDate(LocalDateTime.now().minusDays(1))
                .member(Member.builder().name("Jane").build())
                .book(Book.builder().title("Book 2").build())
                .build()
        );
        when(borrowRecordRepository.findAllByOrderByBorrowDateDesc()).thenReturn(expectedRecords);

        // When
        List<BorrowRecord> actualRecords = borrowService.getBorrowHistory();

        // Then
        assertThat(actualRecords)
            .hasSize(2)
            .extracting(record -> record.getBook().getTitle())
            .containsExactly("Book 1", "Book 2");
    }
}
