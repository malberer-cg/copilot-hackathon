package com.example.library.util;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.service.BookService;
import com.example.library.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlElement;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    @Override
    public void run(String... args) throws Exception {
        loadInitialData();
    }
    private final BookService bookService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    public DataLoader(BookService bookService, MemberService memberService, ObjectMapper objectMapper) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.objectMapper = objectMapper;
    }    public void loadInitialData() {
        // Only load initial data if the database is empty
        if (bookService.getAllBooks().isEmpty() && memberService.getAllMembers().isEmpty()) {
            logger.info("Database is empty. Loading initial data...");
            loadBooks();
            loadMembers();
            logger.info("Initial data loading completed.");
        } else {
            logger.info("Database already contains data. Skipping initial data load.");
        }
    }

    private void loadBooks() {
        try {
            Path dataDir = Paths.get(System.getProperty("user.dir"), "data");
            Files.createDirectories(dataDir);
            Path booksFile = dataDir.resolve("books.json");
            List<Book> books = objectMapper.readValue(booksFile.toFile(), 
                new TypeReference<List<Book>>() {});
            books.forEach(bookService::addBook);
            logger.info("Successfully loaded {} books from {}", books.size(), booksFile);
        } catch (Exception e) {
            logger.error("Error loading books: {}", e.getMessage());
        }
    }

    private void loadMembers() {
        try {
            Path dataDir = Paths.get(System.getProperty("user.dir"), "data");
            Files.createDirectories(dataDir);
            Path membersFile = dataDir.resolve("members.xml");
            JAXBContext context = JAXBContext.newInstance(Members.class);
            Members members = (Members) context.createUnmarshaller().unmarshal(membersFile.toFile());
            int memberCount = members.getMembers().size();
            members.getMembers().forEach(member -> 
                memberService.registerMember(member.getName(), member.getEmail(), member.getPhone())
            );
            logger.info("Successfully loaded {} members from {}", memberCount, membersFile);
        } catch (Exception e) {
            logger.error("Error loading members: {}", e.getMessage());
        }
    }
}

@XmlRootElement(name = "members")
@XmlAccessorType(XmlAccessType.FIELD)
class Members {
    @XmlElement(name = "member")
    private List<Member> members;

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
