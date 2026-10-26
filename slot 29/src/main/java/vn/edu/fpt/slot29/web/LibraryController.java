// Author: Phan Tuan Thanh
package vn.edu.fpt.slot29.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.slot29.domain.Book;
import vn.edu.fpt.slot29.domain.Student;
import vn.edu.fpt.slot29.service.IBookService;
import vn.edu.fpt.slot29.service.IStudentService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LibraryController {
    private final IStudentService students;
    private final IBookService books;

    public LibraryController(IStudentService students, IBookService books) {
        this.students = students;
        this.books = books;
    }

    @GetMapping("/library")
    @Transactional(readOnly = true)
    public LibraryView library() {
        // Create DTOs inside the transaction; never serialize lazy entity graphs.
        var studentViews = students.getStudents().stream().map(student -> new StudentView(
                student.getId(), student.getEmail(), student.getFirstName(), student.getLastName(),
                student.getGpa(), student.getBooks().stream().sorted(Comparator.comparing(Book::getId))
                        .map(BookView::from).toList())).toList();
        return new LibraryView(studentViews, books.getBooks().stream().map(BookView::from).toList());
    }

    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> createStudent(@Valid @RequestBody StudentInput input) {
        Student student = students.createStudent(new Student(input.email(), input.firstName(), input.lastName(), input.gpa()));
        return Map.of("id", student.getId());
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookView createBook(@Valid @RequestBody BookInput input) {
        return BookView.from(books.createBook(new Book(input.isbn(), input.title(), input.author())));
    }

    @PostMapping("/students/{studentId}/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrow(@PathVariable Long studentId, @PathVariable Long bookId) {
        students.borrowBook(studentId, bookId);
    }

    @DeleteMapping("/students/{studentId}/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable Long studentId, @PathVariable Long bookId) {
        students.returnBook(studentId, bookId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidArgument(IllegalArgumentException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> invalidState(IllegalStateException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> duplicateData() {
        return Map.of("message", "Email hoặc ISBN đã tồn tại, hoặc dữ liệu đang được sử dụng.");
    }

    public record StudentInput(@NotBlank @Email @Size(max = 150) String email,
                               @NotBlank @Size(max = 80) String firstName,
                               @NotBlank @Size(max = 80) String lastName,
                               @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double gpa) { }
    public record BookInput(@NotBlank @Size(max = 30) String isbn,
                            @NotBlank @Size(max = 200) String title,
                            @NotBlank @Size(max = 120) String author) { }
    public record BookView(Long id, String isbn, String title, String author) {
        static BookView from(Book book) {
            return new BookView(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor());
        }
    }
    public record StudentView(Long id, String email, String firstName, String lastName,
                              double gpa, List<BookView> books) { }
    public record LibraryView(List<StudentView> students, List<BookView> books) { }
}
