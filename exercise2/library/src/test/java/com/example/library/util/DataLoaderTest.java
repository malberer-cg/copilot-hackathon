package com.example.library.util;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.service.BookService;
import com.example.library.service.MemberService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @Mock
    private BookService bookService;

    @Mock
    private MemberService memberService;

    @Mock
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader = new DataLoader(bookService, memberService, objectMapper);
        System.setProperty("user.dir", tempDir.toString());
    }

    @Test
    void loadInitialDataShouldLoadBooksAndMembersWhenDatabaseIsEmpty() throws Exception {
        // Given
        when(bookService.getAllBooks()).thenReturn(Arrays.asList());
        when(memberService.getAllMembers()).thenReturn(Arrays.asList());
        
        List<Book> books = Arrays.asList(
            Book.builder().title("Book 1").author("Author 1").isbn("123").build(),
            Book.builder().title("Book 2").author("Author 2").isbn("456").build()
        );
        
        List<Member> members = Arrays.asList(
            Member.builder().name("John").email("john@example.com").phone("123").build(),
            Member.builder().name("Jane").email("jane@example.com").phone("456").build()
        );

        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        Path booksFile = dataDir.resolve("books.json");
        Path membersFile = dataDir.resolve("members.xml");
        
        Files.createFile(booksFile);
        Files.createFile(membersFile);

        when(objectMapper.readValue(any(java.io.File.class), any(TypeReference.class)))
            .thenReturn(books);
        Members membersWrapper = new Members();
        membersWrapper.setMembers(members);
        
        // When
        dataLoader.loadInitialData();

        // Then
        verify(bookService, times(2)).addBook(any(Book.class));
        verify(memberService, times(1)).getAllMembers();
    }

    @Test
    void loadInitialDataShouldSkipLoadingWhenDatabaseNotEmpty() {
        // Given
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(
            Book.builder().title("Existing Book").build()
        ));

        // When
        dataLoader.loadInitialData();

        // Then
        verify(bookService, times(1)).getAllBooks();
        verify(memberService, times(0)).getAllMembers();
        verify(bookService, times(0)).addBook(any(Book.class));
        verify(memberService, times(0)).registerMember(any(), any(), any());
    }

    @Test
    void loadBooksShouldHandleFileNotFoundGracefully() {
        // Given
        when(bookService.getAllBooks()).thenReturn(Arrays.asList());
        when(memberService.getAllMembers()).thenReturn(Arrays.asList());

        // When
        dataLoader.loadInitialData();

        // Then
        verify(bookService, times(0)).addBook(any(Book.class));
    }
}
