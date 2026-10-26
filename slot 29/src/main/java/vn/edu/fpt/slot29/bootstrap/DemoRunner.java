// Author: Phan Tuan Thanh
package vn.edu.fpt.slot29.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.slot29.domain.*;
import vn.edu.fpt.slot29.service.*;

@Component
@Profile("demo")
public class DemoRunner implements CommandLineRunner {
    private final IStudentService students;
    private final IBookService books;

    public DemoRunner(IStudentService students, IBookService books) {
        this.students = students;
        this.books = books;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Student student = students.getStudents().stream()
                .filter(existing -> "s29@fu.edu.vn".equals(existing.getEmail()))
                .findFirst().orElseGet(() -> students.createStudent(
                        new Student("s29@fu.edu.vn", "Phan", "Tuan Thanh", 8.0)));
        Book first = findOrCreateBook("978-001", "Hibernate Basics");
        Book second = findOrCreateBook("978-002", "ORM Design");
        Student current = students.getStudent(student.getId());
        java.util.List<Long> missingBookIds = java.util.stream.Stream.of(first, second)
                .filter(book -> current.getBooks().stream()
                        .noneMatch(existing -> existing.getId().equals(book.getId())))
                .map(Book::getId).toList();
        students.borrowBooksAtomically(student.getId(), missingBookIds);
        Student loaded = students.getStudent(student.getId());
        System.out.printf("%s has %d books%n", loaded, loaded.getBooks().size());
        students.findStudentsByBook(first.getId()).forEach(System.out::println);
    }

    private Book findOrCreateBook(String isbn, String title) {
        return books.getBooks().stream().filter(book -> isbn.equals(book.getIsbn()))
                .findFirst().orElseGet(() -> books.createBook(new Book(isbn, title, "FPT")));
    }
}
