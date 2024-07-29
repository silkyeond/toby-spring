package toby.spring.test.spring.embeddeddb;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class EmbeddedDbTest {
  EmbeddedDatabase db;
  NamedParameterJdbcTemplate template;

  @BeforeEach
  void setUp() {
    db =
        new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("schema.sql")
            .addScript("data.sql")
            .build();
    template = new NamedParameterJdbcTemplate(db);
  }

  @AfterEach
  void tearDown() {
    db.shutdown();
  }

  @Test
  void initData() {
    Map<String, String> param = new HashMap<>();
    assertThat(template.queryForObject("SELECT COUNT(*) FROM sqlmap", param, Integer.class))
        .isEqualTo(2);
    List<Map<String, Object>> list =
        template.queryForList("select * from sqlmap order by key_", param);
    assertThat(list.get(0).get("key_")).isEqualTo("KEY1");
    assertThat(list.get(0).get("sql_")).isEqualTo("SQL1");
    assertThat(list.get(1).get("key_")).isEqualTo("KEY2");
    assertThat(list.get(1).get("sql_")).isEqualTo("SQL2");
  }

  @Test
  void insert() {
    Map<String, String> param = new HashMap<>();
    param.put("key_", "KEY3");
    param.put("sql_", "SQL3");
    template.update("insert into sqlmap(key_, sql_) values (:key_,:sql_)", param);

    assertThat(template.queryForObject("select count(*) from sqlmap", param, Integer.class))
        .isEqualTo(3);
  }
}
