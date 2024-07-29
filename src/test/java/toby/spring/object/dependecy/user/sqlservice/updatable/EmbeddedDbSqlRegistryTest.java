package toby.spring.object.dependecy.user.sqlservice.updatable;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
  EmbeddedDatabase db;

  @Override
  protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
    db =
        new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("schema.sql")
            .build();

    EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
    embeddedDbSqlRegistry.setDataSource(db);
    return embeddedDbSqlRegistry;
  }

  @AfterEach
  void tearDown() {
    db.shutdown();
  }
}
