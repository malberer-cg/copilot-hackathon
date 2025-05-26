package com.example.library;

import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.Member;
import com.example.library.service.BookService;
import com.example.library.service.BorrowService;
import com.example.library.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BorrowService borrowService;

    @Test
    void testCompleteBookBorrowingFlow() {
        // Given - Create a book
        Book book = bookService.addBook(
            "Clean Code",
            "Robert C. Martin",
            "978-0132350884"
        );
        assertThat(book.isAvailable()).isTrue();

        // And - Create a member
        Member member = memberService.registerMember(
            "John Doe",
            "john@example.com",
            "123-456-7890"
        );
        assertThat(member.getId()).isNotNull();

        // When - Borrow the book
        BorrowRecord borrowRecord = borrowService.borrowBook(member.getId(), book.getId());

        // Then - Book should be borrowed successfully
        assertThat(borrowRecord).isNotNull();
        assertThat(borrowRecord.getBook().getId()).isEqualTo(book.getId());
        assertThat(borrowRecord.getMember().getId()).isEqualTo(member.getId());
        assertThat(borrowRecord.getBorrowDate()).isNotNull();
        assertThat(borrowRecord.getReturnDate()).isNull();

        // And - Book should no longer be available
        Book borrowedBook = bookService.getBookById(book.getId())
            .orElseThrow();
        assertThat(borrowedBook.isAvailable()).isFalse();

        // When - Return the book
        borrowService.returnBook(member.getId(), book.getId());

        // Then - Book should be returned and available again
        Book returnedBook = bookService.getBookById(book.getId())
            .orElseThrow();
        assertThat(returnedBook.isAvailable()).isTrue();

        // And - Borrow history should be updated
        BorrowRecord updatedRecord = borrowService.getBorrowHistory().stream()
            .filter(r -> r.getBook().getId().equals(book.getId()))
            .findFirst()
            .orElseThrow();
        assertThat(updatedRecord.getReturnDate()).isNotNull();
    }

    @Test
    void testBorrowingUnavailableBookShouldFail() {
        // Given - Create a book and a member
        Book book = bookService.addBook(
            "Design Patterns",
            "Gang of Four",
            "978-0201633610"
        );

        Member member1 = memberService.registerMember(
            "Alice",
            "alice@example.com",
            "111-222-3333"
        );

        Member member2 = memberService.registerMember(
            "Bob",
            "bob@example.com",
            "444-555-6666"
        );

        // When - First member borrows the book
        borrowService.borrowBook(member1.getId(), book.getId());

        // Then - Second member should not be able to borrow the same book
        assertThrows(IllegalStateException.class, () -> 
            borrowService.borrowBook(member2.getId(), book.getId())
        );
    }

    @Test
    void testReturningBookNotBorrowedByMemberShouldFail() {
        // Given - Create a book and two members
        Book book = bookService.addBook(
            "Refactoring",
            "Martin Fowler",
            "978-0134757599"
        );

        Member borrower = memberService.registerMember(
            "Charlie",
            "charlie@example.com",
            "777-888-9999"
        );

        Member nonBorrower = memberService.registerMember(
            "David",
            "david@example.com",
            "000-111-2222"
        );

        // When - First member borrows the book
        borrowService.borrowBook(borrower.getId(), book.getId());

        // Then - Non-borrower should not be able to return the book
        assertThrows(IllegalArgumentException.class, () ->
            borrowService.returnBook(nonBorrower.getId(), book.getId())
        );
    }
}
