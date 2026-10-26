// Author: Phan Tuan Thanh
package vn.edu.fpt.slot29;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:webtest;MODE=MSSQLServer;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LibraryWebIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void servesPageAndManagesBorrowingWithoutSerializingLazyGraphs() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(forwardedUrl("index.html"));
        mvc.perform(get("/index.html")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
        var student = mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"web@fpt.edu.vn","firstName":"Phan","lastName":"Tuan Thanh","gpa":8.5}
                        """)).andExpect(status().isCreated()).andReturn();
        long studentId = json.readTree(student.getResponse().getContentAsString()).get("id").asLong();
        var book = mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"isbn":"WEB-001","title":"Web Library","author":"FPT"}
                        """)).andExpect(status().isCreated()).andReturn();
        long bookId = json.readTree(book.getResponse().getContentAsString()).get("id").asLong();
        String association = "/api/students/" + studentId + "/books/" + bookId;
        mvc.perform(post(association)).andExpect(status().isNoContent());
        mvc.perform(post(association)).andExpect(status().isConflict());
        mvc.perform(get("/api/library")).andExpect(status().isOk())
                .andExpect(jsonPath("$.students[0].books[0].title").value("Web Library"))
                .andExpect(jsonPath("$.books[0].students").doesNotExist());
        mvc.perform(delete(association)).andExpect(status().isNoContent());
        mvc.perform(get("/api/library")).andExpect(status().isOk())
                .andExpect(jsonPath("$.students[0].books").isEmpty())
                .andExpect(jsonPath("$.books.length()").value(1));
    }

    @Test
    void rejectsInvalidStudentInput() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"invalid","firstName":"","lastName":"Thanh","gpa":11}
                        """)).andExpect(status().isBadRequest());
    }
}
