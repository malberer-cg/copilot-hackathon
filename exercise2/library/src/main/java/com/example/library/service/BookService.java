package com.example.library.service;

import com.example.library.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public Book addBook(String title, String author, String isbn) {
        Book book = Book.builder()
            .isbn(isbn)
            .title(title)
            .author(author)
            .genre(null)
            .available(true)
            .build();
        return bookRepository.save(book);
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }    
    
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    public void updateBookAvailability(Long id, boolean available) {
        bookRepository.findById(id).ifPresent(book -> {
            Book updatedBook = Book.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .available(available)
                .build();
            bookRepository.save(updatedBook);
        });
    }
}

interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(@Param("query") String query, @Param("query") String authorQuery);
}
