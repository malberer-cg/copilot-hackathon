# Java 24 Best Practices Guide

## Code Style & Organization
- Use records for immutable data classes
- Use lombok extensively for boilerplate code reduction
- Leverage pattern matching with switch expressions
- Use sealed classes/interfaces for type hierarchies
- Utilize text blocks for multi-line strings
- Follow standard naming conventions: PascalCase for classes, camelCase for methods/variables
- Do not prefix interfaces with "I"
- Suffix class names with "Impl" for implementation classes of interfaces
- Keep class names descriptive and concise
- Use meaningful variable names
- Always make sure the code is properly formatted
- Use consistent indentation (4 spaces)
- Never put multiple statements on a single line
- Do not put closing curly braces on the same line as the next statement
- Do not leave unused imports

## Modern Language Features
- Use `var` for local variables when type is obvious
- Utilize the enhanced `instanceof` pattern matching
- Apply records for DTOs and value objects
- Implement switch expressions with pattern matching
- Use the new String methods (indent, transform, strip)

## Collections & Streams
- Prefer List.of(), Set.of(), Map.of() for immutable collections
- Use Stream API with structured concurrency
- Leverage the Collections Framework APIs
- Apply Optional to handle nullable values properly
- Do not use Optional for method parameters
- Use the new SequencedCollection interface for ordered collections
- Utilize the Vector API for parallel processing of collections

## Project Structure
- Follow modular architecture with Java Platform Module System (JPMS)
- Organize packages by feature rather than layer
- Keep interfaces in separate files
- User the following project structure outline, the files are just examples:
library/
├── src/main/java/com/example/library/
│   ├── model/
│   │   ├── Book.java
│   │   ├── Member.java
│   │   └── BorrowRecord.java
│   ├── service/
│   │   ├── BookService.java
│   │   ├── MemberService.java
│   │   └── BorrowService.java
│   ├── util/
│   │   ├── DataLoader.java
│   │   └── Logger.java
│   └── Main.java
├── src/test/java/com/example/library/
│   └── service/
│       ├── BookServiceTest.java
│       ├── MemberServiceTest.java
│       ├── BorrowServiceTest.java
│       ├── DataLoaderTest.java
│       └── IntegrationTest.java
├── data/
│   ├── books.json
│   ├── members.xml
│   ├── borrow.sql
│   └── logs.txt
└── README.md


## Error Handling
- Use specific exceptions over generic ones
- Leverage try-with-resources for AutoCloseable resources
- Handle exceptions at appropriate levels
- Document exceptional conditions with @throws

## Performance
- Use Vector API for SIMD operations when applicable
- Apply structured concurrency with virtual threads
- Utilize SequencedCollection interface
- Implement Foreign Function & Memory API for native interop

## Testing
- Write unit tests using JUnit 5
- Implement tests using given/when/then pattern
- Use assertj for fluent assertions
- Apply mockito for mocking dependencies
- Verify code coverage with JaCoCo and PIT
- Aim for 100% line coverage and 75% mutation coverage
- Use parameterized tests for multiple scenarios

## Security
- Follow secure coding guidelines
- Dont use Security Manager
- Implement proper input validation
- Apply secure default configurations

## Documentation
- Write clear Javadoc comments
- Document public APIs thoroughly
- Include code examples in documentation
- Use standardized documentation format
```
