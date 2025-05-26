package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BorrowService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookService bookService;
    private final MemberService memberService;
    
    public BorrowRecord borrowBook(Long memberId, Long id) {
        var memberOpt = memberService.getMemberById(memberId);
        var bookOpt = bookService.getBookById(id);

        if (!memberOpt.isPresent()) {
            throw new IllegalArgumentException("Member not found");
        }
        if (!bookOpt.isPresent()) {
            throw new IllegalArgumentException("Book not found");
        }

        var book = bookOpt.get();
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available");
        }

        bookService.updateBookAvailability(id, false);
        
        var record = BorrowRecord.builder()
            .member(memberOpt.get())
            .book(book)
            .borrowDate(LocalDateTime.now())
            .build();

        return borrowRecordRepository.save(record);
    }

    public void returnBook(Long memberId, Long id) {
        var record = borrowRecordRepository
            .findByMember_IdAndBook_IdAndReturnDateIsNull(memberId, id)
            .orElseThrow(() -> new IllegalArgumentException("No active borrow record found"));

        var returnedRecord = BorrowRecord.builder()
            .id(record.getId())
            .member(record.getMember())
            .book(record.getBook())
            .borrowDate(record.getBorrowDate())
            .returnDate(LocalDateTime.now())
            .build();

        borrowRecordRepository.save(returnedRecord);
        bookService.updateBookAvailability(id, true);
    }

    public List<BorrowRecord> getBorrowHistory() {
        return borrowRecordRepository.findAllByOrderByBorrowDateDesc();
    }
}

interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    @Query("SELECT br FROM BorrowRecord br WHERE br.member.id = :memberId AND br.book.id = :id AND br.returnDate IS NULL")
    Optional<BorrowRecord> findByMember_IdAndBook_IdAndReturnDateIsNull(@Param("memberId") Long memberId, @Param("id") Long id);

    List<BorrowRecord> findAllByOrderByBorrowDateDesc();
}
