// Author: Phan Tuan Thanh
package vn.edu.fpt.slot29;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import vn.edu.fpt.slot29.bootstrap.DemoRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:demotest;MODE=MSSQLServer;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=true")
@ActiveProfiles("demo")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DemoRunnerIntegrationTest {
    @Autowired DemoRunner runner;
    @Autowired JdbcTemplate jdbc;

    @Test
    void restartingDemoReusesDataAndRestoresMissingAssociation() {
        runner.run();
        assertCounts();
        jdbc.update("delete from STUDENTS_BOOKS");
        runner.run();
        assertCounts();
    }

    private void assertCounts() {
        assertThat(jdbc.queryForObject("select count(*) from STUDENTS", Long.class)).isEqualTo(1L);
        assertThat(jdbc.queryForObject("select count(*) from BOOKS", Long.class)).isEqualTo(2L);
        assertThat(jdbc.queryForObject("select count(*) from STUDENTS_BOOKS", Long.class)).isEqualTo(2L);
    }
}
