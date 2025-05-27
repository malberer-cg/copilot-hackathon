# Library Management System

A Spring Boot-based library management system that demonstrates modern Java development practices with comprehensive test coverage and clean architecture.

## Project Overview

The Library Management System is a robust console application that enables:
- Book management (adding, searching, tracking availability)
- Member management (registration, profile management)
- Borrowing system (book checkout, returns, history tracking)
- Data persistence using SQLite
- Automated data loading from JSON and XML sources

### Key Features
- **Clean Architecture**: Services, models, and repositories are clearly separated
- **Modern Java**: Utilizes Java 24 features and best practices
- **Lombok Integration**: Reduces boilerplate code
- **Comprehensive Testing**: Unit tests and integration tests with high coverage
- **Database Migration**: Liquibase for database version control
- **SQLite Support**: Lightweight database with custom date/time handling

## Setup Instructions

### Prerequisites
- Java 24 or later
- Maven 3.8+
- Git

### Installation

1. Clone the repository (or extract the project files):
```powershell
git clone <repository-url>
cd library
```

2. Build the project:
```powershell
.\mvn clean install
```

3. Run the application:
```powershell
.\mvn spring-boot:run
```

### Running Tests
```powershell
.\mvn test
```

## Github Copilot Prompts Used
The entire chat history can be found in chat.json. The prompts themselves are as follows (note how in the end it failed miserably to extract the prompts while a simple jq worked perfectly ;) ). In between I fixed some errors it failed to correct in an acceptable timeframe myself but tried to make it do as much work as possible (even when i could do it myself faster). The instructions were also initially generated but heavily edited manually.

