package com.example.library.console;

import com.example.library.service.BookService;
import com.example.library.service.BorrowService;
import com.example.library.service.MemberService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class LibraryConsole {
    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowService borrowService;
    private final Scanner scanner;

    public LibraryConsole(BookService bookService, MemberService memberService, BorrowService borrowService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.borrowService = borrowService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> handleBookManagement();
                case 2 -> handleMemberManagement();
                case 3 -> handleBorrowingSystem();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n=== Library Management System ===");
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Borrowing System");
        System.out.println("0. Exit");
    }

    private void handleBookManagement() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== Book Management ===");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Books");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> addBook();
                case 2 -> viewAllBooks();
                case 3 -> searchBooks();
                case 0 -> managing = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleMemberManagement() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== Member Management ===");
            System.out.println("1. Register New Member");
            System.out.println("2. View All Members");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> registerMember();
                case 2 -> viewAllMembers();
                case 0 -> managing = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleBorrowingSystem() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== Borrowing System ===");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. View Borrow History");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> borrowBook();
                case 2 -> returnBook();
                case 3 -> viewBorrowHistory();
                case 0 -> managing = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Book Management Methods
    private void addBook() {
        System.out.println("\n=== Add New Book ===");
        String title = getStringInput("Enter book title: ");
        String author = getStringInput("Enter author name: ");
        String isbn = getStringInput("Enter ISBN: ");
        try {
            bookService.addBook(title, author, isbn);
            System.out.println("Book added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    private void viewAllBooks() {
        System.out.println("\n=== All Books ===");
        bookService.getAllBooks().forEach(book ->
                System.out.printf("ISBN: %s | Title: %s | Author: %s%n",
                        book.getIsbn(), book.getTitle(), book.getAuthor()));
    }

    private void searchBooks() {
        System.out.println("\n=== Search Books ===");
        String query = getStringInput("Enter search term (title/author/ISBN): ");
        bookService.searchBooks(query).forEach(book ->
                System.out.printf("ISBN: %s | Title: %s | Author: %s%n",
                        book.getIsbn(), book.getTitle(), book.getAuthor()));
    }

    // Member Management Methods
    private void registerMember() {
        System.out.println("\n=== Register New Member ===");
        String name = getStringInput("Enter member name: ");
        String email = getStringInput("Enter email: ");
        String phone = getStringInput("Enter phone number: ");
        try {
            memberService.registerMember(name, email, phone);
            System.out.println("Member registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }

    private void viewAllMembers() {
        System.out.println("\n=== All Members ===");        memberService.getAllMembers().forEach(member ->
                System.out.printf("ID: %d | Name: %s | Email: %s | Phone: %s%n",
                        member.getId(), member.getName(), member.getEmail(), member.getPhone()));
    }

    private void borrowBook() {
        System.out.println("\n=== Borrow Book ===");
        Long memberId = getLongInput("Enter member ID: ");
        Long id = getLongInput("Enter book ID: ");
        try {
            borrowService.borrowBook(memberId, id);
            System.out.println("Book borrowed successfully!");
        } catch (Exception e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.println("\n=== Return Book ===");
        Long memberId = getLongInput("Enter member ID: ");
        Long id = getLongInput("Enter book ID: ");
        try {
            borrowService.returnBook(memberId, id);
            System.out.println("Book returned successfully!");
        } catch (Exception e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }   
    
    private void viewBorrowHistory() {
        System.out.println("\n=== Borrow History ===");
        borrowService.getBorrowHistory().forEach(record -> {
            String borrowDate = formatDate(record.getBorrowDate());
            String returnDate = record.getReturnDate() != null ? formatDate(record.getReturnDate()) : "Not returned";
            System.out.printf("Member ID: %s | Book ISBN: %s | Borrowed: %s | Returned: %s%n",
                    record.getMember().getId(), record.getBook().getIsbn(),
                    borrowDate, returnDate);
        });
    }

    // Utility Methods
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }    private long getLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
      private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
