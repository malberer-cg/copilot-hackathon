package com.example.library.service;

import com.example.library.model.Book;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository);
    }

    @Test
    void addBookShouldCreateNewBookWithGivenDetails() {
        // Given
        String title = "Test Book";
        String author = "Test Author";
        String isbn = "1234567890";
        Book expectedBook = Book.builder()
            .isbn(isbn)
            .title(title)
            .author(author)
            .genre(null)
            .available(true)
            .build();
        when(bookRepository.save(any(Book.class))).thenReturn(expectedBook);

        // When
        Book actualBook = bookService.addBook(title, author, isbn);

        // Then
        assertThat(actualBook)
            .isNotNull()
            .extracting("title", "author", "isbn", "available")
            .containsExactly(title, author, isbn, true);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void getBookByIsbnShouldReturnBookWhenExists() {
        // Given
        String isbn = "1234567890";
        Book expectedBook = Book.builder()
            .isbn(isbn)
            .title("Test Book")
            .author("Test Author")
            .available(true)
            .build();
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(expectedBook));

        // When
        Optional<Book> actualBook = bookService.getBookByIsbn(isbn);

        // Then
        assertThat(actualBook).isPresent();
        assertThat(actualBook.get())
            .extracting("isbn", "title", "author", "available")
            .containsExactly(isbn, "Test Book", "Test Author", true);
    }

    @Test
    void getBookByIsbnShouldReturnEmptyWhenNotExists() {
        // Given
        String isbn = "1234567890";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // When
        Optional<Book> actualBook = bookService.getBookByIsbn(isbn);

        // Then
        assertThat(actualBook).isEmpty();
    }

    @Test
    void searchBooksShouldReturnMatchingBooks() {
        // Given
        String query = "Test";        List<Book> expectedBooks = java.util.Arrays.asList(
            Book.builder().title("Test Book 1").author("Author 1").build(),
            Book.builder().title("Book 2").author("Test Author 2").build()
        );
        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query))
            .thenReturn(expectedBooks);

        // When
        List<Book> actualBooks = bookService.searchBooks(query);

        // Then
        assertThat(actualBooks)
            .hasSize(2)
            .extracting("title")
            .containsExactly("Test Book 1", "Book 2");
    }

    @Test
    void updateBookAvailabilityShouldUpdateWhenBookExists() {
        // Given
        Long bookId = 1L;
        Book existingBook = Book.builder()
            .id(bookId)
            .title("Test Book")
            .author("Test Author")
            .isbn("1234567890")
            .available(true)
            .build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        bookService.updateBookAvailability(bookId, false);

        // Then
        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(any(Book.class));
    }
}