- "fill these instructions with current modern best practices based on java 24"
- "#codebase Build a console-based Library Management System using Core Java. The system should support book and member management, borrowing functionality, and include unit testing with JUnit 5. Use SQLite for persistent storage. Functional Requirements\r\n1. Book Management\r\n•\tAdd new books (Title, Author, ISBN, Genre, Availability)\r\n•\tView all books\r\n•\tSearch books by title or author\r\n2. Member Management\r\n•\tRegister new members (Name, Email, Member ID)\r\n•\tView all members\r\n3. Borrowing System\r\n•\tBorrow a book (check availability, assign to member)\r\n•\tReturn a book (update availability)\r\n•\tView borrowing history\r\n•\tUse SQLite JDBC driver to connect to a local database file (e.g., library.db)\r\n•\tUse SQL scripts to:\r\n•\tCreate tables: books, members, borrow_records\r\n•\tInsert sample data\r\n•\tUse DataLoader.java to load data from JSON, XML, and SQL\r\nWhen run from the console without parameters the program should show a menu with command for the various functionalities of the program."
- "#codebase create a data directory containing the initialization files required by DataLoader.java"
"make sure the db file is created before liquibase runs"
- "Error creating bean with name 'liquibase' defined in class path resource [org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseConfiguration.class]: Circular depends-on relationship between 'liquibase' and 'entityManagerFactory'   "
"[SQLITE_ERROR] SQL error or missing database (AUTOINCREMENT is only allowed on an INTEGER PRIMARY KEY)"
- "i dont see a menu when running the app"
- "when showing borrowing history i get [SQLITE_ERROR] SQL error or missing database (no such column: br1_0.member_member_id)"
- "now i get this error when showing borrowing history: Unparseable date: \"1748267156594\" does not match (\\p{Nd}++)\\Q-\\E(\\p{Nd}++)\\Q-\\E(\\p{Nd}++)\\Q \\E(\\p{Nd}++)\\Q:\\E(\\p{Nd}++)\\Q:\\E(\\p{Nd}++)\\Q.\\E(\\p{Nd}++)"
- "change all entities to have incrementing long keys"
- "Compilation failure: Compilation failure:\r\n[ERROR] /C:/temp/copilotkurs/copilotdemo/exercise2/library/src/main/java/com/example/library/console/LibraryConsole.java:[154,31] Symbol nicht gefunden\r\n[ERROR]   Symbol: Methode getMemberId()\r\n[ERROR]   Ort: Variable member von Typ com.example.library.model.Member\r\n[ERROR] /C:/temp/copilotkurs/copilotdemo/exercise2/library/src/main/java/com/example/library/console/LibraryConsole.java:[163,38] Inkompatible Typen: java.lang.String kann nicht in java.lang.Long konvertiert werden\r\n[ERROR] /C:/temp/copilotkurs/copilotdemo/exercise2/library/src/main/java/com/example/library/console/LibraryConsole.java:[175,38] Inkompatible Typen: java.lang.String kann nicht in java.lang.Long konvertiert werden\r\n[ERROR] /C:/temp/copilotkurs/copilotdemo/exercise2/library/src/main/java/com/example/library/console/LibraryConsole.java:[186,43] Symbol nicht gefunden\r\n[ERROR]   Symbol: Methode getMemberId()\r\n[ERROR]   Ort: Klasse com.example.library.model.Member\r\n[ERROR] -> [Help 1]"
- "liquibase.exception.ChangeLogParseException: Syntax error in file classpath:db/changelog/db.changelog-master.yaml: mapping values are not allowed here      \r\n in 'reader', line 5, column 22:\r\n            - createTable:"
- "liquibase.exception.ChangeLogParseException: Syntax error in file classpath:db/changelog/db.changelog-master.yaml: mapping values are not allowed here      \r\n in 'reader', line 5, column 22:\r\n            - createTable:"
- "liquibase.exception.ChangeLogParseException: Syntax error in file classpath:db/changelog/db.changelog-master.yaml: mapping values are not allowed here      \r\n in 'reader', line 5, column 22:\r\n            - createTable:"
- "liquibase.exception.DatabaseException: [SQLITE_ERROR] SQL error or missing database (AUTOINCREMENT is only allowed on an INTEGER PRIMARY KEY) [Failed SQL: (1) CREATE TABLE books (id BIGINT CONSTRAINT PK_BOOKS PRIMARY KEY AUTOINCREMENT NOT NULL, isbn VARCHAR(20) NOT NULL, title VARCHAR(255) NOT NULL, author VARCHAR(255) NOT NULL, genre VARCHAR(50), available BOOLEAN DEFAULT 1, UNIQUE (isbn))]"
"when printing the list of borrowing history: java.text.ParseException: Unparseable date: \"1748270167030\" does not match (\\p{Nd}++)\\Q-\\E(\\p{Nd}++)\\Q-\\E(\\p{Nd}++)\\Q \\E(\\p{Nd}++)\\Q:\\E(\\p{Nd}++)\\Q:\\E(\\p{Nd}++)\\Q.\\E(\\p{Nd}++)"
- "#codebase make sure the initial data is only loaded if the db is empty"
- "reduce boilerplate code by utilizing lombok more, e.g. for constructors"
- "Also replace logger initialization with lombok annotations. And check for any other possible use of lombok across the #codebase "
- "#codebase Create complehensive unit tests for every class. Aim for a line coverage of 80%. Testing Requirements\r\nUse JUnit 5 and create modular test classes:\r\nTest Class\tPurpose\r\nBookServiceTest.java\tTest book addition, search, and availability\r\nMemberServiceTest.java\tTest member registration and retrieval\r\nBorrowServiceTest.java\tTest borrow and return logic\r\nDataLoaderTest.java\tValidate data loading from JSON, XML, SQL\r\n"
- "when running the tests in maven the menu shows up and blocks the running test. can you fix that? when manually running from the shell the menu must still show up"
- "add another test: IntegrationTest.java\tEnd-to-end test of book borrowing and returning"
- "#codebase Create A README.md file with:\r\n•\tProject overview\r\n•\tSetup instructions\r\n•\tScreenshots (optional)\r\n•\tGitHub Copilot prompts used\r\n•\tChallenges faced\r\n"
- "be sure to add the actual prompts used by me in this chat"
- "those are still not the prompts used in this chat. only use the real prompts I have used in this chat, nothing else and only edit README.md"   
- "all prompts used in this entire chat, not just the last one"
- "when i say all prompts in this chat i mean the entire history of everything i prompted today. put that in the README.md"

## Implementation Challenges & Solutions

1. **SQLite DateTime Handling**
   - Challenge: SQLite doesn't natively support DateTime types
   - Solution: Implemented custom `SQLiteLocalDateTimeConverter` to handle timestamp conversions

2. **Test Data Management**
   - Challenge: Tests were interfering with production data
   - Solution: Created separate test configuration with in-memory database

3. **Console Menu in Tests**
   - Challenge: Console menu was blocking automated tests
   - Solution: Implemented profile-based configuration to disable menu during tests

4. **Initial Data Loading**
   - Challenge: Needed to load initial data only when database is empty
   - Solution: Added checks in `DataLoader` to verify database state before loading

## Architecture

```
com.example.library/
├── model/         # Domain entities
├── service/       # Business logic
├── repository/    # Data access
├── config/        # Configuration classes
├── console/       # UI layer
└── util/          # Utilities and helpers
```

## Test Coverage

Current test coverage: >80% line coverage

Key test areas:
- Unit tests for all service classes
- Integration tests for complete workflows
- Data loading and conversion tests
- Error handling and edge cases
